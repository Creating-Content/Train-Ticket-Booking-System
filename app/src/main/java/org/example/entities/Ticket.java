package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

// If your dateOfTravel is a String, no specific java.time import needed here for the field.
// If you decide to use java.time.LocalDateTime for dateOfTravel, you'd import it here.
// import java.time.LocalDateTime;

// Removed java.sql.Date as it's typically not used with modern Java date/time or Jackson for JSON.

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket {

    private String ticketId;
    private String userId;
    private String source;
    private String destination;
    private String dateOfTravel; // Keeping as String as per your current code and JSON
    private Train train; // This will store the full Train object related to the ticket

    // Default constructor for Jackson deserialization
    public Ticket() {}

    // Constructor to be used when creating a new ticket from UserBookingService
    // This signature matches exactly: (String, String, String, String, String, Train)
    public Ticket(String ticketId, String userId, String source, String destination, String dateOfTravel, Train train) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
        // The 'ticketInfo' field is not directly set here, as getTicketInfo() is a dynamic method.
        // If 'ticketInfo' needs to be a persisted field, it should be added to the constructor and JSON.
    }

    // This method generates the ticket info string for display.
    // It dynamically creates the string based on current field values.
    public String getTicketInfo() {
        String trainInfo = (train != null) ? train.getTrainId() : "N/A";
        return String.format("Ticket ID: %s | User: %s | From: %s | To: %s | On: %s | Train: %s",
                ticketId, userId, source, destination, dateOfTravel, trainInfo);
    }

    // --- Getters and Setters for all fields (crucial for Jackson) ---
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDateOfTravel() {
        return dateOfTravel;
    }

    public void setDateOfTravel(String dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}
    // Removed getTicketInfoField/setTicketInfoField as getTicketInfo() method handles display.
    // If you need a 'ticketInfo' field to be persisted in JSON, you'd add it as a private field
    // and include it in constructors/getters/setters.
