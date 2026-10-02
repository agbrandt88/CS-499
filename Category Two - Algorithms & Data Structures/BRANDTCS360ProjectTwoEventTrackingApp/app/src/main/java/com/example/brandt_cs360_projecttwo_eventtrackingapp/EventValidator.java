package com.example.brandt_cs360_projecttwo_eventtrackingapp;

// Supports user event input validation.
public class EventValidator {

    public static int eventNameLengthMax = 100;
    public static int eventNotesLength = 400;

    private EventValidator() {

    }

    public static Validation validation(String name, String date, String time, String endTime, String notes) {
        if (isBlank(name)) {
            return Validation.invalid("Invalid name.");
        }
        if (isBlank(date)) {
            return Validation.invalid("Invalid date.");
        }
        if (isBlank(time)) {
            return Validation.invalid("Invalid time.");
        }
        if (isBlank(endTime)) {
            return Validation.invalid("Invalid end time.");
        }
        if (name.length() > eventNameLengthMax) {
            return Validation.invalid("Invalid character amount.");
        }
        if (notes != null && notes.length() > eventNotesLength) {
            return Validation.invalid("Invalid character amount.");
        }

        return Validation.valid();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Sets up functions for returning validation results.
    public static class Validation {

        private boolean valid;
        private String response;

        private Validation(boolean valid, String response) {
            this.valid = valid;
            this.response = response;
        }

        public static Validation valid() {
            return new Validation(true, null);
        }

        public static Validation invalid(String response) {
            return new Validation(false, response);
        }

        public boolean isValid() {
            return valid;
        }

        public String getResponse() {
            return response;
        }
    }
}
