CREATE TABLE posts (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   text VARCHAR,
   author_user_id UUID
);