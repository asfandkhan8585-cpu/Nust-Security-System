package com.campus.security.model;

import java.time.LocalDateTime;

public class Student extends Person {
    String fathersName;
    String fieldOfStudy;
    String contactNumber;
    String universityRollNumber;
    Department enrolledDepartment;

    String livingStatus;
    String accommodationName;

    // Location Tracking
    boolean isInsideCampus = false;
    boolean isInsideDepartment = false;
    boolean isInsideHostel = false;

    String activeDepartmentLocation = "";
    String activeHostelLocation = "";

    LocalDateTime lastCampusEntry;
    LocalDateTime lastDepartmentEntry;
    LocalDateTime lastHostelEntry;

    public Student(String nationalIdCard, String fullName, String fathersName,
                   Department enrolledDepartment, String fieldOfStudy, String contactNumber,
                   String universityRollNumber, String livingStatus, String accommodationName) {
        super(nationalIdCard, fullName);
        this.fathersName = fathersName;
        this.enrolledDepartment = enrolledDepartment;
        this.fieldOfStudy = fieldOfStudy;
        this.contactNumber = contactNumber;
        this.universityRollNumber = universityRollNumber;
        this.livingStatus = livingStatus;
        this.accommodationName = accommodationName;
    }
}

