package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // For pretty printing JSON

import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime; // For current date/time when booking
import java.util.*;
import java.util.stream.Collectors;

public class UserBookingService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<User> userList;
    private User currentUser; // This holds the currently logged-in/signed-up user

    private final String USER_FILE_PATH = "src/main/resources/localDB/users.json";
    private TrainService trainService; // Inject TrainService or instantiate it

    public UserBookingService() throws IOException {
        // Configure ObjectMapper for pretty printing JSON (optional, but good for debugging)
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadUserListFromFile();
        this.trainService = new TrainService(); // Initialize TrainService here
    }

    // Method to set the active user after login/signup
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Getter for currentUser (useful for App.java if needed)
    public User getCurrentUser() {
        return currentUser;
    }

    private void loadUserListFromFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        if (!usersFile.exists()) {
            System.out.println("UserBookingService: users.json not found. Creating an empty file.");
            userList = new ArrayList<>(); // Initialize with an empty list
            saveUserListToFile(); // Create the file with an empty list
        } else {
            userList = objectMapper.readValue(usersFile, new TypeReference<List<User>>() {});
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
        System.out.println("UserBookingService: User list saved to file.");
    }

    // Modified signUp to return the User object and handle existing users
    public User signUp(String username, String password) {
        // Check if user already exists (case-insensitive)
        Optional<User> existingUser = userList.stream()
                .filter(u -> u.getName().equalsIgnoreCase(username))
                .findFirst();

        if (existingUser.isPresent()) {
            System.out.println("Error: Username '" + username + "' already exists. Please choose a different one.");
            return null; // User already exists
        }

        String hashedPassword = UserServiceUtil.hashPassword(password);
        User newUser = new User(username, password, hashedPassword, new ArrayList<>(), UUID.randomUUID().toString());
        userList.add(newUser);
        try {
            saveUserListToFile();
            return newUser; // Return the newly signed-up user
        } catch (IOException ex) {
            System.err.println("Error saving user data after signup: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    // Modified login to return the User object
    public User login(String username, String password) {
        Optional<User> foundUser = userList.stream()
                .filter(u -> u.getName().equalsIgnoreCase(username) && UserServiceUtil.checkPassword(password, u.getHashedPassword()))
                .findFirst();

        if (foundUser.isPresent()) {
            return foundUser.get(); // Return the logged-in user
        } else {
            return null; // Login failed
        }
    }

    public void fetchBookings() {
        if (currentUser == null) {
            System.out.println("No user logged in to fetch bookings.");
            return;
        }

        // Find the most up-to-date user object from the list (in case changes occurred from other sessions/apps)
        Optional<User> updatedUserOpt = userList.stream()
                .filter(u -> u.getUserId().equals(currentUser.getUserId()))
                .findFirst();

        if (updatedUserOpt.isPresent()) {
            User updatedUser = updatedUserOpt.get();
            List<Ticket> tickets = updatedUser.getTicketsBooked();
            if (tickets == null || tickets.isEmpty()) {
                System.out.println("You have no bookings.");
            } else {
                System.out.println("Your Bookings:");
                updatedUser.printTickets(); // Assuming printTickets method exists and works in User.java
            }
        } else {
            System.out.println("Current user not found in the database. Please relogin.");
        }
    }

    public List<Train> getTrains(String source, String destination) {
        // TrainService is already initialized in constructor
        return trainService.searchTrains(source, destination);
    }

    public List<List<Integer>> fetchSeats(Train train) {
        if (train != null) {
            return train.getSeats();
        }
        return new ArrayList<>(); // Return empty list if train is null
    }

    // Helper method to get available seats count from TrainService
    public int getAvailableSeatsCount(Train train) {
        return trainService.getAvailableSeatsCount(train);
    }

    public Boolean bookTrainSeat(Train train, int row, int col) {
        if (currentUser == null) {
            System.out.println("Error: No user logged in to book a seat.");
            return Boolean.FALSE;
        }
        if (train == null) {
            System.out.println("Error: No train selected for booking.");
            return Boolean.FALSE;
        }

        try {
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() && col >= 0 && col < seats.get(row).size()) {
                if (seats.get(row).get(col) == 0) { // Check if seat is available (0 means available)
                    seats.get(row).set(col, 1); // Mark as booked (1 means booked)
                    train.setSeats(seats); // Update the train object's seats

                    // Update train data in file using TrainService
                    trainService.updateTrainSeats(train);

                    // Create a new Ticket and add to currentUser's bookings
                    // Ensure dateOfTravel is a String as per Ticket.java constructor
                    Ticket newTicket = new Ticket(
                        UUID.randomUUID().toString(), // ticketId
                        currentUser.getUserId(),     // userId
                        train.getStationTimes().keySet().stream().findFirst().orElse("N/A"), // source (first station in map)
                        train.getStationTimes().keySet().stream().reduce((first, second) -> second).orElse("N/A"), // destination (last station in map)
                        LocalDateTime.now().toString(), // dateOfTravel as String
                        train // Pass the actual Train object
                    );

                    // Add ticket to current user's booked tickets
                    currentUser.getTicketsBooked().add(newTicket);

                    // Update the user in the main userList to reflect changes in currentUser's bookings
                    userList = userList.stream()
                        .map(u -> u.getUserId().equals(currentUser.getUserId()) ? currentUser : u)
                        .collect(Collectors.toList());
                    
                    saveUserListToFile(); // Save updated user data

                    return true; // Booking successful
                } else {
                    System.out.println("Seat (" + row + "," + col + ") is already booked.");
                    return false; // Seat is already booked
                }
            } else {
                System.out.println("Invalid row or column index.");
                return false; // Invalid row or seat index
            }
        } catch (IOException ex) {
            System.err.println("Error during seat booking: " + ex.getMessage());
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    // Refined cancelBooking method
    public Boolean cancelBooking(String ticketIdToCancel) { // Take ticketId as argument
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return Boolean.FALSE;
        }
        if (ticketIdToCancel == null || ticketIdToCancel.trim().isEmpty()) {
            System.out.println("Ticket ID cannot be empty for cancellation.");
            return Boolean.FALSE;
        }

        // Find the user in userList to get the mutable list of tickets
        Optional<User> userInListOpt = userList.stream()
            .filter(u -> u.getUserId().equals(currentUser.getUserId()))
            .findFirst();

        if (userInListOpt.isPresent()) {
            User userInList = userInListOpt.get(); // This is the mutable user object from the list
            List<Ticket> tickets = userInList.getTicketsBooked();

            if (tickets == null || tickets.isEmpty()) {
                System.out.println("You have no bookings to cancel.");
                return Boolean.FALSE;
            }

            // Find the ticket to be removed and its associated train/seat
            Optional<Ticket> ticketToRemoveOpt = tickets.stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketIdToCancel))
                .findFirst();

            if (ticketToRemoveOpt.isPresent()) {
                Ticket ticketToRemove = ticketToRemoveOpt.get();
                boolean removed = tickets.remove(ticketToRemove); // Remove the ticket from the list

                if (removed) {
                    // Update the currentUser object's tickets list as well
                    currentUser.setTicketsBooked(tickets); 

                    // --- TODO: Implement logic to free up the seat in the train's data ---
                    // This requires finding the train by ID, then finding the specific seat
                    // and changing its status from 1 (booked) back to 0 (available).
                    // This needs the row/column information, which is not directly in Ticket.
                    // You might need to store row/col in Ticket or re-derive it.
                    // For now, this part is a placeholder.
                    Train bookedTrain = ticketToRemove.getTrain();
                    if (bookedTrain != null) {
                        // This is complex: you need to know the exact seat (row, col) from the ticket
                        // to free it up. If ticket doesn't store row/col, you can't easily do this.
                        // For a simple implementation, you might skip freeing the seat or add row/col to Ticket.
                        System.out.println("NOTE: Seat freeing logic not fully implemented yet for cancellation.");
                    }
                    // --- END TODO ---

                    try {
                        saveUserListToFile(); // Save the updated user list
                        System.out.println("Ticket with ID " + ticketIdToCancel + " has been canceled.");
                        return Boolean.TRUE;
                    } catch (IOException e) {
                        System.err.println("Error saving user data after cancellation: " + e.getMessage());
                        e.printStackTrace();
                        return Boolean.FALSE;
                    }
                } else {
                    System.out.println("Failed to remove ticket from list (internal error).");
                    return Boolean.FALSE;
                }
            } else {
                System.out.println("No ticket found with ID " + ticketIdToCancel + " for user " + currentUser.getName() + ".");
                return Boolean.FALSE;
            }
        } else {
            System.out.println("Current user not found in the database. Please relogin.");
            return Boolean.FALSE;
        }
    }
}
