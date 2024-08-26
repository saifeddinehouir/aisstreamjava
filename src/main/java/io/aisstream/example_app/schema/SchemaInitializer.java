package io.aisstream.example_app.schema;

import io.aisstream.example_app.db.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SchemaInitializer {

    public static void createOrUpdateSchema() {
        String createVesselTable = "CREATE TABLE IF NOT EXISTS vessel (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "call_sign VARCHAR(50), " +
                "destination VARCHAR(255), " +
                "dimensions VARCHAR(255)" +
                ")";

        String createCurrentPositionTable = "CREATE TABLE IF NOT EXISTS current_position (" +
                "vessel_id INTEGER PRIMARY KEY, " +
                "latitude DOUBLE PRECISION, " +
                "longitude DOUBLE PRECISION, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (vessel_id) REFERENCES vessel(id) ON DELETE CASCADE" +
                ")";

        String createHistoricalPositionsTable = "CREATE TABLE IF NOT EXISTS historical_positions (" +
                "id SERIAL PRIMARY KEY, " +
                "vessel_id INTEGER, " +
                "latitude DOUBLE PRECISION, " +
                "longitude DOUBLE PRECISION, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (vessel_id) REFERENCES vessel(id) ON DELETE CASCADE" +
                ")";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement createVesselStmt = connection.prepareStatement(createVesselTable);
             PreparedStatement createCurrentPositionStmt = connection.prepareStatement(createCurrentPositionTable);
             PreparedStatement createHistoricalPositionsStmt = connection.prepareStatement(createHistoricalPositionsTable)) {

            createVesselStmt.executeUpdate();
            createCurrentPositionStmt.executeUpdate();
            createHistoricalPositionsStmt.executeUpdate();
            System.out.println("Schema checked and updated if necessary.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating schema.");
        }
    }
}
