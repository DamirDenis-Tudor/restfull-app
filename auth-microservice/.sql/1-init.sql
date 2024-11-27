CREATE TABLE blacklist_tokens
(
    id        CHAR(36) PRIMARY KEY,
    token     VARCHAR(255) NOT NULL,
    timestamp DATETIME     NOT NULL
);


-- Create Users Table
CREATE TABLE IF NOT EXISTS users
(
    id       CHAR(36) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);