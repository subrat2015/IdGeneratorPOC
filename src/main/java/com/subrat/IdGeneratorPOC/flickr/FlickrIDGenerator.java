package com.subrat.IdGeneratorPOC.flickr;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlickrIDGenerator {
    private Integer count;
    private final DataSource evenDataSource;
    private final DataSource oddDataSource;

    public FlickrIDGenerator(Integer count, DataSource evenDataSource, DataSource oddDataSource) {
        this.count = count;
        this.evenDataSource = evenDataSource;
        this.oddDataSource = oddDataSource;
    }

    public Integer getId() {
        synchronized ("lock") {
            count = count + 1;
        }
        if (count % 2 == 0) {
            return getId(evenDataSource, "a");
        } else {
            return getId(oddDataSource, "b");
        }
    }

    private Integer getId(DataSource dataSource, String dummyValue) {
        String selectSQL = "SELECT id FROM id_store WHERE dummy_name = ? FOR UPDATE";
        String updateSQL = "INSERT INTO id_store (dummy_name, id) VALUES (?, 0) ON DUPLICATE KEY UPDATE id = id + 2";
        Connection conn = null;
        try  {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
            // Select statement to get the updated ID
            selectStmt.setString(1, dummyValue);

            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int updatedId = rs.getInt("id");
                PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                // Update statement
                updateStmt.setString(1, dummyValue);
                updateStmt.executeUpdate();
                conn.commit();
                return updatedId;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.out.println("rollbacking ....");
                    conn.rollback();  // Ensure rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if(conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }


}
