# NUST Smart Security System

This is a Java application that tracks students and visitors as they enter or exit the NUST campus, departments, and hostels. The system interacts with users through a graphical user interface (GUI) built with JavaFX.

## Key Features

* **Access Tracking:** Records when a student scans their ID card at the main gate, specific department buildings, or various hostels. It securely enforces access rules, such as preventing Day Scholars from accessing hostels.
* **Student Management:** Stores new student details like their enrolled department and living status, and handles live registrations and deletions.
* **Live Location Monitoring:** Includes a live tracking function to locate exactly where a student is on campus at any given time based on their card scans.
* **Visitor Controls:** Manages temporary visitors by logging their entry and exit times. The system enforces a fifteen-minute time limit and automatically flags them for a fine if they overstay.
* **Data Persistence:** All records and states are automatically saved into a local file database so no information is lost when the program closes.

## Code Structure

The project has been modernized and organized cleanly:

* **Controllers & Views (JavaFX):** Manages the dashboard screen, button clicks, panels, and dropdown menus (`MainController.java` and `dashboard.fxml`).
* **Security Logic:** Does all the heavy lifting, such as calculating times, managing access tracking, and enforcing security rules based on a student's living status or scan location.
* **Database Handler:** Responsible for reading from and writing to the data files.
* **Models / Object Blueprints:** The rest of the files cleanly define the data structures for a student, visitor, and various departments.