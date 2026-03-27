CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    text VARCHAR,
    author_user_id UUID
);

CREATE TABLE friendship (
    user_id UUID PRIMARY KEY,
    friend_id UUID,

    CONSTRAINT fk_user_friends
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);