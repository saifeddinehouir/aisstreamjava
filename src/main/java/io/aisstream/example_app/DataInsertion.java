package io.aisstream.example_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import io.aisstream.example_app.db.DBUtil;

public class DataInsertion {

    public static void storeVesselData(int mmsi, String mmsiString, String shipName,
                                       double latitude, double longitude, int heading, String timeUtc) {

        Timestamp sqlTimestamp = ConvertToTimestamp(timeUtc);

        try (Connection conn = DBUtil.getConnection()) {
            String checkQuery = "SELECT COUNT(*) FROM vessel WHERE mmsi = ? OR mmsi_string = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, mmsi);
                checkStmt.setString(2, mmsiString);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    rs.next(); // Move to the first row
                    int count = rs.getInt(1);

                    if (count > 0) {
                        String sql = "UPDATE vessel SET ship_name = ?, latitude = ?, longitude = ?,heading = ?, time_utc = ? WHERE mmsi = ? OR mmsi_string = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, shipName);
                            pstmt.setDouble(2, latitude);
                            pstmt.setDouble(3, longitude);
                            pstmt.setTimestamp(4, sqlTimestamp);
                            pstmt.setInt(5, heading);
                            pstmt.setInt(6, mmsi);
                            pstmt.setString(7, mmsiString);

                            pstmt.executeUpdate();
                        }
                    } else {
                        String sql = "INSERT INTO vessel (mmsi, mmsi_string, ship_name, latitude, longitude, heading, time_utc) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, mmsi);
                            pstmt.setString(2, mmsiString);
                            pstmt.setString(3, shipName);
                            pstmt.setDouble(4, latitude);
                            pstmt.setDouble(5, longitude);
                            pstmt.setInt(6, heading);
                            pstmt.setTimestamp(7, sqlTimestamp);

                            pstmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Timestamp ConvertToTimestamp(String timeUtc) {
        // Remove "+0000 UTC" part
        String cleanedTimeUtc = timeUtc.replace(" +0000 UTC", "");

        // Define the formatter to handle up to nanoseconds
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
                .optionalEnd()
                .toFormatter();

        // Parse to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(cleanedTimeUtc, formatter);

        // Convert to SQL Timestamp
        return Timestamp.valueOf(localDateTime);
    }
}
