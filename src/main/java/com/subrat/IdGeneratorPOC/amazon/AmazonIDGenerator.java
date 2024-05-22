package com.subrat.IdGeneratorPOC.amazon;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AmazonIDGenerator {

    private final DataSource dataSource;
    private final Integer rangeSize;

    public AmazonIDGenerator(DataSource dataSource, Integer rangeSize) {
        this.dataSource = dataSource;
        this.rangeSize = rangeSize;
    }

    public Range getIDRange(String serviceName) {
        Connection connection = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Prepare and execute the SELECT statement with exclusive lock
            String selectSQL = "SELECT id FROM id_store WHERE service_name = ? FOR UPDATE";
            selectStmt = connection.prepareStatement(selectSQL);
            selectStmt.setString(1, serviceName);
            resultSet = selectStmt.executeQuery();

            // Check if the row exists and fetch the current id
            if (!resultSet.next()) {
                connection.rollback(); // Rollback if the service name is not found
                return null; // Return false indicating the operation failed
            }
            int currentID = resultSet.getInt("id");

            // Prepare and execute the UPDATE statement
            String updateSQL = "UPDATE id_store SET id = ? WHERE service_name = ?";
            updateStmt = connection.prepareStatement(updateSQL);
            updateStmt.setInt(1, currentID + rangeSize);
            updateStmt.setString(2, serviceName);
            int affectedRows = updateStmt.executeUpdate();

            if (affectedRows > 0) {
                connection.commit(); // Commit the transaction to finalize changes and release the lock
                return new Range(currentID, currentID + rangeSize, serviceName);
            } else {
                connection.rollback(); // Rollback if the update failed
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) connection.rollback(); // Rollback in case of exception
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (selectStmt != null) selectStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}