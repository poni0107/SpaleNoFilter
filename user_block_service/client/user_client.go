package client

import (
	"context"
	"fmt"
	"net/http"
)

type UserClient struct {
	baseURL string
	client  *http.Client
}

func NewUserClient(baseURL string, client *http.Client) *UserClient {
	return &UserClient{baseURL: baseURL, client: client}
}

func (c *UserClient) Exists(ctx context.Context, userID int) (bool, error) {
	req, err := http.NewRequestWithContext(
		ctx,
		http.MethodGet,
		fmt.Sprintf("%s/users/%d", c.baseURL, userID),
		nil,
	)
	if err != nil {
		return false, err
	}

	resp, err := c.client.Do(req)
	if err != nil {
		return false, err
	}
	defer resp.Body.Close()

	switch resp.StatusCode {
	case http.StatusOK:
		return true, nil
	case http.StatusNotFound:
		return false, nil
	default:
		return false, fmt.Errorf("user service error: %d", resp.StatusCode)
	}
}
