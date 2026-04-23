package com.campus.security.model;

import java.time.LocalDateTime;

public class Visitor extends Person {
    public LocalDateTime timeOfEntry;
    public boolean hasOverstayed = false;

    public Visitor(String nationalIdCard) {
        super(nationalIdCard, "Temporary Visitor");
    }
}

