# core-service

Spring Boot 3.2.x service that exposes REST APIs for user CRUD operations for the React MFE frontend.

## Tech Stack
- Java 17
- Spring Boot 3.2.12
- Maven
- H2 in-memory database
- Spring Data JPA / Hibernate
- SpringDoc OpenAPI / Swagger UI
- Spring Boot Actuator
- JUnit 5 / Mockito / Spring Boot Test
- Lombok

## Project Structure
```
core-service/
├── src/
│   ├── main/
│   │   ├── java/com/ashishtestorg/coreservice/
│   │   │   ├── CoreServiceApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```

## Getting Started
### Prerequisites
- Java 17+
- Maven 3.9+

### Run locally
```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

### Useful URLs
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI docs: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`
- H2 console: `http://localhost:8080/h2-console`

### Build and test
```bash
mvn clean verify
```

### Run with Docker
```bash
docker build -t core-service .
docker run --rm -p 8080:8080 core-service
```

## API Endpoints
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/users` | Create new user |
| GET | `/api/v1/users` | Get all users (paginated) |
| GET | `/api/v1/users/{id}` | Get user by ID |
| PUT | `/api/v1/users/{id}` | Update user |
| DELETE | `/api/v1/users/{id}` | Delete user |
| GET | `/api/v1/users/search?email=` | Search user by email |

## Example curl commands
### Create a user
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"Ashish",
    "lastName":"Test",
    "email":"ashish@example.com",
    "phoneNumber":"1234567890",
    "dateOfBirth":"1990-01-01",
    "address":"Test Address"
  }'
```

### Get all users
```bash
curl "http://localhost:8080/api/v1/users?page=0&size=10"
```

### Get a user by ID
```bash
curl http://localhost:8080/api/v1/users/1
```

### Update a user
```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"Ashish",
    "lastName":"Updated",
    "email":"ashish.updated@example.com",
    "phoneNumber":"9999999999",
    "dateOfBirth":"1990-01-01",
    "address":"Updated Address"
  }'
```

### Delete a user
```bash
curl -X DELETE http://localhost:8080/api/v1/users/1
```

### Search by email
```bash
curl "http://localhost:8080/api/v1/users/search?email=ashish.updated@example.com"
```

## Validation and error handling
Validation failures return structured `400 Bad Request` responses with field-level details. Missing users return `404 Not Found`, and duplicate emails return `409 Conflict`.
