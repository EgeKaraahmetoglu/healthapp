# Health App Usage Guide

This document provides a step-by-step guide for using the Health App REST API.

## Setup and Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/healthapp.git
   cd healthapp
   ```

2. **Configure Database**:
   - Ensure PostgreSQL is running
   - Create a database named `healthdb`
   - Update credentials in `src/main/resources/application.properties` if needed

3. **Build and Run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## API Usage Examples

The following examples use `curl` commands to interact with the API. You can also use tools like Postman or the included Swagger UI at `http://localhost:8080/swagger-ui.html`.

### Patient Management

1. **Create a Patient**:
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"name": "John Doe", "email": "john.doe@example.com", "phoneNumber": "123-456-7890"}' http://localhost:8080/api/patients
   ```

2. **Get All Patients**:
   ```bash
   curl -X GET http://localhost:8080/api/patients
   ```

3. **Get Patient by ID**:
   ```bash
   curl -X GET http://localhost:8080/api/patients/1
   ```

4. **Update Patient**:
   ```bash
   curl -X PUT -H "Content-Type: application/json" -d '{"name": "John Doe", "email": "john.updated@example.com", "phoneNumber": "123-456-7890"}' http://localhost:8080/api/patients/1
   ```

5. **Delete Patient**:
   ```bash
   curl -X DELETE http://localhost:8080/api/patients/1
   ```

### Question Management

1. **Create a Question**:
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"content": "What are the symptoms of flu?", "patientId": 1, "attachmentIds": []}' http://localhost:8080/api/questions
   ```

2. **Get All Questions**:
   ```bash
   curl -X GET http://localhost:8080/api/questions
   ```

3. **Get Question by ID**:
   ```bash
   curl -X GET http://localhost:8080/api/questions/1
   ```

4. **Delete Question**:
   ```bash
   curl -X DELETE http://localhost:8080/api/questions/1
   ```

### Attachment Management

1. **Upload an Attachment**:
   ```bash
   curl -X POST -F "file=@path/to/file.pdf" http://localhost:8080/api/attachments/upload
   ```

2. **Download an Attachment**:
   ```bash
   curl -X GET http://localhost:8080/api/attachments/1/download --output file.pdf
   ```

3. **Get All Attachments**:
   ```bash
   curl -X GET http://localhost:8080/api/attachments
   ```

4. **Delete an Attachment**:
   ```bash
   curl -X DELETE http://localhost:8080/api/attachments/1
   ```

### Answer Management

1. **Create an Answer**:
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"content": "Flu symptoms typically include fever, cough, sore throat, and fatigue.", "questionId": 1}' http://localhost:8080/api/answers
   ```

2. **Get Answers for a Question**:
   ```bash
   curl -X GET http://localhost:8080/api/answers/question/1
   ```

## Running Tests

1. **Run All Tests**:
   ```bash
   mvn test
   ```

2. **Run Specific Test**:
   ```bash
   mvn -Dtest=PatientControllerTest test
   ```

3. **Run with Test Profile**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=test
   ```
