CREATE TABLE IF NOT EXISTS blocked_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    blocked_user_id INT NOT NULL,
    UNIQUE (user_id, blocked_user_id),
    CHECK (user_id <> blocked_user_id)
);
