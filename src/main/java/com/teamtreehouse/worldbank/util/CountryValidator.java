package com.teamtreehouse.worldbank.util;

import com.teamtreehouse.worldbank.model.Country;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates Country objects and input data for country operations.
 * Ensures data integrity before database operations.
 */
public class CountryValidator {

    /**
     * Inner class to hold validation results and manage error messages.
     * Implements a collection of validation errors with utility methods.
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();

        /**
         * Adds a validation error to the collection.
         * @param error The error message to add
         */
        public void addError(String error) {
            errors.add(error);
        }

        /**
         * Checks if validation passed (no errors present).
         * @return true if validation passed, false otherwise
         */
        public boolean isValid() {
            return errors.isEmpty();
        }

        /**
         * Returns a defensive copy of the error list.
         * @return List of validation error messages
         */
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        /**
         * Displays validation errors to console if present.
         * Formats errors for user readability.
         */
        public void displayErrors() {
            if (!isValid()) {
                System.out.println("\nValidation errors:");
                errors.forEach(error -> System.out.println("- " + error));
            }
        }
    }

    /**
     * Validates all fields of a Country object.
     * Checks for required fields and data constraints.
     * @param country The Country object to validate
     * @return ValidationResult containing any validation errors
     */
    public ValidationResult validate(Country country) {
        ValidationResult result = new ValidationResult();

        // Validate country code: must be 3 letters
        if (country.getCode() == null || country.getCode().trim().isEmpty()) {
            result.addError("Country code cannot be empty.");
        } else if (country.getCode().length() != 3) {
            result.addError("Country code must be exactly 3 characters.");
        } else if (!country.getCode().matches("[A-Za-z]{3}")) {
            result.addError("Country code must contain only letters.");
        }

        // Validate country name: non-empty, max 32 chars
        if (country.getName() == null || country.getName().trim().isEmpty()) {
            result.addError("Country name cannot be empty.");
        } else if (country.getName().length() > 32) {
            result.addError("Country name cannot exceed 32 characters.");
        }

        // Validate internet users percentage: 0-100 if present
        if (country.getInternetUsers() != null) {
            if (country.getInternetUsers() < 0) {
                result.addError("Internet users percentage cannot be negative.");
            }
            if (country.getInternetUsers() > 100) {
                result.addError("Internet users percentage cannot exceed 100.");
            }
        }

        // Validate literacy rate: 0-100 if present
        if (country.getAdultLiteracyRate() != null) {
            if (country.getAdultLiteracyRate() < 0) {
                result.addError("Adult literacy rate cannot be negative.");
            }
            if (country.getAdultLiteracyRate() > 100) {
                result.addError("Adult literacy rate cannot exceed 100.");
            }
        }

        return result;
    }

    /**
     * Validates numeric input strings for percentage fields.
     * @param input The input string to validate
     * @param fieldName Name of the field for error messages
     * @param required Whether the field is required
     * @return ValidationResult containing any validation errors
     */
    public ValidationResult validateInput(String input, String fieldName, boolean required) {
        ValidationResult result = new ValidationResult();

        // Check if required field is present
        if (required && (input == null || input.trim().isEmpty())) {
            result.addError(fieldName + " is required");
            return result;
        }

        // Validate numeric input and range if value provided
        if (input != null && !input.trim().isEmpty()) {
            try {
                double value = Double.parseDouble(input);
                if (value < 0 || value > 100) {
                    result.addError(fieldName + " must be between 0 and 100");
                }
            } catch (NumberFormatException e) {
                result.addError(fieldName + " must be a valid number");
            }
        }

        return result;
    }
}