-- Create the database if it does not exist
CREATE DATABASE IF NOT EXISTS snowflake_id_store;

-- Select the newly created database
USE snowflake_id_store;

-- Create a table if it does not exist
CREATE TABLE IF NOT EXISTS sequence_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT NOT NULL PRIMARY KEY,
    post VARCHAR(100)
);



-- Create the stored procedure to generate unique IDs
DELIMITER //

CREATE PROCEDURE generate_unique_id(
    IN machineId BIGINT,
    OUT unique_id BIGINT
)
BEGIN
    DECLARE epochTimeStamp BIGINT;
    DECLARE sequenceNumber BIGINT;
    DECLARE mask BIGINT;

    -- Get current timestamp in milliseconds
    SET epochTimeStamp = UNIX_TIMESTAMP(NOW()); -- Milliseconds precision

    SET mask = 549755813887; -- This is equivalent to 0x7FFFFFFFFFL

    -- Set first 41 bits
    SET epochTimeStamp = epochTimeStamp << 23;
    SET epochTimeStamp = epochTimeStamp & mask;  -- Ensure sign bit is 0

    -- Set next 10 bits
    SET machineId = machineId << 13;

    -- Insert a new record to get an auto-incremented sequence number
    INSERT INTO sequence_table () VALUES ();
    SET sequenceNumber = LAST_INSERT_ID() % 10000; -- Limit the sequence number to 10,000

    -- Combine values to generate unique ID
    SET unique_id = epochTimeStamp | machineId | sequenceNumber;
END //

DELIMITER ;