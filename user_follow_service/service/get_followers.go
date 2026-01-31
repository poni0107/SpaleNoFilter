package service

import "context"

func (s *FollowService) GetFollowers(ctx context.Context, userID int) ([]int, error) {
	if err := s.validateUser(ctx, userID); err != nil {
		return nil, err
	}

	return s.repo.GetFollowers(userID)
}
