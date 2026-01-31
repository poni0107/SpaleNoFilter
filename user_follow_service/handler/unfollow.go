package handler

import (
	"net/http"
	"strconv"
)

func (c *FollowController) Unfollow(w http.ResponseWriter, r *http.Request) {
	userID, _ := strconv.Atoi(r.URL.Query().Get("user_id"))
	followedID, _ := strconv.Atoi(r.URL.Query().Get("followed_id"))

	err := c.service.Unfollow(r.Context(), userID, followedID)
	if err != nil {
		mapError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
}
