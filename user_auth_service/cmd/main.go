package main

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"net/url"
	"os"
	"strconv"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

type Claims struct {
	Id       uint   `json:"user_id"`
	Username string `json:"username"`
	jwt.RegisteredClaims
}

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}
type UserAuthResponse struct {
	UserID   uint   `json:"user_id"`
	Username string `json:"username"`
}
type CreateUserResponse struct {
	AccessToken string `json:"access_token"`
}

var jwtSecret = []byte(os.Getenv("JWT_SECRET"))

func loginHandler(w http.ResponseWriter, r *http.Request) {
	jwtSecret := []byte("super_long_random_secret_32_bytes_minimum")

	if len(jwtSecret) < 32 {
		fmt.Println(jwtSecret)
		log.Fatal("JWT_SECRET must be at least 32 bytes")
	}
	var req LoginRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "invalid body", http.StatusBadRequest)
		return
	}

	client := &http.Client{
		Timeout: 10 * time.Second,
	}
	q := url.Values{}
	q.Add("username", req.Username)
	q.Add("password", req.Password)

	url := "http://localhost:9001/users/auth?" + q.Encode()
	userAPIReq, err := http.NewRequest("GET", url, nil)
	if err != nil {
		fmt.Println("Error creating request:", err)
		return
	}

	userAPIReq.Header.Set("Accept", "application/json")

	resp, err := client.Do(userAPIReq)
	if err != nil {
		http.Error(w, "user service unavailable", http.StatusBadGateway)
		return
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		log.Printf("user-service error: %d %s", resp.StatusCode, string(body))

		if resp.StatusCode == http.StatusUnauthorized {
			http.Error(w, "invalid credentials", http.StatusUnauthorized)
			return
		}

		http.Error(w, "user service error", http.StatusBadGateway)
		return
	}

	var userResp UserAuthResponse
	if err := json.NewDecoder(resp.Body).Decode(&userResp); err != nil {
		http.Error(w, "invalid user service response", http.StatusInternalServerError)
		return
	}

	userID := userResp.UserID

	claims := Claims{
		Id:       userID,
		Username: userResp.Username,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(15 * time.Minute)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
			Issuer:    "auth-service",
			Subject:   strconv.Itoa(int(userID)),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	signed, err := token.SignedString(jwtSecret)
	if err != nil {
		http.Error(w, "token error", http.StatusInternalServerError)
		return
	}

	json.NewEncoder(w).Encode(CreateUserResponse{
		AccessToken: signed,
	})

}

func main() {

	http.HandleFunc("/login", loginHandler)

	log.Println("Auth service running on :8500")
	log.Fatal(http.ListenAndServe(":8500", nil))
}
