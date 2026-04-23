# NUST Smart Security System

This is a simple Java program that tracks students and visitors as they enter or exit the NUST campus, departments, and hostels. The system runs completely in the command line.

## Key Features

* **Access Tracking:** Records when a student scans their ID card at the main gate or a specific department building.
* **Student Management:** Stores new student details like their enrolled department and hostel status, and includes a feature to permanently remove student records using their CNIC.
* **Live Location Monitoring:** Includes a live tracking function to locate exactly where a student is on campus at any given time.
* **Visitor Controls:** Manages temporary visitors by logging their entry time. The system gives these visitors a fifteen-minute time limit and applies a fine if they stay too long.
* **Data Persistence:** All of this information is automatically saved into a plain text file so no data is lost when the program closes.

## Code Structure

The code is split into a few basic parts to keep things organized:

* **Main Interface:** Handles the text menu shown on the screen.
* **Security Logic:** Does all the heavy lifting, such as calculating times and enforcing the security rules.
* **Database Handler:** Responsible for reading and writing the text file.
* **Object Blueprints:** The rest of the files are simple blueprints that define what a student, visitor, or department is.