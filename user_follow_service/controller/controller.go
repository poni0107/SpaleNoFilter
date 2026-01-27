package controller

import (
	"user_follow_service/repository"
)

type FollowController struct {
	Repo *repository.FollowRepository
}
