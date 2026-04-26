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
    private int totalFinesCollected = 0;

    private DateTimeFormatter clockFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    public SecurityLogic() {
        registeredStudents = storageHandler.loadRecords();
        int[] fines = new int[1];
        storageHandler.loadVisitors(activeVisitors, lateLeavers, fines);
        totalFinesCollected = fines[0];
    }

    // --- VISITOR MANAGEMENT ---

    public String generateSystemStats() {
        int dayScholars = 0, hostelites = 0, externalHostelites = 0;
        int insideCampus = 0, insideDept = 0, insideHostel = 0;
        java.util.Map<String, Integer> deptCounts = new java.util.HashMap<>();
        java.util.Map<String, Integer> hostelCounts = new java.util.HashMap<>();

        for (Student s : registeredStudents) {
            if ("Day Scholar".equalsIgnoreCase(s.livingStatus)) dayScholars++;
            else if ("Hostelite".equalsIgnoreCase(s.livingStatus)) hostelites++;
            else if ("External Hostelite".equalsIgnoreCase(s.livingStatus)) externalHostelites++;

            if (s.isInsideCampus) insideCampus++;
            if (s.isInsideDepartment) {
                insideDept++;
                String deptName = s.activeDepartmentLocation.isEmpty() ? "Unknown Dept" : s.activeDepartmentLocation;
                deptCounts.put(deptName, deptCounts.getOrDefault(deptName, 0) + 1);
            }
            if (s.isInsideHostel) {
                insideHostel++;
                String hostelName = s.activeHostelLocation.isEmpty() ? "Unknown Hostel" : s.activeHostelLocation;
                hostelCounts.put(hostelName, hostelCounts.getOrDefault(hostelName, 0) + 1);
            }
        }

        int activeVis = activeVisitors.size();
        int lateVis = lateLeavers.size();
        int pendingFines = lateVis * 500;

        StringBuilder sb = new StringBuilder();
        sb.append(" SYSTEM STATISTICS \n\n");
        
        sb.append(" [ STUDENT REGISTRATION ]\n");
        sb.append(String.format("   Total Registered Students:   %d\n", registeredStudents.size()));
        sb.append(String.format("     - Day Scholars:            %d\n", dayScholars));
        sb.append(String.format("     - Internal Hostelites:     %d\n", hostelites));
        sb.append(String.format("     - External Hostelites:     %d\n\n", externalHostelites));

        sb.append(" [ REAL-TIME OCCUPANCY ]\n");
        sb.append(String.format("   Students inside Campus:      %d\n", insideCampus));
        sb.append(String.format("   Students inside Departments: %d\n", insideDept));
        for (java.util.Map.Entry<String, Integer> entry : deptCounts.entrySet()) {
            sb.append(String.format("     - %-22s %d\n", entry.getKey() + ":", entry.getValue()));
        }
        sb.append(String.format("   Students inside Hostels:     %d\n", insideHostel));
        for (java.util.Map.Entry<String, Integer> entry : hostelCounts.entrySet()) {
            sb.append(String.format("     - %-22s %d\n", entry.getKey() + ":", entry.getValue()));
        }
        sb.append("\n");

        sb.append(" [ VISITOR METRICS ]\n");
        sb.append(String.format("   Active Visitors:             %d\n", activeVis));
        sb.append(String.format("   Late Leavers (Overstayed):   %d\n", lateVis));
        sb.append(String.format("   Pending Fines (Unpaid): %d PKR\n", pendingFines));
        sb.append(String.format("   Total Fines Collected:  %d PKR\n", totalFinesCollected));
        sb.append("\n\n");

        return sb.toString();
    }

    public Visitor logVisitorEntry(String cnic) {
        Visitor v = new Visitor(cnic);
        v.timeOfEntry = LocalDateTime.now();
        activeVisitors.add(v);
        storageHandler.saveVisitors(activeVisitors, lateLeavers, totalFinesCollected);
        return v;
    }

    public String logVisitorExit(String cnic, boolean payingFine) {
        // 1. Check if they are already flagged as a late leaver
        for (int i = 0; i < lateLeavers.size(); i++) {
            if (lateLeavers.get(i).getNationalIdCard().equals(cnic)) {
                if (payingFine) {
                    lateLeavers.remove(i);
                    totalFinesCollected += 500;
                    storageHandler.saveVisitors(activeVisitors, lateLeavers, totalFinesCollected);
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
                    storageHandler.saveVisitors(activeVisitors, lateLeavers, totalFinesCollected);
                    return "⚠️ OVERSTAYED (" + minutesPassed + " mins). Added to Late Leavers. 500 PKR fine applies.";
                } else {
                    activeVisitors.remove(i);
                    storageHandler.saveVisitors(activeVisitors, lateLeavers, totalFinesCollected);
                    return "Visitor exited safely. Duration: " + minutesPassed + " mins.";
                }
            }
        }

        return "Error: Visitor not found inside NUST.";
    }


    public ArrayList<Visitor> getActiveVisitors() {
        return activeVisitors;
    }

    public ArrayList<Visitor> getLateLeavers() {
        return lateLeavers;
    }

    // --- STUDENT MANAGEMENT ---

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
                targetStudent.lastCampusEntry = LocalDateTime.now();
                resultMessage = "EXIT RECORDED - NUST MAIN GATE | Name: " + targetStudent.fullName;
            } else {
                targetStudent.isInsideCampus = true;
                targetStudent.lastCampusEntry = LocalDateTime.now();
                resultMessage = "ENTRY RECORDED - NUST MAIN GATE | Name: " + targetStudent.fullName;
            }
        } else {
            // It's a department or hostel location
            if (!targetStudent.isInsideCampus) {
                resultMessage = "ACCESS DENIED: Student must enter NUST Main Gate first!";
            } else {
                if (scanLocation.endsWith("Hostel")) {
                    if ("Day Scholar".equalsIgnoreCase(targetStudent.livingStatus) || "External Hostelite".equalsIgnoreCase(targetStudent.livingStatus)) {
                        return "ACCESS DENIED: Day Scholars and External Hostelites are not allowed in Hostels!";
                    }
                    String specificHostel = scanLocation;
                    if (targetStudent.isInsideHostel && targetStudent.activeHostelLocation.equals(specificHostel)) {
                        targetStudent.isInsideHostel = false;
                        targetStudent.activeHostelLocation = "";
                        targetStudent.lastHostelEntry = LocalDateTime.now();
                        resultMessage = "EXIT RECORDED - " + specificHostel + " | Name: " + targetStudent.fullName;
                    } else {
                        targetStudent.isInsideHostel = true;
                        targetStudent.activeHostelLocation = specificHostel;
                        targetStudent.lastHostelEntry = LocalDateTime.now();
                        resultMessage = "ENTRY RECORDED - " + specificHostel + " | Name: " + targetStudent.fullName;
                    }
                } else {
                    String specificDept = scanLocation; // location is directly the department name from ComboBox
                    if (targetStudent.isInsideDepartment && targetStudent.activeDepartmentLocation.equals(specificDept)) {
                        targetStudent.isInsideDepartment = false;
                        targetStudent.activeDepartmentLocation = "";
                        targetStudent.lastDepartmentEntry = LocalDateTime.now();
                        resultMessage = "EXIT RECORDED - " + specificDept + " | Name: " + targetStudent.fullName;
                    } else {
                        targetStudent.isInsideDepartment = true;
                        targetStudent.activeDepartmentLocation = specificDept;
                        targetStudent.lastDepartmentEntry = LocalDateTime.now();
                        resultMessage = "ENTRY RECORDED - " + specificDept + " | Name: " + targetStudent.fullName;
                    }
                }
            }
        }
        storageHandler.saveRecords(registeredStudents);
        return resultMessage;
    }
}
