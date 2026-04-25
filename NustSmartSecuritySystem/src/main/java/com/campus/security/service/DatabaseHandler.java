package com.campus.security.service;

import com.campus.security.model.*;

import java.io.*;
import java.util.ArrayList;

public class DatabaseHandler {
    private final String DATA_FILE = "nust_student_records.txt";

    public boolean saveRecords(ArrayList<Student> studentList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (int i = 0; i < studentList.size(); i++) {
                Student s = studentList.get(i);
                // Saving one property per line
                writer.write(s.getNationalIdCard() + "\n");
                writer.write(s.fullName + "\n");
                writer.write(s.fathersName + "\n");
                writer.write(s.enrolledDepartment.getDepartmentName() + "\n");
                writer.write(s.fieldOfStudy + "\n");
                writer.write(s.contactNumber + "\n");
                writer.write(s.universityRollNumber + "\n");
                writer.write(s.livingStatus + "\n");
                writer.write(s.accommodationName + "\n");

                // Saving tracking states as simple "true" or "false" strings
                writer.write((s.isInsideCampus ? "true" : "false") + "\n");
                writer.write((s.isInsideDepartment ? "true" : "false") + "\n");
                writer.write((s.isInsideHostel ? "true" : "false") + "\n");

                writer.write((s.activeDepartmentLocation.isEmpty() ? "none" : s.activeDepartmentLocation) + "\n");
                writer.write((s.activeHostelLocation.isEmpty() ? "none" : s.activeHostelLocation) + "\n");
            }
            return true;
        } catch (IOException error) {
            System.err.println("Error saving to database: " + error.getMessage());
            error.printStackTrace();
            return false;
        }
    }

    public ArrayList<Student> loadRecords() {
        ArrayList<Student> loadedData = new ArrayList<>();
        File databaseFile = new File(DATA_FILE);

        if (!databaseFile.exists()) {
            return loadedData;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String cnicLine;
            // Read the first line (CNIC). If it's not null, read the rest of the lines for that student
            while ((cnicLine = reader.readLine()) != null) {
                String name = reader.readLine();
                String fname = reader.readLine();
                String deptName = reader.readLine();
                String field = reader.readLine();
                String contact = reader.readLine();
                String rollNo = reader.readLine();
                String living = reader.readLine();
                String hostelName = reader.readLine();

                String insideCamp = reader.readLine();
                String insideDept = reader.readLine();
                String insideHostel = reader.readLine();

                String activeDept = reader.readLine();
                String activeHostel = reader.readLine();

                Department dept = resolveDepartment(deptName);

                Student s = new Student(cnicLine, name, fname, dept, field, contact, rollNo, living, hostelName);

                // Set the states manually using simple string comparison (no parsing needed)
                if (insideCamp != null && insideCamp.equals("true")) s.isInsideCampus = true;
                if (insideDept != null && insideDept.equals("true")) s.isInsideDepartment = true;
                if (insideHostel != null && insideHostel.equals("true")) s.isInsideHostel = true;

                if (activeDept != null && !activeDept.equals("none")) s.activeDepartmentLocation = activeDept;
                if (activeHostel != null && !activeHostel.equals("none")) s.activeHostelLocation = activeHostel;

                loadedData.add(s);
            }
        } catch (IOException error) {
            System.err.println("Error loading from database: " + error.getMessage());
            error.printStackTrace();
        }
        return loadedData;
    }

    private Department resolveDepartment(String deptName) {
        if (deptName == null) return new SCME();

        switch (deptName) {
            case "SEECS": return new SEECS();
            case "SMME": return new SMME();
            case "NBS": return new NBS();
            case "S3H": return new S3H();
            case "ASAB": return new ASAB();
            default: return new SCME();
        }
    }
}

