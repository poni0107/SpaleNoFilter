package routes

import (
	"net/http"
	"user_follow_service/controller"
)

func Register(ctrl *controller.FollowController) {
	http.HandleFunc("/follow", ctrl.Follow)
	http.HandleFunc("/unfollow", ctrl.Unfollow)
	http.HandleFunc("/get-following", ctrl.GetFollowing)
	http.HandleFunc("/get-followers", ctrl.GetFollowers)
}
