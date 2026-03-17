package com.chinonso.university_scheduling.exception;

// Thrown whenever a scheduling or enrolment conflict is detected
// e.g. room full, student time clash, wrong course level
public class SchedulingConflictException extends RuntimeException {
    public SchedulingConflictException(String message) {
        super(message);
    }
}
