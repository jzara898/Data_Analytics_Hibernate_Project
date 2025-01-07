package com.teamtreehouse.worldbank.model;
//TODO: jcz cmt
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "COUNTRY")
public class Country {
    @Id
    @Column(length = 3)
    private String code;

    @Column(length = 32)
    private String name;

    @Column(columnDefinition = "DECIMAL(11,8)")
    private Double internetUsers;

    @Column(columnDefinition = "DECIMAL(11,8)")
    private Double adultLiteracyRate;

    // Default constructor required by Hibernate
    public Country() {}

    private Country(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.internetUsers = builder.internetUsers;
        this.adultLiteracyRate = builder.adultLiteracyRate;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public Double getInternetUsers() { return internetUsers; }
    public Double getAdultLiteracyRate() { return adultLiteracyRate; }

    // Setters
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setInternetUsers(Double internetUsers) { this.internetUsers = internetUsers; }
    public void setAdultLiteracyRate(Double adultLiteracyRate) { this.adultLiteracyRate = adultLiteracyRate; }

    // Builder class
    public static class Builder {
        private String code;
        private String name;
        private Double internetUsers;
        private Double adultLiteracyRate;

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

        public Country build() {
            return new Country(this);
        }
    }

    @Override
    public String toString() {
        return String.format("%-3s  %-32s %10s %10s",
                code,
                name,
                internetUsers != null ? String.format("%.2f", internetUsers) : "--",
                adultLiteracyRate != null ? String.format("%.2f", adultLiteracyRate) : "--");
    }
}