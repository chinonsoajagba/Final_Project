package com.chinonso.university_scheduling.exception;

public class SchedulingConflictException extends RuntimeException {
    public SchedulingConflictException(String message) {
        super(message);
    }
}
