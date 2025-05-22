# Literacy Data Management System - WorldBank Data

A Java-based console application for managing country data with statistical analysis capabilities, built using Hibernate ORM and H2 database.

## Overview

This project demonstrates a complete data management solution that allows users to perform CRUD operations on country data while providing statistical insights. The application showcases modern Java development practices including the use of build tools, ORM frameworks, and design patterns.

## Features

- **Data Management**: Complete CRUD operations for country records
- **Statistical Analysis**: Calculate mean, minimum, and maximum values for country indicators
- **Data Presentation**: Well-formatted table display with proper column alignment
- **User Interface**: Interactive console menu system
- **Data Persistence**: Hibernate ORM with H2 in-memory database
- **Build Management**: Gradle-based project structure

## Technology Stack

- **Java**: Core programming language
- **Hibernate ORM**: Object-relational mapping and persistence
- **H2 Database**: In-memory database for development and testing
- **Gradle**: Build automation and dependency management
- **Java Streams**: Advanced data processing and statistics

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── [source code]
│   └── resources/
│       └── hibernate.cfg.xml
└── build.gradle

```

## Prerequisites

- Java 8 or higher
- Gradle (or use included Gradle wrapper)

## Setup and Installation

1. **Clone the repository**
    
    ```bash
    git clone [repository-url]
    cd country-data-management
    
    ```
    
2. **Build the project**
    
    ```bash
    ./gradlew build
    
    ```
    
3. **Run the application**
    
    ```bash
    ./gradlew run
    
    ```
    

## Configuration

The application uses Hibernate configuration specified in `src/main/resources/hibernate.cfg.xml`:

- **Database**: H2 in-memory database
- **Connection URL**: Configured for local development
- **Entity Mapping**: Automatic mapping of Country class
- **SQL Dialect**: H2 database dialect

## Usage

### Main Menu Options

1. **View Data Table**: Display all countries in a formatted table
    - Column headers included
    - Numeric values formatted to 2 decimal places
    - NULL values displayed as "--"
2. **View Statistics**: Calculate and display statistical metrics
    - Average (mean) values for each indicator
    - Minimum and maximum values using Java Streams
    - Real-time calculations based on current data
3. **Add Country**: Create new country records
    - Interactive input prompts
    - Data validation and persistence
4. **Edit Country**: Modify existing country data
    - Search and select country to edit
    - Update individual fields
    - Immediate database persistence
5. **Delete Country**: Remove country records
    - Confirmation prompts
    - Safe deletion with database cleanup

### Data Format

Countries are stored with the following attributes:

- Country identifier
- Numeric indicators (formatted to 2 decimal places)
- Proper handling of NULL/missing values

## Technical Highlights

### Design Patterns

- **Builder Pattern**: Used for creating new Country entities, providing a clean and flexible object construction approach

### Database Operations

- **Hibernate Session Management**: Proper session lifecycle management
- **Transaction Handling**: Ensures data consistency and integrity
- **Entity Mapping**: Clean object-relational mapping configuration

### Data Processing

- **Java Streams**: Advanced statistical calculations using functional programming concepts
- **Data Validation**: Input validation and error handling
- **Formatting**: Consistent data presentation with proper number formatting

## Development Notes

### Code Quality

- Clean, maintainable code structure
- Proper separation of concerns
- Comprehensive error handling
- Well-documented configuration

### Build System

- Gradle integration with necessary dependencies
- Standard directory structure following Gradle Java plugin conventions
- Automated build and dependency management

## Future Enhancements

Potential areas for expansion:

- Additional statistical functions
- Data export capabilities
- Web-based user interface
- Database persistence options
- Data import from external sources

## Dependencies

Key project dependencies managed through Gradle:

- Hibernate Core
- H2 Database Engine
- JUnit (for testing)
- Additional utility libraries as needed

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request


## Contact

email: j2carter@ucsd.edu

---

*This project demonstrates practical application of Java enterprise development concepts including ORM frameworks, database management, and modern build tools.*
