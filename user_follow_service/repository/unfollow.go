package repository

func (r *FollowRepository) Unfollow(userID, followedID int) error {
	_, err := r.db.Exec(
		"DELETE FROM user_follows WHERE user_id = ? AND followed_user_id = ?",
		userID, followedID,
	)
	return err
}
