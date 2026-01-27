CREATE TABLE IF NOT EXISTS user_follows (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    followed_user_id INT NOT NULL,
    UNIQUE (user_id, followed_user_id),
    CHECK (user_id <> followed_user_id)
);
