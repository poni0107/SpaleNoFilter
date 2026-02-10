package domain

import "errors"

var (
	ErrUserNotFound   = errors.New("user not found")
	ErrSelfBlock      = errors.New("cannot block yourself")
	ErrAlreadyBlocked = errors.New("user already blocked")
)
