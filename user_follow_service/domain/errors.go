package domain

import "errors"

var (
	ErrUserNotFound     = errors.New("user not found")
	ErrAlreadyFollowing = errors.New("already following")
	ErrSelfFollow       = errors.New("cannot follow yourself")
)
