package com.teamtreehouse.worldbank.util;

import com.teamtreehouse.worldbank.model.Country;
import java.util.ArrayList;
import java.util.List;

public class CountryValidator {

    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public void displayErrors() {
            if (!isValid()) {
                System.out.println("\nValidation errors:");
                errors.forEach(error -> System.out.println("- " + error));
            }
        }
    }

    public ValidationResult validate(Country country) {
        ValidationResult result = new ValidationResult();

        // Validate code
        if (country.getCode() == null || country.getCode().trim().isEmpty()) {
            result.addError("Country code cannot be empty");
        } else if (country.getCode().length() != 3) {
            result.addError("Country code must be exactly 3 characters");
        } else if (!country.getCode().matches("[A-Za-z]{3}")) {
            result.addError("Country code must contain only letters");
        }

        // Validate name
        if (country.getName() == null || country.getName().trim().isEmpty()) {
            result.addError("Country name cannot be empty");
        } else if (country.getName().length() > 32) {
            result.addError("Country name cannot exceed 32 characters");
        }

        // Validate internetUsers
        if (country.getInternetUsers() != null) {
            if (country.getInternetUsers() < 0) {
                result.addError("Internet users percentage cannot be negative");
            }
            if (country.getInternetUsers() > 100) {
                result.addError("Internet users percentage cannot exceed 100");
            }
        }

        // Validate adultLiteracyRate
        if (country.getAdultLiteracyRate() != null) {
            if (country.getAdultLiteracyRate() < 0) {
                result.addError("Adult literacy rate cannot be negative");
            }
            if (country.getAdultLiteracyRate() > 100) {
                result.addError("Adult literacy rate cannot exceed 100");
            }
        }

        return result;
    }

    public ValidationResult validateInput(String input, String fieldName, boolean required) {
        ValidationResult result = new ValidationResult();

        if (required && (input == null || input.trim().isEmpty())) {
            result.addError(fieldName + " is required");
            return result;
        }

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