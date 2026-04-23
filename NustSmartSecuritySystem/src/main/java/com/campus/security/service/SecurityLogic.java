package com.campus.security.service;

import com.campus.security.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class SecurityLogic {
    private ArrayList<Student> registeredStudents;
    private ArrayList<Visitor> activeVisitors = new ArrayList<>();
    private ArrayList<Visitor> lateLeavers = new ArrayList<>();
    private DatabaseHandler storageHandler = new DatabaseHandler();

    private DateTimeFormatter clockFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    public SecurityLogic() {
        registeredStudents = storageHandler.loadRecords();
    }

    // --- VISITOR MANAGEMENT ---

    public void logVisitorEntry(String cnic) {
        Visitor v = new Visitor(cnic);
        v.timeOfEntry = LocalDateTime.now();
        activeVisitors.add(v);
        System.out.println("Success! Visitor entered at " + v.timeOfEntry.format(clockFormat));
        System.out.println("Time Limit: 15 Minutes.");
    }

    public void logVisitorExit(String cnic, boolean payingFine) {
        // 1. Check if they are already flagged as a late leaver
        for (int i = 0; i < lateLeavers.size(); i++) {
            if (lateLeavers.get(i).getNationalIdCard().equals(cnic)) {
                if (payingFine) {
                    lateLeavers.remove(i);
                    System.out.println("Fine paid. Visitor cleared at " + LocalDateTime.now().format(clockFormat));
                } else {
                    System.out.println("⚠️ LATE LEAVER. 500 PKR Fine required to exit. Please select the 'Pay Fine' option.");
                }
                return;
            }
        }

        // 2. Check active visitors trying to leave
        for (int i = 0; i < activeVisitors.size(); i++) {
            if (activeVisitors.get(i).getNationalIdCard().equals(cnic)) {
                Visitor v = activeVisitors.get(i);
                long minutesPassed = ChronoUnit.MINUTES.between(v.timeOfEntry, LocalDateTime.now());

                if (minutesPassed > 15) {
                    v.hasOverstayed = true;
                    lateLeavers.add(v);
                    activeVisitors.remove(i);
                    System.out.println("⚠️ OVERSTAYED (" + minutesPassed + " mins). Added to Late Leavers. 500 PKR fine applies.");
                } else {
                    activeVisitors.remove(i);
                    System.out.println("Visitor exited safely. Duration: " + minutesPassed + " mins.");
                }
                return;
            }
        }

        System.out.println("Error: Visitor not found inside NUST.");
    }


    // --- STUDENT MANAGEMENT (Unchanged) ---

    public void registerNewStudent(Student newStudent) {
        registeredStudents.add(newStudent);
        storageHandler.saveRecords(registeredStudents);
        System.out.println("Success! Student " + newStudent.fullName + " added to the system.");
    }

    public void deleteStudentRecord(String targetCnic) {
        boolean wasRemoved = false;

        for (int i = 0; i < registeredStudents.size(); i++) {
            if (registeredStudents.get(i).getNationalIdCard().equals(targetCnic)) {
                registeredStudents.remove(i);
                wasRemoved = true;
                break;
            }
        }

        if (wasRemoved) {
            storageHandler.saveRecords(registeredStudents);
            System.out.println("Student record permanently removed.");
        } else {
            System.out.println("Error: No student found with CNIC " + targetCnic);
        }
    }

    public void searchStudentByCnic(String searchCnic) {
        Student foundStudent = null;

        for (int i = 0; i < registeredStudents.size(); i++) {
            if (registeredStudents.get(i).getNationalIdCard().equals(searchCnic)) {
                foundStudent = registeredStudents.get(i);
                break;
            }
        }

        if (foundStudent == null) {
            System.out.println("Search Result: Student not found in the database.");
            return;
        }

        System.out.println("\n--- SEARCH RESULTS ---");
        System.out.println("Name: " + foundStudent.fullName);
        System.out.println("CMS ID: " + foundStudent.universityRollNumber);
        System.out.println("Home Department: " + foundStudent.enrolledDepartment.getDepartmentName());
        System.out.println("Status: " + foundStudent.livingStatus);

        System.out.println("\n--- LIVE TRACKING ---");
        if (foundStudent.isInsideCampus) {
            System.out.println("Campus Status: Currently INSIDE NUST");
            if (foundStudent.isInsideDepartment) {
                System.out.println("Department Status: Inside " + foundStudent.activeDepartmentLocation);
            } else {
                System.out.println("Department Status: Not in any specific department building.");
            }
        } else {
            System.out.println("Campus Status: OUTSIDE NUST");
        }
        System.out.println("----------------------\n");
    }

    public void scanStudentCard(String scannedCnic, String scanLocation) {
        String currentTime = LocalDateTime.now().format(clockFormat);
        Student targetStudent = null;

        for (int i = 0; i < registeredStudents.size(); i++) {
            if (registeredStudents.get(i).getNationalIdCard().equals(scannedCnic)) {
                targetStudent = registeredStudents.get(i);
                break;
            }
        }

        if (targetStudent == null) {
            System.out.println("ALERT: Identity not found in database! Time: " + currentTime);
            return;
        }

        if (scanLocation.equalsIgnoreCase("MainGate")) {
            if (targetStudent.isInsideCampus) {
                targetStudent.isInsideCampus = false;
                targetStudent.isInsideDepartment = false;
                targetStudent.isInsideHostel = false;
                System.out.println("EXIT RECORDED - NUST MAIN GATE | Name: " + targetStudent.fullName);
            } else {
                targetStudent.isInsideCampus = true;
                targetStudent.lastCampusEntry = LocalDateTime.now();
                System.out.println("ENTRY RECORDED - NUST MAIN GATE | Name: " + targetStudent.fullName);
            }
        } else if (scanLocation.startsWith("Dept_")) {
            if (!targetStudent.isInsideCampus) {
                System.out.println("ACCESS DENIED: Student must enter NUST Main Gate first!");
            } else {
                String specificDept = scanLocation.replace("Dept_", "");
                if (targetStudent.isInsideDepartment && targetStudent.activeDepartmentLocation.equals(specificDept)) {
                    targetStudent.isInsideDepartment = false;
                    targetStudent.activeDepartmentLocation = "";
                    System.out.println("EXIT RECORDED - " + specificDept + " | Name: " + targetStudent.fullName);
                } else {
                    targetStudent.isInsideDepartment = true;
                    targetStudent.activeDepartmentLocation = specificDept;
                    System.out.println("ENTRY RECORDED - " + specificDept + " | Name: " + targetStudent.fullName);
                }
            }
        }
        storageHandler.saveRecords(registeredStudents);
    }
}
