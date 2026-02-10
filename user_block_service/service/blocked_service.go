package service

import (
	"context"
	"user_block_service/domain"
)

type BlockedRepository interface {
	Block(int, int) error
	Unblock(int, int) error
	GetBlocked(int) ([]int, error)
}

type UserClient interface {
	Exists(context.Context, int) (bool, error)
}

type BlockedService struct {
	repo  BlockedRepository
	users UserClient
}

func NewBlockedService(r BlockedRepository, u UserClient) *BlockedService {
	return &BlockedService{repo: r, users: u}
}

func (s *BlockedService) validateUser(ctx context.Context, userID int) error {
	ok, err := s.users.Exists(ctx, userID)
	if err != nil {
		return err
	}
	if !ok {
		return domain.ErrUserNotFound
	}
	return nil
}

func (s *BlockedService) Block(ctx context.Context, userID, blockedID int) error {
	if userID == blockedID {
		return domain.ErrSelfBlock
	}

	if err := s.validateUser(ctx, userID); err != nil {
		return err
	}
	if err := s.validateUser(ctx, blockedID); err != nil {
		return err
	}

	return s.repo.Block(userID, blockedID)
}

func (s *BlockedService) Unblock(ctx context.Context, userID, blockedID int) error {
	if err := s.validateUser(ctx, userID); err != nil {
		return err
	}
	if err := s.validateUser(ctx, blockedID); err != nil {
		return err
	}

	return s.repo.Unblock(userID, blockedID)
}

func (s *BlockedService) GetBlocked(ctx context.Context, userID int) ([]int, error) {
	if err := s.validateUser(ctx, userID); err != nil {
		return nil, err
	}

	return s.repo.GetBlocked(userID)
}
