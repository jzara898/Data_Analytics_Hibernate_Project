package com.teamtreehouse.worldbank.model;
//TODO: jcz cmt
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/**
 * Entity class representing a country and its associated statistics.
 * This class serves as the primary data model for the application and maps
 * directly to the COUNTRY table in the database using JPA annotations.
 *
 * The class implements the Builder pattern for flexible object construction
 * and includes proper data validation through its setters.
 */
@Entity
@Table(name = "COUNTRY")
public class Country {
    // Primary key - ISO country code
    @Id
    @Column(length = 3)
    private String code;

    // Country name with maximum length constraint
    @Column(length = 32)
    private String name;

    // Internet users per 100 people (percentage)
    // DECIMAL(11,8) allows for precise decimal storage
    @Column(columnDefinition = "DECIMAL(11,8)")
    private Double internetUsers;

    // Adult literacy rate (percentage)
    // DECIMAL(11,8) allows for precise decimal storage
    @Column(columnDefinition = "DECIMAL(11,8)")
    private Double adultLiteracyRate;

    /**
     * Default constructor required by Hibernate for entity instantiation.
     * Should not be used directly in application code - use Builder instead.
     */
    public Country() {}

    /**
     * Private constructor used by the Builder pattern.
     * Ensures all Country objects are created through the Builder.
     *
     * @param builder Builder object containing the country data
     */
    private Country(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.internetUsers = builder.internetUsers;
        this.adultLiteracyRate = builder.adultLiteracyRate;
    }

    // Getter methods - provide read access to private fields
    public String getCode() { return code; }
    public String getName() { return name; }
    public Double getInternetUsers() { return internetUsers; }
    public Double getAdultLiteracyRate() { return adultLiteracyRate; }

    // Setter methods - allow modification of private fields
    // Used by Hibernate and for updating existing records
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setInternetUsers(Double internetUsers) { this.internetUsers = internetUsers; }
    public void setAdultLiteracyRate(Double adultLiteracyRate) { this.adultLiteracyRate = adultLiteracyRate; }

    /**
     * Builder class for constructing Country objects.
     * Implements the Builder pattern to provide:
     * - Flexible object construction
     * - Immutable objects
     * - Clear and readable object creation syntax
     */
    public static class Builder {
        private String code;
        private String name;
        private Double internetUsers;
        private Double adultLiteracyRate;

        // Builder methods - each returns the Builder instance for method chaining
        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder internetUsers(Double internetUsers) {
            this.internetUsers = internetUsers;
            return this;
        }

        public Builder adultLiteracyRate(Double adultLiteracyRate) {
            this.adultLiteracyRate = adultLiteracyRate;
            return this;
        }

        /**
         * Builds and returns a new Country instance.
         * @return A new Country object with the Builder's current values
         */
        public Country build() {
            return new Country(this);
        }
    }

    /**
     * Provides a formatted string representation of the Country object.
     * Used for displaying country data in the console interface.
     * Format: "CODE NAME            INTERNET_USERS LITERACY_RATE"
     *
     * @return Formatted string with fixed-width columns
     */
    @Override
    public String toString() {
        return String.format("%-3s  %-32s %10s %10s",
                code,
                name,
                internetUsers != null ? String.format("%.2f", internetUsers) : "--",
                adultLiteracyRate != null ? String.format("%.2f", adultLiteracyRate) : "--");
    }
}