package service

import (
	"context"
	"fmt"
	"user_service/domain"

	"golang.org/x/crypto/bcrypt"
)

type UserRepository interface {
	Create(string, string, string) (int, error)
	Exists(int) (bool, error)
	GetByID(int) (*domain.User, error)
	Delete(int) error
	GetByUsername(username string) (*domain.User, error)
}

type UserService struct {
	repo UserRepository
}

func NewUserService(r UserRepository) *UserService {
	return &UserService{repo: r}
}

func (s *UserService) CreateUser(
	ctx context.Context,
	username, email, passwordHash string,
) (int, error) {
	return s.repo.Create(username, email, passwordHash)
}

func (s *UserService) Exists(
	ctx context.Context,
	userID int,
) (bool, error) {
	return s.repo.Exists(userID)
}

func (s *UserService) GetUser(
	ctx context.Context,
	userID int,
) (*domain.User, error) {
	return s.repo.GetByID(userID)
}

func (s *UserService) DeleteUser(
	ctx context.Context,
	userID int,
) error {
	return s.repo.Delete(userID)
}
func (s *UserService) GetUserByID(id uint) (*domain.User, error) {
	return s.repo.GetByID(int(id))
}

func (s *UserService) GetByCredentials(username, password string) (*domain.User, error) {
	user, err := s.repo.GetByUsername(username)
	if err != nil {
		return nil, err
	}
	if user == nil {
		return nil, fmt.Errorf("invalid credentials")
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(password)); err != nil {
		return nil, fmt.Errorf("invalid credentials")
	}

	return user, nil
}
