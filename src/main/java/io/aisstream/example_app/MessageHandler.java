package io.aisstream.example_app;

import io.aisstream.example_app.db.DBUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public void processMessage(String messageJson) {
        try {
            JSONObject message = new JSONObject(messageJson);
            String messageType = message.getString("MessageType");

            switch (messageType) {
                case "PositionReport":
                    handlePositionReport(message.getJSONObject("Message").getJSONObject("PositionReport"));
                    break;
                case "ShipStaticData":
                    handleShipStaticData(message.getJSONObject("Message").getJSONObject("ShipStaticData"));
                    break;
                case "UnknownMessage":
                    handleUnknownMessage(message.getJSONObject("Message"));
                    break;
                case "StandardClassBPositionReport":
                    handleStandardClassBPositionReport(message.getJSONObject("Message").getJSONObject("StandardClassBPositionReport"));
                    break;
                default:
                    System.out.println("Unhandled message type: " + messageType);
            }
        } catch (Exception e) {
            logger.error("Error processing message: " + messageJson, e);
        }
    }

    private void handlePositionReport(JSONObject positionReport) {
        // Extract and process position report data
        double latitude = positionReport.getDouble("Latitude");
        double longitude = positionReport.getDouble("Longitude");
        int vesselId = positionReport.getInt("VesselID"); // Assuming VesselID is provided

        String sql = "INSERT INTO historical_positions (vessel_id, latitude, longitude) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vesselId);
            pstmt.setDouble(2, latitude);
            pstmt.setDouble(3, longitude);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error insertion position report into historical_positions", e);
        }
    }

    private void handleShipStaticData(JSONObject shipStaticData) {
        // Extract and process static ship data
        int vesselId = shipStaticData.getInt("ID"); // Assuming ID is provided
        String shipName = shipStaticData.getString("Name");
        String callSign = shipStaticData.getString("CallSign");
        String destination = shipStaticData.getString("Destination");
        String dimensions = shipStaticData.getString("Dimensions");

        String sql = "INSERT INTO vessel (id, name, call_sign, destination, dimensions) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, " +
                "call_sign = EXCLUDED.call_sign, destination = EXCLUDED.destination, " +
                "dimensions = EXCLUDED.dimensions";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vesselId);
            pstmt.setString(2, shipName);
            pstmt.setString(3, callSign);
            pstmt.setString(4, destination);
            pstmt.setString(5, dimensions);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error inserting or updating vessel data", e);
        }
    }

    private void handleUnknownMessage(JSONObject unknownMessage) {
        // Handle unknown messages
        System.out.println("Received unknown message: " + unknownMessage.toString());
    }

    private void handleStandardClassBPositionReport(JSONObject standardClassBPositionReport) {
        // Extract and process Standard Class B position report data
        double latitude = standardClassBPositionReport.getDouble("Latitude");
        double longitude = standardClassBPositionReport.getDouble("Longitude");
        int vesselId = standardClassBPositionReport.getInt("VesselID"); // Assuming VesselID is provided

        String sql = "INSERT INTO historical_positions (vessel_id, latitude, longitude) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vesselId);
            pstmt.setDouble(2, latitude);
            pstmt.setDouble(3, longitude);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error inserting Standard Class B position report into historical_positions", e);
        }
    }
}
