package com.teamtreehouse.worldbank.service;

import com.teamtreehouse.worldbank.model.Country;
import com.teamtreehouse.worldbank.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Service class that handles all business logic and data operations for Country entities.
 * This class serves as an intermediary between the UI layer and data persistence layer,
 * implementing CRUD operations and statistical analysis of country data.
 *
 * Key responsibilities:
 * - Data retrieval and persistence
 * - Transaction management
 * - Data analysis and statistics
 * - Error handling and validation
 */
public class CountryService {

    /**
     * Retrieves all countries from the database and sorts them alphabetically by name.
     * Uses Hibernate's session to query the database and Java streams for sorting.
     *
     * @param session Active Hibernate session
     * @return List of Country objects sorted by name (case-insensitive)
     */
    public List<Country> getAllCountries(Session session) {
        List<Country> countries = session.createQuery("from Country", Country.class).list();
        return countries.stream()
                .sorted(Comparator.comparing(Country::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific country by its code.
     * Uses Optional to handle potential null returns elegantly.
     *
     * @param session Active Hibernate session
     * @param code Three-letter country code
     * @return Optional containing the Country if found, empty Optional otherwise
     */
    public Optional<Country> getCountryByCode(Session session, String code) {
        return Optional.ofNullable(session.get(Country.class, code));
    }

    /**
     * Saves a new country to the database.
     * Implements proper transaction management and duplicate checking.
     *
     * @param session Active Hibernate session
     * @param country Country object to be saved
     * @throws IllegalStateException if country code already exists
     */
    public void saveCountry(Session session, Country country) {
        // Defensive check for existing country code
        if (getCountryByCode(session, country.getCode()).isPresent()) {
            throw new IllegalStateException(" Sorry, country with code '" + country.getCode() + "' already exists.");
        }

        Transaction transaction = session.beginTransaction();
        try {
            // Persist the new country object
            session.persist(country);
            transaction.commit();
        } catch (Exception e) {
            // Rollback on any error
            transaction.rollback();
            // Provide specific error message for primary key violations
            if (e.getMessage().contains("primary key violation")) {
                throw new IllegalStateException("Country code '" + country.getCode() + "' already exists in database.");
            }
            throw e;
        }
    }

    /**
     * Updates an existing country in the database.
     * Implements transaction management for safe updates.
     *
     * @param session Active Hibernate session
     * @param country Updated Country object
     * @throws IllegalStateException if update fails
     */
    public void updateCountry(Session session, Country country) {
        Transaction transaction = session.beginTransaction();
        try {
            // Merge the updated country object
            session.merge(country);
            transaction.commit();
        } catch (Exception e) {
            // Rollback on any error
            transaction.rollback();
            throw new IllegalStateException("Error updating country: " + e.getMessage());
        }
    }

    /**
     * Deletes a country from the database by its code.
     * Implements safe transaction management for deletion.
     *
     * @param session Active Hibernate session
     * @param code Three-letter country code to delete
     * @throws IllegalStateException if deletion fails
     */
    public void deleteCountry(Session session, String code) {
        Transaction transaction = session.beginTransaction();
        try {
            Country country = session.get(Country.class, code);
            if (country != null) {
                session.remove(country);
            }
            transaction.commit();
        } catch (Exception e) {
            // Rollback on any error
            transaction.rollback();
            throw new IllegalStateException("Error deleting country: " + e.getMessage());
        }
    }

    /**
     * Calculates and displays statistical analysis of country data.
     * Uses Java streams for efficient data processing.
     * Analyzes both internet usage and literacy rate metrics.
     *
     * Statistics calculated:
     * - Maximum values with associated countries
     * - Minimum values with associated countries
     * - Average values across all countries
     */
    public void displayStatistics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Country> countries = getAllCountries(session);

            // Calculate statistics for Internet Users
            Country maxInternetCountry = countries.stream()
                    .filter(c -> c.getInternetUsers() != null)
                    .max(Comparator.comparing(Country::getInternetUsers))
                    .orElse(null);

            Country minInternetCountry = countries.stream()
                    .filter(c -> c.getInternetUsers() != null)
                    .min(Comparator.comparing(Country::getInternetUsers))
                    .orElse(null);

            double avgInternet = countries.stream()
                    .filter(c -> c.getInternetUsers() != null)
                    .mapToDouble(Country::getInternetUsers)
                    .average()
                    .orElse(0.0);

            // Calculate statistics for Literacy Rate
            Country maxLiteracyCountry = countries.stream()
                    .filter(c -> c.getAdultLiteracyRate() != null)
                    .max(Comparator.comparing(Country::getAdultLiteracyRate))
                    .orElse(null);

            Country minLiteracyCountry = countries.stream()
                    .filter(c -> c.getAdultLiteracyRate() != null)
                    .min(Comparator.comparing(Country::getAdultLiteracyRate))
                    .orElse(null);

            double avgLiteracy = countries.stream()
                    .filter(c -> c.getAdultLiteracyRate() != null)
                    .mapToDouble(Country::getAdultLiteracyRate)
                    .average()
                    .orElse(0.0);

            // Format and display results
            System.out.println("\nStatistics:");
            System.out.println("Internet Users (per 100):");
            if (maxInternetCountry != null) {
                System.out.printf("  Maximum: %.2f (%s)\n",
                        maxInternetCountry.getInternetUsers(),
                        maxInternetCountry.getName());
            }
            if (minInternetCountry != null) {
                System.out.printf("  Minimum: %.2f (%s)\n",
                        minInternetCountry.getInternetUsers(),
                        minInternetCountry.getName());
            }
            System.out.printf("  Average: %.2f\n", avgInternet);

            System.out.println("\nAdult Literacy Rate:");
            if (maxLiteracyCountry != null) {
                System.out.printf("  Maximum: %.2f (%s)\n",
                        maxLiteracyCountry.getAdultLiteracyRate(),
                        maxLiteracyCountry.getName());
            }
            if (minLiteracyCountry != null) {
                System.out.printf("  Minimum: %.2f (%s)\n",
                        minLiteracyCountry.getAdultLiteracyRate(),
                        minLiteracyCountry.getName());
            }
            System.out.printf("  Average: %.2f\n", avgLiteracy);
        }
    }
}