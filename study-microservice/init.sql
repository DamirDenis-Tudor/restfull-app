-- Create a new database if it doesn't exist (optional)
CREATE DATABASE IF NOT EXISTS `study-database`;

-- Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON `study-database`.* TO 'user'@'%' WITH GRANT OPTION;

-- Flush privileges to ensure that the changes take effect
FLUSH PRIVILEGES;
