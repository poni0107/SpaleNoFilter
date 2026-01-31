package service

import (
	"context"
)

type FollowRepository interface {
	Follow(int, int) error
	Unfollow(int, int) error
	GetFollowers(int) ([]int, error)
	GetFollowing(int) ([]int, error)
}

type UserClient interface {
	Exists(context.Context, int) (bool, error)
}

type FollowService struct {
	repo  FollowRepository
	users UserClient
}

func NewFollowService(r FollowRepository, u UserClient) *FollowService {
	return &FollowService{repo: r, users: u}
}
