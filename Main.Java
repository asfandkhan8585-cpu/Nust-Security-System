// contains the main code ( Hamza >-< )
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner keyboardInput = new Scanner(System.in);
        SecurityLogic securitySystem = new SecurityLogic();

        boolean keepRunning = true;

        System.out.println("------------------------------------------");
        System.out.println("  NUST SMART SECURITY SYSTEM (CONSOLE)   ");
        System.out.println("------------------------------------------");

        while (keepRunning) {
            System.out.println("\nSelect an Operation:");
            System.out.println("1. Scan ID Card (Gate/Dept)");
            System.out.println("2. Add New Student");
            System.out.println("3. Remove Student");
            System.out.println("4. Live Search by CNIC");
            System.out.println("5. Manage Visitors");
            System.out.println("6. Exit System");
            System.out.print("Choice: ");

            String userChoice = keyboardInput.nextLine();

            switch (userChoice) {
                case "1":
                    System.out.print("Enter Scanned CNIC: ");
                    String cardId = keyboardInput.nextLine();
                    System.out.println("Locations: 'MainGate' or 'Dept_SEECS', 'Dept_SMME' etc.");
                    System.out.print("Enter Scanner Location: ");
                    String loc = keyboardInput.nextLine();
                    securitySystem.scanStudentCard(cardId, loc);
                    break;

                case "2":
                    System.out.println("\n--- Register Student ---");
                    System.out.print("CNIC (Format: 12345-1234567-1): ");
                    String id = keyboardInput.nextLine();
                    System.out.print("Full Name: ");
                    String name = keyboardInput.nextLine();
                    System.out.print("Father's Name: ");
                    String fName = keyboardInput.nextLine();
                    System.out.print("CMS ID: ");
                    String cms = keyboardInput.nextLine();

                    System.out.print("Department (e.g., SEECS, SMME, NBS): ");
                    String deptChoice = keyboardInput.nextLine().toUpperCase();

                    Department deptObject;
                    switch(deptChoice) {
                        case "SEECS": deptObject = new SEECS(); break;
                        case "SMME": deptObject = new SMME(); break;
                        case "NBS": deptObject = new NBS(); break;
                        case "S3H": deptObject = new S3H(); break;
                        case "ASAB": deptObject = new ASAB(); break;
                        default: deptObject = new SCME();
                    }

                    System.out.print("Major/Program: ");
                    String majorProgram = keyboardInput.nextLine();
                    System.out.print("Phone Number: ");
                    String contact = keyboardInput.nextLine();

                    System.out.println("Living Status: ");
                    System.out.println("1. Day Scholar");
                    System.out.println("2. Internal Hostelite");
                    System.out.println("3. External Hostelite");
                    System.out.print("Select Status (1-3): ");
                    String statusInput = keyboardInput.nextLine();

                    String livingState = "Day Scholar";
                    String accommodation = "N/A";

                    if (statusInput.equals("2")) {
                        livingState = "Internal Hostelite";
                        System.out.print("Enter NUST Hostel Name (e.g., Attar, Razi): ");
                        accommodation = keyboardInput.nextLine();
                    } else if (statusInput.equals("3")) {
                        livingState = "External Hostelite";
                        System.out.print("Enter External Hostel Name/Area: ");
                        accommodation = keyboardInput.nextLine();
                    }

                    Student newEnrollment = new Student(id, name, fName, deptObject, majorProgram, contact, cms, livingState, accommodation);
                    securitySystem.registerNewStudent(newEnrollment);
                    break;

                case "3":
                    System.out.print("Enter CNIC of the student to remove: ");
                    String deleteCnic = keyboardInput.nextLine();
                    securitySystem.deleteStudentRecord(deleteCnic);
                    break;

                case "4":
                    System.out.print("Enter CNIC to search: ");
                    String searchCnic = keyboardInput.nextLine();
                    securitySystem.searchStudentByCnic(searchCnic);
                    break;

                case "5":
                    System.out.println("\n--- Visitor Management ---");
                    System.out.println("1. Log Entry");
                    System.out.println("2. Log Exit");
                    System.out.println("3. Pay Fine & Exit (Late Leavers)");
                    System.out.print("Select action: ");
                    String vAction = keyboardInput.nextLine();

                    System.out.print("Enter Visitor CNIC: ");
                    String vCnic = keyboardInput.nextLine();

                    if (vAction.equals("1")) {
                        securitySystem.logVisitorEntry(vCnic);
                    } else if (vAction.equals("2")) {
                        securitySystem.logVisitorExit(vCnic, false); // False = when not paying fine
                    } else if (vAction.equals("3")) {
                        securitySystem.logVisitorExit(vCnic, true);  // True = when paying fine
                    } else {
                        System.out.println("Invalid visitor action.");
                    }
                    break;

                case "6":
                    System.out.println("System shutting down. Goodbye!");
                    keepRunning = false;
                    break;

                default:
                    System.out.println("Invalid selection. Try again.");
            }
        }
        keyboardInput.close();
    }
}