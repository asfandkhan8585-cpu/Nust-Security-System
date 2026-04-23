package com.campus.security.model;

public abstract class Person {
    public String nationalIdCard;
    public String fullName;

    public Person(String nationalIdCard, String fullName) {
        this.nationalIdCard = nationalIdCard;
        this.fullName = fullName;
    }

    public String getNationalIdCard() {
        return nationalIdCard;
    }
}
// this class stores and returns the student credentials
