package com.teamtreehouse.worldbank.service;

import com.teamtreehouse.worldbank.model.Country;
import com.teamtreehouse.worldbank.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CountryService {

    public List<Country> getAllCountries(Session session) {
        List<Country> countries = session.createQuery("from Country", Country.class).list();
        return countries.stream()
                .sorted(Comparator.comparing(Country::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<Country> getCountryByCode(Session session, String code) {
        return Optional.ofNullable(session.get(Country.class, code));
    }

    public void saveCountry(Session session, Country country) {
        // First check if country code already exists
        if (getCountryByCode(session, country.getCode()).isPresent()) {
            throw new IllegalStateException(" Sorry, country with code '" + country.getCode() + "' already exists.");
        }

        Transaction transaction = session.beginTransaction();
        try {
            session.persist(country);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            if (e.getMessage().contains("primary key violation")) {
                throw new IllegalStateException("Country code '" + country.getCode() + "' already exists in database.");
            }
            throw e;
        }
    }

    public void updateCountry(Session session, Country country) {
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(country);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new IllegalStateException("Error updating country: " + e.getMessage());
        }
    }

    public void deleteCountry(Session session, String code) {
        Transaction transaction = session.beginTransaction();
        try {
            Country country = session.get(Country.class, code);
            if (country != null) {
                session.remove(country);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new IllegalStateException("Error deleting country: " + e.getMessage());
        }
    }

    public void displayStatistics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Country> countries = getAllCountries(session);

            // For Internet Users
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

            // For Literacy Rate
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

            // Display results
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