package io.aisstream.example_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import io.aisstream.example_app.db.DBUtil;

public class DataInsertion {
    public static void storeVesselData(int mmsi, String mmsiString, String shipName,
                                         double latitude, double longitude, String timeUtc) {
        
        Timestamp sqlTimestamp = ConvertToTimestamp(timeUtc);
        
        String sql = "INSERT INTO vessel (mmsi, mmsi_string, ship_name, latitude, longitude, time_utc) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mmsi);
            pstmt.setString(2, mmsiString);
            pstmt.setString(3, shipName);
            pstmt.setDouble(4, latitude);
            pstmt.setDouble(5, longitude);
            pstmt.setTimestamp(6, sqlTimestamp);

            pstmt.executeUpdate();
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
    Timestamp sqlTimestamp = Timestamp.valueOf(localDateTime);

    return sqlTimestamp;
}
}
