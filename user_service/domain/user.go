package domain

import "time"

type User struct {
	ID             int
	Username       string
	Email          string
	PasswordHash   string
	FullName       string
	Bio            string
	ProfilePicture string
	IsPrivate      bool
	IsActive       bool
	CreatedAt      time.Time
}
