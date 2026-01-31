package repository

func (r *FollowRepository) Follow(userID, followedID int) error {
	_, err := r.db.Exec(
		"INSERT INTO user_follows (user_id, followed_user_id) VALUES (?, ?)",
		userID, followedID,
	)
	return err
}
