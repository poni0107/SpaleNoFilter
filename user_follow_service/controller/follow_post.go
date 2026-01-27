package controller

import (
	"net/http"
	"strconv"
	"user_follow_service/view"
)

func (c *FollowController) Follow(w http.ResponseWriter, r *http.Request) {
	userID, _ := strconv.Atoi(r.URL.Query().Get("user_id"))
	followedID, _ := strconv.Atoi(r.URL.Query().Get("followed_id"))

	err := c.Repo.Follow(userID, followedID)
	if err != nil {
		view.Error(w, err.Error(), "error", http.StatusConflict)
		return
	}
	view.Success(w, "followed")
}
