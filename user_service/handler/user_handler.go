package handler

import (
	"context"
	"encoding/json"
	"net/http"
	"strconv"
	"user_service/domain"
	"user_service/middleware"
)

type UserService interface {
	CreateUser(context.Context, string, string, string) (int, error)
	Exists(context.Context, int) (bool, error)
	GetUser(context.Context, int) (*domain.User, error)
	DeleteUser(context.Context, int) error
	GetUserByID(id uint) (*domain.User, error)
	GetByCredentials(username, password string) (*domain.User, error)
}

type UserController struct {
	service UserService
}
type UserAuthResponse struct {
	UserID   uint   `json:"user_id"`
	Username string `json:"username"`
}

func NewUserController(s UserService) *UserController {
	return &UserController{service: s}
}

func (uc *UserController) Exists(w http.ResponseWriter, r *http.Request) {
	idStr := r.URL.Query().Get("id")
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, "invalid id", http.StatusBadRequest)
		return
	}

	ok, err := uc.service.Exists(r.Context(), id)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	json.NewEncoder(w).Encode(map[string]bool{
		"exists": ok,
	})
}

func (uc *UserController) CreateUser(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}

	var req CreateUserRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "invalid request", http.StatusBadRequest)
		return
	}

	id, err := uc.service.CreateUser(r.Context(), req.Username, req.Email, req.Password)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]int{"id": id})
}

func (c *UserController) GetUser(w http.ResponseWriter, r *http.Request) {
	claims, ok := middleware.GetClaims(r.Context())
	if !ok {
		http.Error(w, "unauthorized", http.StatusUnauthorized)
		return
	}

	requestedIDStr := r.URL.Query().Get("id")
	if requestedIDStr == "" {
		http.Error(w, "missing id", http.StatusBadRequest)
		return
	}

	requestedID, err := strconv.ParseUint(requestedIDStr, 10, 64)
	if err != nil {
		http.Error(w, "invalid id", http.StatusBadRequest)
		return
	}

	if claims.UserId != uint(requestedID) {
		http.Error(w, "forbidden", http.StatusForbidden)
		return
	}

	user, err := c.service.GetUserByID(uint(requestedID))
	if err != nil || user == nil {
		http.Error(w, "not found", http.StatusNotFound)
		return
	}

	json.NewEncoder(w).Encode(user)
}

func (uc *UserController) GetMe(w http.ResponseWriter, r *http.Request) {
	claims, ok := middleware.GetClaims(r.Context())
	if !ok {
		http.Error(w, "unauthorized", http.StatusUnauthorized)
		return
	}

	user, err := uc.service.GetUserByID(claims.UserId)
	if err != nil || user == nil {
		http.Error(w, "not found", http.StatusNotFound)
		return
	}

	json.NewEncoder(w).Encode(user)
}

func (uc *UserController) DeleteUser(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}

	idStr := r.URL.Query().Get("id")
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, "invalid id", http.StatusBadRequest)
		return
	}

	if err := uc.service.DeleteUser(r.Context(), id); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}

func (uc *UserController) GetUserByCredentials(w http.ResponseWriter, r *http.Request) {
	username := r.URL.Query().Get("username")
	password := r.URL.Query().Get("password")

	if username == "" || password == "" {
		http.Error(w, "missing username or password", http.StatusBadRequest)
		return
	}

	user, err := uc.service.GetByCredentials(username, password)
	if err != nil {
		http.Error(w, "invalid credentials", http.StatusUnauthorized)
		return
	}

	json.NewEncoder(w).Encode(UserAuthResponse{
		UserID:   uint(user.ID),
		Username: user.Username,
	})
}
