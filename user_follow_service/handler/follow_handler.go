package handler

import (
	"context"
)

type FollowService interface {
	Follow(ctx context.Context, userID, followedID int) error
	Unfollow(ctx context.Context, userID, followedID int) error
	GetFollowers(ctx context.Context, userID int) ([]int, error)
	GetFollowing(ctx context.Context, userID int) ([]int, error)
}

type FollowController struct {
	service FollowService
}

func NewFollowController(s FollowService) *FollowController {
	return &FollowController{service: s}
}
