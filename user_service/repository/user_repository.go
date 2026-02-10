package repository

import (
	"database/sql"
	"user_service/domain"
)

type UserRepository struct {
	db *sql.DB
}

func NewUserRepository(db *sql.DB) *UserRepository {
	return &UserRepository{db: db}
}

func (r *UserRepository) Create(username, email, passwordHash string) (int, error) {
	res, err := r.db.Exec(`
		INSERT INTO users (user_name, email, password_hash)
		VALUES (?, ?, ?)
	`, username, email, passwordHash)
	if err != nil {
		return 0, err
	}

	id, _ := res.LastInsertId()
	return int(id), nil
}

func (r *UserRepository) Exists(userID int) (bool, error) {
	var id int
	err := r.db.QueryRow(
		"SELECT id FROM users WHERE id = ? AND is_active = TRUE",
		userID,
	).Scan(&id)

	if err == sql.ErrNoRows {
		return false, nil
	}
	return err == nil, err
}

func (r *UserRepository) GetByID(userID int) (*domain.User, error) {
	var (
		fullName       sql.NullString
		bio            sql.NullString
		profilePicture sql.NullString
	)

	var u domain.User
	err := r.db.QueryRow(`
		SELECT id, user_name, email, full_name, bio, profile_picture,
		       is_private, is_active, created_at
		FROM users WHERE id = ?
	`, userID).Scan(
		&u.ID,
		&u.Username,
		&u.Email,
		&fullName,
		&bio,
		&profilePicture,
		&u.IsPrivate,
		&u.IsActive,
		&u.CreatedAt,
	)

	if err == sql.ErrNoRows {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}

	// Map NULL-safe values
	u.FullName = fullName.String
	u.Bio = bio.String
	u.ProfilePicture = profilePicture.String

	return &u, nil
}

func (r *UserRepository) Delete(userID int) error {
	_, err := r.db.Exec(
		"UPDATE users SET is_active = FALSE WHERE id = ?",
		userID,
	)
	return err
}

func (r *UserRepository) GetByUsername(username string) (*domain.User, error) {
	var u domain.User
	err := r.db.QueryRow(`
        SELECT id, user_name, email, password_hash
        FROM users
        WHERE user_name = ? AND is_active = TRUE
    `, username).Scan(&u.ID, &u.Username, &u.Email, &u.PasswordHash)

	if err != nil {
		if err == sql.ErrNoRows {
			return nil, nil
		}
		return nil, err
	}

	return &u, nil
}
