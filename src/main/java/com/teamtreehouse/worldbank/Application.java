package com.teamtreehouse.worldbank;

import com.teamtreehouse.worldbank.model.Country;
import com.teamtreehouse.worldbank.service.CountryService;
import com.teamtreehouse.worldbank.util.CountryValidator;
import com.teamtreehouse.worldbank.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Scanner;

//TODO jcz: add detailed comments ahead of team presentation.
public class Application {
    private static final CountryService countryService = new CountryService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private static final CountryValidator validator = new CountryValidator();

    public static void main(String[] args) {
        try {
            boolean running = true;
            while (running) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> displayData();
                    case 2 -> countryService.displayStatistics();
                    case 3 -> addCountry();
                    case 4 -> editCountry();
                    case 5 -> deleteCountry();
                    case 6 -> {
                        System.out.println(". . . Shutting down . . . ");
                        running = false;
                    }
                    default -> System.out.println("Sorry, that option is invalid. Please try again.");
                }
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void displayMenu() {
        System.out.println("\nWorld Bank Data Analysis");
        System.out.println("1. View Data Table");
        System.out.println("2. View Statistics");
        System.out.println("3. Add Country");
        System.out.println("4. Edit Country");
        System.out.println("5. Delete Country");
        System.out.println("6. Exit");
        System.out.print("Please enter your choice: ");
    }

    private static void displayData() {
        try (Session session = sessionFactory.openSession()) {
            List<Country> countries = countryService.getAllCountries(session);

            // Display header
            System.out.println("\nCountry                             Internet Users    Literacy");
            System.out.println("--------------------------------------------------------------------");

            // Display data
            countries.forEach(System.out::println);
        }
    }

    private static void addCountry() {
        System.out.println("\nAdd New Country");

        System.out.print("Enter country code (3 characters): ");
        String code = scanner.nextLine().toUpperCase();
        if (code.length() != 3) {
            System.out.println("Error: The country code must be exactly 3 characters.");
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            if (countryService.getCountryByCode(session, code).isPresent()) {
                System.out.println("Error: Country with code '" + code + "' already exists.");
                System.out.println("Please use the edit option to modify existing countries.");
                return;
            }

            System.out.print("Enter country name: ");
            String name = scanner.nextLine();

            System.out.print("Enter internet users per 100 (or press Enter for none): ");
            String internetUsersStr = scanner.nextLine();

            System.out.print("Enter adult literacy rate (or press Enter for none): ");
            String literacyRateStr = scanner.nextLine();

            // Parse and validate numeric inputs
            Double internetUsers = null;
            Double literacyRate = null;

            if (!internetUsersStr.isEmpty()) {
                CountryValidator.ValidationResult internetValidation =
                        validator.validateInput(internetUsersStr, "Internet users", false);
                if (!internetValidation.isValid()) {
                    internetValidation.displayErrors();
                    return;
                }
                internetUsers = Double.parseDouble(internetUsersStr);
            }

            if (!literacyRateStr.isEmpty()) {
                CountryValidator.ValidationResult literacyValidation =
                        validator.validateInput(literacyRateStr, "Literacy rate", false);
                if (!literacyValidation.isValid()) {
                    literacyValidation.displayErrors();
                    return;
                }
                literacyRate = Double.parseDouble(literacyRateStr);
            }

            Country country = new Country.Builder()
                    .code(code)
                    .name(name)
                    .internetUsers(internetUsers)
                    .adultLiteracyRate(literacyRate)
                    .build();

            // Validate the entire country object
            CountryValidator.ValidationResult validation = validator.validate(country);
            if (!validation.isValid()) {
                validation.displayErrors();
                return;
            }

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

    private static void editCountry() {
        System.out.print("\nEnter country code to edit: ");
        String code = scanner.nextLine().toUpperCase();

        try (Session session = sessionFactory.openSession()) {
            countryService.getCountryByCode(session, code).ifPresentOrElse(
                    country -> {
                        System.out.println("Current data: " + country);

                        System.out.print("Enter new name (or press Enter to keep current): ");
                        String name = scanner.nextLine();
                        if (!name.isEmpty()) {
                            country.setName(name);
                        }

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

                        // Validate the entire country object
                        CountryValidator.ValidationResult validation = validator.validate(country);
                        if (!validation.isValid()) {
                            validation.displayErrors();
                            return;
                        }

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

    private static void deleteCountry() {
        System.out.print("\nEnter country code to delete: ");
        String code = scanner.nextLine().toUpperCase();

        try (Session session = sessionFactory.openSession()) {
            countryService.getCountryByCode(session, code).ifPresentOrElse(
                    country -> {
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