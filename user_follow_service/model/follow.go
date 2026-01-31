package model

type Follow struct {
	ID             int `json:"id"`
	UserID         int `json:"userId"`
	FollowedUserID int `json:"followedUserId"`
}
