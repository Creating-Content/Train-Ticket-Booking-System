package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList; // Import for ArrayList
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String name;
    private String password; // This field is typically only used for input, not persisted directly
    private String hashedPassword;
    private List<Ticket> ticketsBooked;
    private String userId;

    // Default constructor for Jackson deserialization
    public User() {
        this.ticketsBooked = new ArrayList<>(); // Initialize to prevent NullPointerException
    }

    // Constructor for creating a new user (e.g., during signup)
    public User(String name, String password, String hashedPassword, List<Ticket> ticketsBooked, String userId) {
        this.name = name;
        this.password = password; // Raw password, usually not persisted
        this.hashedPassword = hashedPassword;
        // Ensure ticketsBooked is initialized, even if null is passed
        this.ticketsBooked = (ticketsBooked != null) ? ticketsBooked : new ArrayList<>();
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter for raw password (if needed for login comparison before hashing)
    public String getPassword() {
        return password;
    }

    // Setter for raw password (if needed for input)
    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<Ticket> getTicketsBooked() {
        return ticketsBooked;
    }

    public void setTicketsBooked(List<Ticket> ticketsBooked) {
        // Defensive check to ensure the list is never null
        this.ticketsBooked = (ticketsBooked != null) ? ticketsBooked : new ArrayList<>();
    }

    // Method to print all booked tickets for this user
    public void printTickets() {
        if (ticketsBooked == null || ticketsBooked.isEmpty()) {
            System.out.println("No tickets booked for " + this.name + ".");
            return;
        }
        System.out.println("--- Tickets for " + this.name + " ---");
        for (Ticket ticket : ticketsBooked) {
            if (ticket != null) {
                System.out.println(ticket.getTicketInfo()); // Calls the getTicketInfo method from Ticket
            }
        }
        System.out.println("-------------------------");
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}