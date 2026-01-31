package service

import (
	"context"
	"user_follow_service/domain"
)

func (s *FollowService) Unfollow(ctx context.Context, userID, followedID int) error {
	if userID == followedID {
		return domain.ErrSelfFollow
	}

	if err := s.validateUser(ctx, userID); err != nil {
		return err
	}
	if err := s.validateUser(ctx, followedID); err != nil {
		return err
	}

	return s.repo.Unfollow(userID, followedID)
}
