package com.teamtreehouse.worldbank;

import com.teamtreehouse.worldbank.model.Country;
import com.teamtreehouse.worldbank.service.CountryService;
import com.teamtreehouse.worldbank.util.CountryValidator;
import com.teamtreehouse.worldbank.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the World Bank Data Analysis program.
 * This class implements a command-line interface for users to interact with World Bank data
 * about internet usage and adult literacy rates across different countries.
 *
 * Key features:
 * - View formatted data table of all countries
 * - Display statistical analysis
 * - Add new country records
 * - Edit existing country data
 * - Delete country records
 */
public class Application {
    // Core service and utility instances used throughout the application
    private static final CountryService countryService = new CountryService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private static final CountryValidator validator = new CountryValidator();

    /**
     * Main entry point of the application.
     * Implements the main program loop and menu-driven interface.
     */
    public static void main(String[] args) {
        try {
            boolean running = true;
            while (running) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline after numeric input

                // Route user input to appropriate handler method
                switch (choice) {
                    case 1 -> displayData();
                    case 2 -> countryService.displayStatistics();
                    case 3 -> addCountry();
                    case 4 -> editCountry();
                    case 5 -> deleteCountry();
                    case 6 -> {
                        System.out.println(" * * * Shutting down * * * ");
                        running = false;
                    }
                    default -> System.out.println("Sorry, that option is invalid. Please try again.");
                }
            }
        } finally {
            // Ensure proper cleanup of Hibernate resources
            HibernateUtil.shutdown();
        }
    }

    /**
     * Displays the main menu options to the user.
     * Shows numbered list of available operations.
     */
    private static void displayMenu() {
        System.out.println("\n====World Bank Data Analysis====");
        System.out.println("1. View Data Table");
        System.out.println("2. View Statistics");
        System.out.println("3. Add Country");
        System.out.println("4. Edit Country");
        System.out.println("5. Delete Country");
        System.out.println("6. Exit");
        System.out.print("\nPlease enter your choice: \n");
    }

    /**
     * Displays formatted table of all country data.
     * Uses a Hibernate session to retrieve and display country information.
     * Formats output with fixed-width columns for readability.
     */
    private static void displayData() {
        try (Session session = sessionFactory.openSession()) {
            List<Country> countries = countryService.getAllCountries(session);

            // Format header with consistent column widths
            System.out.println("\nCode  Country                           Internet Users    Literacy");
            System.out.println("------------------------------------------------------------------");

            // Leverage Country.toString() for consistent formatting
            countries.forEach(System.out::println);
        }
    }

    /**
     * Handles the addition of a new country record.
     * Implements input validation and data persistence.
     * Process:
     * 1. Collect and validate country code
     * 2. Check for existing records
     * 3. Collect remaining country data
     * 4. Validate numeric inputs
     * 5. Create and persist new country record
     */
    private static void addCountry() {
        System.out.println("\nAdd New Country");

        // Validate country code format
        System.out.print("Enter country code (3 characters): ");
        String code = scanner.nextLine().toUpperCase();
        if (code.length() != 3) {
            System.out.println("Error: The country code must be exactly 3 characters.");
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            // Check for duplicate country codes
            if (countryService.getCountryByCode(session, code).isPresent()) {
                System.out.println("Error: Country with code '" + code + "' already exists.");
                System.out.println("Please use the edit option to modify existing countries.");
                return;
            }

            // Collect remaining country data
            System.out.print("Enter country name: ");
            String name = scanner.nextLine();

            System.out.print("Enter internet users per 100 (or press Enter for none): ");
            String internetUsersStr = scanner.nextLine();

            System.out.print("Enter adult literacy rate (or press Enter for none): ");
            String literacyRateStr = scanner.nextLine();

            // Parse and validate numeric inputs
            Double internetUsers = null;
            Double literacyRate = null;

            // Validate internet users input if provided
            if (!internetUsersStr.isEmpty()) {
                CountryValidator.ValidationResult internetValidation =
                        validator.validateInput(internetUsersStr, "Internet users", false);
                if (!internetValidation.isValid()) {
                    internetValidation.displayErrors();
                    return;
                }
                internetUsers = Double.parseDouble(internetUsersStr);
            }

            // Validate literacy rate input if provided
            if (!literacyRateStr.isEmpty()) {
                CountryValidator.ValidationResult literacyValidation =
                        validator.validateInput(literacyRateStr, "Literacy rate", false);
                if (!literacyValidation.isValid()) {
                    literacyValidation.displayErrors();
                    return;
                }
                literacyRate = Double.parseDouble(literacyRateStr);
            }

            // Create new country using Builder pattern
            Country country = new Country.Builder()
                    .code(code)
                    .name(name)
                    .internetUsers(internetUsers)
                    .adultLiteracyRate(literacyRate)
                    .build();

            // Validate complete country object
            CountryValidator.ValidationResult validation = validator.validate(country);
            if (!validation.isValid()) {
                validation.displayErrors();
                return;
            }

            // Attempt to save the new country
            try {
                countryService.saveCountry(session, country);
                System.out.println("Country add successful.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Sorry, there was an error adding country: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the editing of existing country records.
     * Implements field-by-field updates with validation.
     * Allows users to keep existing values by pressing Enter.
     */
    private static void editCountry() {
        System.out.print("\nEnter country code to edit: ");
        String code = scanner.nextLine().toUpperCase();

        try (Session session = sessionFactory.openSession()) {
            // Use Optional to handle country lookup elegantly
            countryService.getCountryByCode(session, code).ifPresentOrElse(
                    country -> {
                        System.out.println("Current data: " + country);

                        // Handle name update
                        System.out.print("Enter new name (or press Enter to keep current): ");
                        String name = scanner.nextLine();
                        if (!name.isEmpty()) {
                            country.setName(name);
                        }

                        // Handle internet users update
                        System.out.print("Enter new internet users value (or press Enter to keep current): ");
                        String internetUsersStr = scanner.nextLine();
                        if (!internetUsersStr.isEmpty()) {
                            CountryValidator.ValidationResult internetValidation =
                                    validator.validateInput(internetUsersStr, "Internet users", false);
                            if (!internetValidation.isValid()) {
                                internetValidation.displayErrors();
                                return;
                            }
                            country.setInternetUsers(Double.parseDouble(internetUsersStr));
                        }

                        // Handle literacy rate update
                        System.out.print("Enter new literacy rate (or press Enter to keep current): ");
                        String literacyRateStr = scanner.nextLine();
                        if (!literacyRateStr.isEmpty()) {
                            CountryValidator.ValidationResult literacyValidation =
                                    validator.validateInput(literacyRateStr, "Literacy rate", false);
                            if (!literacyValidation.isValid()) {
                                literacyValidation.displayErrors();
                                return;
                            }
                            country.setAdultLiteracyRate(Double.parseDouble(literacyRateStr));
                        }

                        // Validate updated country object
                        CountryValidator.ValidationResult validation = validator.validate(country);
                        if (!validation.isValid()) {
                            validation.displayErrors();
                            return;
                        }

                        // Attempt to persist updates
                        try {
                            countryService.updateCountry(session, country);
                            System.out.println("Country update successful.");
                        } catch (Exception e) {
                            System.out.println("Sorry, there was an error updating country: " + e.getMessage());
                        }
                    },
                    () -> System.out.println("No country found.")
            );
        }
    }

    /**
     * Handles the deletion of country records.
     * Implements confirmation step to prevent accidental deletions.
     */
    private static void deleteCountry() {
        System.out.print("\nEnter country code to delete: ");
        String code = scanner.nextLine().toUpperCase();

        try (Session session = sessionFactory.openSession()) {
            // Use Optional to handle country lookup elegantly
            countryService.getCountryByCode(session, code).ifPresentOrElse(
                    country -> {
                        // Show country data and require explicit confirmation
                        System.out.println("Are you sure you want to delete: " + country);
                        System.out.print("Enter EXACTLY 'YES' to confirm: ");
                        String confirmation = scanner.nextLine();

                        if (confirmation.equals("YES")) {
                            try {
                                countryService.deleteCountry(session, code);
                                System.out.println("Country delete successful.");
                            } catch (Exception e) {
                                System.out.println("Sorry, there was an error deleting country: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Deletion is cancelled.  Please try again.");
                        }
                    },
                    () -> System.out.println("No country found.")
            );
        }
    }
}