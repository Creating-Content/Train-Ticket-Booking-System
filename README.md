# Train-Ticket-Booking-System

## 🚂 Java Console-Based Train Ticket Booking System
![Alt text for the image](output_images/output1.png)
A console-based application built with Java and Gradle for managing train bookings, user accounts, and train seat availability, with data persistence handled via JSON files.

## ✨ Features
###User Management: Sign up new users, log in existing users, and secure user accounts with hashed passwords.

###Train Search: Find available trains between specified source and destination stations.

###Seat Booking: Book seats on available trains, with real-time updates to seat availability.

###Booking Management: Fetch and view personal booking history, and cancel existing bookings.

###Data Persistence: All user and train data is stored and loaded from local JSON files, ensuring data is saved across application runs.

## 🚀 Technologies Used
Java: The core programming language for the application logic.

Gradle: Build automation tool for managing dependencies and project compilation.

Jackson: A high-performance JSON processor for serializing Java objects to JSON and deserializing JSON to Java objects.

jBCrypt: A library for secure password hashing and verification.

🛠️ Getting Started
Prerequisites
Java Development Kit (JDK) 21 or later. You can download it from Oracle JDK or OpenJDK.

Git (optional, for cloning the repository).

Cloning the Repository (if applicable)
git clone https://github.com/your-username/your-repository-name.git
cd your-repository-name/app # Navigate into the 'app' module

Project Structure
The core application logic resides in the app module.

app/src/main/java/org/example/: Contains the main App class and utility/service classes.

app/src/main/java/org/example/entities/: Contains the Java POJOs (Plain Old Java Objects) for User, Train, and Ticket.

app/src/main/java/org/example/service/: Contains the business logic for user management (UserBookingService) and train management (TrainService).

app/src/main/java/org/example/util/: Contains utility classes like UserServiceUtil for password hashing.

app/src/main/resources/localDB/: This directory will store your users.json and trains.json files for data persistence. These files will be created automatically if they don't exist when the application runs for the first time.

Setup and Running the Application
Navigate to the project's root directory in your terminal. This is the directory containing gradlew.bat (or gradlew on Linux/macOS) and the app folder.

cd C:\Users\ARKADIP GHOSH\Desktop\GOAL\Projects\IRCTC # Example path

Build the project. This compiles all Java files and resolves dependencies.

.\gradlew.bat clean build --no-build-cache # For Windows
# Or for Linux/macOS:
# ./gradlew clean build --no-build-cache

Wait for BUILD SUCCESSFUL message.

Run the application.

.\gradlew.bat run # For Windows
# Or for Linux/macOS:
# ./gradlew run

Interact with the application.
The application will print a menu to the console, and you can type your choices (numbers 1-7) and press Enter to interact.

💾 Data Files (.json)
The application uses users.json and trains.json for data persistence.

users.json: Stores user details, including hashed passwords and their booked tickets.

trains.json: Stores train details, including available seats and station schedules.

These files are located in app/src/main/resources/localDB/. If these files do not exist when the application runs, they will be created automatically with empty data structures.

Sample trains.json Content (Initial)

🖥️ Sample Interaction / Output
Here's a simulated terminal session demonstrating a user signing up, logging in, searching for a train, booking a seat, and viewing their bookings.
![Alt text for the image](output_images/output2.png)
![Alt text for the image](output_images/output3.png)
![Alt text for the image](output_images/output4.png)
![Alt text for the image](output_images/output5.png)
![Alt text for the image](output_images/output6.png)
![Alt text for the image](output_images/output7.png)
![Alt text for the image](output_images/output8.png)

📝 Future Enhancements
Graphical User Interface (GUI): Implement a user-friendly GUI using technologies like JavaFX or Swing.

Database Integration: Replace JSON file persistence with a robust database (e.g., H2, MySQL, PostgreSQL) for better data management and scalability.

Advanced Booking Features: Implement seat selection logic (e.g., specific seat numbers, class types), multiple passenger bookings.

Train Schedule Management: Add an admin interface for managing train schedules, adding/removing trains, and updating routes.

Payment Gateway Integration: Simulate or integrate with a payment system for ticket purchases.

JUnit Testing: Expand unit and integration tests for more comprehensive code coverage.

Date/Time Handling: Use java.time.LocalDate or LocalDateTime more extensively and consistently for dateOfTravel in Ticket and Train entities, potentially with custom Jackson serializers/deserializers for specific formats.

🤝 Contributing
Contributions are welcome! If you have suggestions or find issues, please open an issue or submit a pull request.

📄 License
This project is licensed under the MIT License - see the LICENSE file (if you have one) for details.
