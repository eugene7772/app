CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    login VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE user_info (
    user_id UUID PRIMARY KEY,
    first_name VARCHAR(100),
    second_name VARCHAR(100),
    birthdate DATE,
    biography TEXT,
    city VARCHAR(50),

    CONSTRAINT fk_user_info_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);