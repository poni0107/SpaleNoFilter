package repository

import (
	"database/sql"
)

type FollowRepository struct {
	DB *sql.DB
}

func (r *FollowRepository) Follow(userID, followedID int) error {
	_, err := r.DB.Exec(
		"INSERT INTO user_follows (user_id, followed_user_id) VALUES (?, ?)",
		userID, followedID,
	)
	return err
}

func (r *FollowRepository) Unfollow(userID, followedID int) error {
	_, err := r.DB.Exec(
		"DELETE FROM user_follows WHERE user_id = ? AND followed_user_id = ?",
		userID, followedID,
	)
	return err
}

func (r *FollowRepository) Followers(userID int) ([]int, error) {
	rows, err := r.DB.Query(
		"SELECT user_id FROM user_follows WHERE followed_user_id = ?",
		userID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []int
	for rows.Next() {
		var id int
		rows.Scan(&id)
		result = append(result, id)
	}
	return result, nil
}

func (r *FollowRepository) GetFollowing(userID int64) ([]int64, error) {
	rows, err := r.DB.Query(`
		SELECT followed_user_id
		FROM user_follows
		WHERE user_id = ?
	`, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []int64
	for rows.Next() {
		var id int64
		if err := rows.Scan(&id); err != nil {
			return nil, err
		}
		result = append(result, id)
	}

	return result, nil
}

func (r *FollowRepository) GetFollowers(userID int64) ([]int64, error) {
	rows, err := r.DB.Query(
		"SELECT user_id FROM user_follows WHERE followed_user_id = ?",
		userID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []int64
	for rows.Next() {
		var id int64
		if err := rows.Scan(&id); err != nil {
			return nil, err
		}
		result = append(result, id)
	}

	return result, nil
}
