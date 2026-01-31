package repository

func (r *FollowRepository) GetFollowing(userID int) ([]int, error) {
	rows, err := r.db.Query(
		"SELECT followed_user_id FROM user_follows WHERE user_id = ?",
		userID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var res []int
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			return nil, err
		}
		res = append(res, id)
	}
	return res, nil
}
