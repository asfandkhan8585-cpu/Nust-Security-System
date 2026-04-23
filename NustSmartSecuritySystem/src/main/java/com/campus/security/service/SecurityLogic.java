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

    public Visitor logVisitorEntry(String cnic) {
        Visitor v = new Visitor(cnic);
        v.timeOfEntry = LocalDateTime.now();
        activeVisitors.add(v);
        return v;
    }

    public String logVisitorExit(String cnic, boolean payingFine) {
        // 1. Check if they are already flagged as a late leaver
        for (int i = 0; i < lateLeavers.size(); i++) {
            if (lateLeavers.get(i).getNationalIdCard().equals(cnic)) {
                if (payingFine) {
                    lateLeavers.remove(i);
                    return "Fine paid. Visitor cleared at " + LocalDateTime.now().format(clockFormat);
                } else {
                    return "⚠️ LATE LEAVER. 500 PKR Fine required to exit. Please select the 'Pay Fine' option.";
                }
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
                    return "⚠️ OVERSTAYED (" + minutesPassed + " mins). Added to Late Leavers. 500 PKR fine applies.";
                } else {
                    activeVisitors.remove(i);
                    return "Visitor exited safely. Duration: " + minutesPassed + " mins.";
                }
            }
        }

        return "Error: Visitor not found inside NUST.";
    }


    // --- STUDENT MANAGEMENT (Unchanged) ---

    public boolean registerNewStudent(Student newStudent) {
        registeredStudents.add(newStudent);
        return storageHandler.saveRecords(registeredStudents);
    }

    public boolean deleteStudentRecord(String targetCnic) {
        boolean wasRemoved = false;

        for (int i = 0; i < registeredStudents.size(); i++) {
            if (registeredStudents.get(i).getNationalIdCard().equals(targetCnic)) {
                registeredStudents.remove(i);
                wasRemoved = true;
                break;
            }
        }

        if (wasRemoved) {
            return storageHandler.saveRecords(registeredStudents);
        }
        return false;
    }

    public Student searchStudentByCnic(String searchCnic) {
        for (int i = 0; i < registeredStudents.size(); i++) {
            if (registeredStudents.get(i).getNationalIdCard().equals(searchCnic)) {
                return registeredStudents.get(i);
            }
        }
        return null;
    }

    public String scanStudentCard(String scannedCnic, String scanLocation) {
        String currentTime = LocalDateTime.now().format(clockFormat);
        Student targetStudent = null;

        for (int i = 0; i < registeredStudents.size(); i++) {
            if (registeredStudents.get(i).getNationalIdCard().equals(scannedCnic)) {
                targetStudent = registeredStudents.get(i);
                break;
            }
        }

        if (targetStudent == null) {
            return "ALERT: Identity not found in database! Time: " + currentTime;
        }

        String resultMessage = "";

        if (scanLocation.equalsIgnoreCase("MainGate")) {
            if (targetStudent.isInsideCampus) {
                targetStudent.isInsideCampus = false;
                targetStudent.isInsideDepartment = false;
                targetStudent.isInsideHostel = false;
                resultMessage = "EXIT RECORDED - NUST MAIN GATE | Name: " + targetStudent.fullName;
            } else {
                targetStudent.isInsideCampus = true;
                targetStudent.lastCampusEntry = LocalDateTime.now();
                resultMessage = "ENTRY RECORDED - NUST MAIN GATE | Name: " + targetStudent.fullName;
            }
        } else if (scanLocation.startsWith("Dept_")) {
            if (!targetStudent.isInsideCampus) {
                resultMessage = "ACCESS DENIED: Student must enter NUST Main Gate first!";
            } else {
                String specificDept = scanLocation.replace("Dept_", "");
                if (targetStudent.isInsideDepartment && targetStudent.activeDepartmentLocation.equals(specificDept)) {
                    targetStudent.isInsideDepartment = false;
                    targetStudent.activeDepartmentLocation = "";
                    resultMessage = "EXIT RECORDED - " + specificDept + " | Name: " + targetStudent.fullName;
                } else {
                    targetStudent.isInsideDepartment = true;
                    targetStudent.activeDepartmentLocation = specificDept;
                    resultMessage = "ENTRY RECORDED - " + specificDept + " | Name: " + targetStudent.fullName;
                }
            }
        }
        storageHandler.saveRecords(registeredStudents);
        return resultMessage;
    }
}
