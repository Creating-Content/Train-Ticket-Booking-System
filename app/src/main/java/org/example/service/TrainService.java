package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // Import for pretty printing
import org.example.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    // Corrected path, assuming it's relative to the 'app' module root
    private static final String TRAIN_DB_PATH = "src/main/resources/localDB/trains.json";

    public TrainService() throws IOException {
        // Enable pretty printing for JSON output (useful for debugging)
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        File trainsFile = new File(TRAIN_DB_PATH);
        // Check if the file exists; if not, create an empty list and save it
        if (!trainsFile.exists()) {
            System.out.println("TrainService: trains.json not found. Creating an empty file.");
            trainList = new ArrayList<>(); // Initialize with an empty list
            saveTrainListToFile(); // Create the file
        } else {
            // If the file exists, read its content
            trainList = objectMapper.readValue(trainsFile, new TypeReference<List<Train>>() {});
        }
    }

    public List<Train> searchTrains(String source, String destination) {
        // Make sure source and destination are case-insensitive for search
        String lowerCaseSource = source.toLowerCase();
        String lowerCaseDestination = destination.toLowerCase();

        return trainList.stream()
                .filter(train -> validTrain(train, lowerCaseSource, lowerCaseDestination))
                .collect(Collectors.toList());
    }

    // Renamed from addTrain to saveOrUpdateTrain to reflect its actual "add or update" behavior
    public void saveOrUpdateTrain(Train newTrain) {
        // Find the index of the train if it already exists
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            // Update existing train
            trainList.set(index.getAsInt(), newTrain);
            System.out.println("TrainService: Updated existing train: " + newTrain.getTrainId());
        } else {
            // Add new train
            trainList.add(newTrain);
            System.out.println("TrainService: Added new train: " + newTrain.getTrainId());
        }
        saveTrainListToFile(); // Save changes to file
    }

    // Specific method to update only the seats of a train and persist it
    public void updateTrainSeats(Train updatedTrain) throws IOException {
        // Find the train in the list by ID
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            // Replace the old train object with the one that has updated seats
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
            System.out.println("TrainService: Seats updated for train: " + updatedTrain.getTrainId());
        } else {
            System.err.println("TrainService: Warning: Attempted to update seats for a train not found in the list: " + updatedTrain.getTrainId());
            // If the train isn't found, you might want to add it or throw an exception.
            // For now, it just warns and doesn't save (as it's not in the list).
        }
    }

    private void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            // It's better to rethrow RuntimeException or handle more gracefully
            // in a real application, but printStackTrace is okay for now.
            System.err.println("TrainService: Error saving train list to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStations();

        // Ensure stationOrder is not null or empty
        if (stationOrder == null || stationOrder.isEmpty()) {
            return false;
        }

        // Use the already lowercased source and destination
        int sourceIndex = stationOrder.indexOf(source);
        int destinationIndex = stationOrder.indexOf(destination);

        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }

    // Method to count available seats in a train
    public int getAvailableSeatsCount(Train train) {
        if (train == null || train.getSeats() == null) {
            return 0;
        }
        int count = 0;
        for (List<Integer> row : train.getSeats()) {
            if (row != null) { // Defensive check for null rows
                for (Integer seatStatus : row) {
                    if (seatStatus != null && seatStatus == 0) { // Assuming 0 means available
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
