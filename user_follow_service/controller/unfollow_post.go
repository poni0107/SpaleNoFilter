package controller

import (
	"net/http"
	"strconv"
	"user_follow_service/view"
)

func (c *FollowController) Unfollow(w http.ResponseWriter, r *http.Request) {
	userID, _ := strconv.Atoi(r.URL.Query().Get("user_id"))
	followedID, _ := strconv.Atoi(r.URL.Query().Get("followed_id"))

	err := c.Repo.Unfollow(userID, followedID)
	if err != nil {
		view.Error(w, err.Error(), "error", http.StatusInternalServerError)
		return
	}
	view.Success(w, "unfollowed")
}
