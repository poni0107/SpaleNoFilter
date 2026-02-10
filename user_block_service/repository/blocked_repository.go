package repository

import "database/sql"

type BlockedRepository struct {
	db *sql.DB
}

func NewBlockedRepository(db *sql.DB) *BlockedRepository {
	return &BlockedRepository{db: db}
}

func (r *BlockedRepository) Block(userID, blockedID int) error {
	_, err := r.db.Exec(
		"INSERT INTO blocked_users (user_id, blocked_user_id) VALUES (?, ?)",
		userID, blockedID,
	)
	return err
}

func (r *BlockedRepository) Unblock(userID, blockedID int) error {
	_, err := r.db.Exec(
		"DELETE FROM blocked_users WHERE user_id = ? AND blocked_user_id = ?",
		userID, blockedID,
	)
	return err
}

func (r *BlockedRepository) GetBlocked(userID int) ([]int, error) {
	rows, err := r.db.Query(
		"SELECT blocked_user_id FROM blocked_users WHERE user_id = ?",
		userID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []int
	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			return nil, err
		}
		result = append(result, id)
	}
	return result, nil
}
