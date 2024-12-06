CREATE TABLE blacklist
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    token     VARCHAR(255) NOT NULL,
    timestamp DATETIME     NOT NULL
);


CREATE TABLE IF NOT EXISTS users
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL
);

