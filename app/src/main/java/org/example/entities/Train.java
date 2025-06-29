package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

// No direct 'java.sql.Date' or 'java.sql.Time' imports needed unless specifically used.
import java.util.List;
import java.util.Map;
import java.util.ArrayList; // Added for defensive initialization if needed

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Train {

    private String trainId;
    private String trainNo;
    private List<List<Integer>> seats;
    private Map<String, String> stationTimes;
    private List<String> stations;

    // Default constructor for Jackson deserialization
    public Train() {
        // Initialize collections to prevent NullPointerExceptions
        this.seats = new ArrayList<>();
        this.stationTimes = new java.util.HashMap<>();
        this.stations = new ArrayList<>();
    }

    // Constructor for creating a new train
    public Train(String trainId, String trainNo, List<List<Integer>> seats, Map<String, String> stationTimes, List<String> stations) {
        this.trainId = trainId;
        this.trainNo = trainNo;
        // Defensive initialization for collections
        this.seats = (seats != null) ? seats : new ArrayList<>();
        this.stationTimes = (stationTimes != null) ? stationTimes : new java.util.HashMap<>();
        this.stations = (stations != null) ? stations : new ArrayList<>();
    }

    // --- Getters and Setters for all fields (crucial for Jackson) ---
    public List<String> getStations() {
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = (stations != null) ? stations : new ArrayList<>();
    }

    public List<List<Integer>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<Integer>> seats) {
        this.seats = (seats != null) ? seats : new ArrayList<>();
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public Map<String, String> getStationTimes() {
        return stationTimes;
    }

    public void setStationTimes(Map<String, String> stationTimes) {
        this.stationTimes = (stationTimes != null) ? stationTimes : new java.util.HashMap<>();
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    // Method to get a descriptive string for the train
    public String getTrainInfo() {
        return String.format("Train ID: %s Train No: %s", trainId, trainNo);
    }
}
