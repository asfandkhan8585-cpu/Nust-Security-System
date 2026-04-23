package com.campus.security.model;

import java.time.LocalDateTime;

public class Student extends Person {
    public String fathersName;
    public String fieldOfStudy;
    public String contactNumber;
    public String universityRollNumber;
    public Department enrolledDepartment;

    public String livingStatus;
    public String accommodationName;

    // Location Tracking
    public boolean isInsideCampus = false;
    public boolean isInsideDepartment = false;
    public boolean isInsideHostel = false;

    public String activeDepartmentLocation = "";
    public String activeHostelLocation = "";

    public LocalDateTime lastCampusEntry;
    public LocalDateTime lastDepartmentEntry;
    public LocalDateTime lastHostelEntry;

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

