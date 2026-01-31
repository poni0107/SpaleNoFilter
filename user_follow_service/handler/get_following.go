package handler

import (
	"net/http"
	"strconv"
)

func (c *FollowController) GetFollowing(w http.ResponseWriter, r *http.Request) {
	userID, _ := strconv.Atoi(r.URL.Query().Get("user_id"))
	data, err := c.service.GetFollowing(r.Context(), userID)
	if err != nil {
		mapError(w, err)
		return
	}
	writeJSON(w, http.StatusOK, data)
}
