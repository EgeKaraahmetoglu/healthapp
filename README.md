# Health App - Spring Boot REST API

This Spring Boot application provides a comprehensive REST API for a healthcare question and answer system. It allows patients to ask health-related questions, attach relevant files, and receive answers.

## Features

- Patient management
- Question and answer system
- File attachment support
- RESTful API endpoints
- Comprehensive test suite (unit and integration tests)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for production use)
- Git

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/healthapp.git
cd healthapp
```

### Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE healthdb;
```

2. Update the database configuration in `src/main/resources/application.properties` with your own credentials if needed.

### Build and Run

#### Using Maven

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

#### Using JAR file

```bash
java -jar target/healthapp-1.0-SNAPSHOT.jar
```

### Testing

Run all tests:

```bash
mvn test
```

Run specific test class:

```bash
mvn -Dtest=PatientControllerTest test
```

Run with test profile (using H2 in-memory database):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## Project Structure

- `src/main/java/com/ege/healthapp`: Main application code
  - `controller`: REST API controllers
  - `model`: Entity classes
  - `repository`: Spring Data JPA repositories
  - `service`: Business logic
  - `dto`: Data Transfer Objects
  - `exception`: Custom exceptions

- `src/test/java/com/ege/healthapp`: Test code
  - `controller`: Controller unit tests
  - `integration`: Integration tests
  - `model`: Model unit tests
  - `repository`: Repository tests

## Configuration

### Production

The production configuration uses PostgreSQL and can be customized in `application.properties`.

### Testing

The test configuration uses H2 in-memory database and can be found in `application-test.properties`.
