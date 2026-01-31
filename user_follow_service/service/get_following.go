package service

import "context"

func (s *FollowService) GetFollowing(ctx context.Context, userID int) ([]int, error) {
	if err := s.validateUser(ctx, userID); err != nil {
		return nil, err
	}

	return s.repo.GetFollowing(userID)
}
