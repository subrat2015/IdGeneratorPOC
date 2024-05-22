-- Create the database if it does not exist
CREATE DATABASE IF NOT EXISTS flickr_id_store;

-- Select the newly created database
USE flickr_id_store;

-- Create a table if it does not exist
CREATE TABLE IF NOT EXISTS id_store (
    id INT NOT NULL PRIMARY KEY,
    dummy_name VARCHAR(100) UNIQUE
);

-- Insert an initial record into the table
INSERT INTO id_store (id, dummy_name)
VALUES (0, 'a');