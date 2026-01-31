package service

import (
	"context"
	"user_follow_service/domain"
)

func (s *FollowService) validateUser(ctx context.Context, userID int) error {
	ok, err := s.users.Exists(ctx, userID)
	if err != nil {
		return err
	}
	if !ok {
		return domain.ErrUserNotFound
	}
	return nil
}
