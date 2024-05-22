-- Create the database if it does not exist
CREATE DATABASE IF NOT EXISTS amazon_id_store;

-- Select the newly created database
USE amazon_id_store;

-- Create a table if it does not exist
CREATE TABLE IF NOT EXISTS id_store (
    service_name VARCHAR(100) PRIMARY KEY,
    id INT NOT NULL
);

-- Insert an initial record into the table
INSERT INTO id_store (service_name, id)
VALUES ('order_service', 0);
INSERT INTO id_store (service_name, id)
VALUES ('payment_service', 0);