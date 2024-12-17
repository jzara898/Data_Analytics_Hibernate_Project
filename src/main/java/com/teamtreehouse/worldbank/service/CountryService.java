package com.teamtreehouse.worldbank.service;

import com.teamtreehouse.worldbank.model.Country;
import com.teamtreehouse.worldbank.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.DoubleSummaryStatistics;
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
            throw new IllegalStateException("Country with code '" + country.getCode() + "' already exists.");
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

            // Using Java streams for calculations
            DoubleSummaryStatistics internetStats = countries.stream()
                    .filter(c -> c.getInternetUsers() != null)
                    .mapToDouble(Country::getInternetUsers)
                    .summaryStatistics();

            DoubleSummaryStatistics literacyStats = countries.stream()
                    .filter(c -> c.getAdultLiteracyRate() != null)
                    .mapToDouble(Country::getAdultLiteracyRate)
                    .summaryStatistics();

            System.out.println("\nStatistics:");
            System.out.println("Internet Users (per 100):");
            System.out.printf("  Maximum: %.2f\n", internetStats.getMax());
            System.out.printf("  Minimum: %.2f\n", internetStats.getMin());
            System.out.printf("  Average: %.2f\n", internetStats.getAverage());

            System.out.println("\nAdult Literacy Rate:");
            System.out.printf("  Maximum: %.2f\n", literacyStats.getMax());
            System.out.printf("  Minimum: %.2f\n", literacyStats.getMin());
            System.out.printf("  Average: %.2f\n", literacyStats.getAverage());
        }
    }
}