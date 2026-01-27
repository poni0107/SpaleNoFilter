package model

type Follow struct {
	ID             int `json:"id"`
	UserID         int `json:"user_id"`
	FollowedUserID int `json:"followed_user_id"`
}
