package handler

type BlockRequest struct {
	BlockerID int `json:"blocker_id"`
	BlockedID int `json:"blocked_id"`
}

type UnblockRequest struct {
	BlockerID int `json:"blocker_id"`
	BlockedID int `json:"blocked_id"`
}
