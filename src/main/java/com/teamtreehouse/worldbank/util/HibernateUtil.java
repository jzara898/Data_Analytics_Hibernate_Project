package com.teamtreehouse.worldbank.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Utility class for managing Hibernate SessionFactory lifecycle.
 * Implements the Singleton pattern to ensure only one SessionFactory instance exists.
 */
public class HibernateUtil {
    // Static registry instance for Hibernate service configuration
    private static StandardServiceRegistry registry;
    // Static SessionFactory instance following Singleton pattern
    private static SessionFactory sessionFactory;

    /**
     * Gets or creates the Hibernate SessionFactory.
     * Implements lazy initialization for the SessionFactory.
     * @return The singleton SessionFactory instance
     * @throws RuntimeException if initialization fails
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry builder for Hibernate configuration
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

                // Load Hibernate configuration from hibernate.cfg.xml
                registryBuilder.configure("hibernate.cfg.xml");

                // Build the service registry with loaded configuration
                registry = registryBuilder.build();

                // Create metadata sources from the registry
                MetadataSources sources = new MetadataSources(registry);

                // Build metadata from sources
                Metadata metadata = sources.getMetadataBuilder().build();

                // Create the SessionFactory from metadata
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                // Log the error and clean up resources if initialization fails
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                throw new RuntimeException("Error initializing Hibernate SessionFactory: " + e.getMessage(), e);
            }
        }
        return sessionFactory;
    }

    /**
     * Cleanly shuts down Hibernate by destroying the registry.
     * Should be called when the application terminates.
     */
    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}