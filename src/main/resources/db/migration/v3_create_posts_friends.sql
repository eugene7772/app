CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    text VARCHAR,
    author_user_id UUID,
    created_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE friendship (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    friend_id UUID,

    CONSTRAINT fk_user_friends
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);