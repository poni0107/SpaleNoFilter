package handler

import (
	"net/http"
	"strconv"
)

func (c *FollowController) Follow(w http.ResponseWriter, r *http.Request) {
	userID, _ := strconv.Atoi(r.URL.Query().Get("user_id"))
	followedID, _ := strconv.Atoi(r.URL.Query().Get("followed_id"))

	err := c.service.Follow(r.Context(), userID, followedID)
	if err != nil {
		mapError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
}
