# Placement Management System (Backend)

A Spring Boot REST API for managing campus placements: students, job postings,
applications and automatic eligibility checking.

## Tech stack

- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation, Security)
- MySQL
- Maven (wrapper included)
- JUnit 5 + H2 (tests only)

## Features

- Manage students and job postings (CRUD with validation)
- Automatic eligibility check: CGPA, branch, required skills and backlogs
- Apply for jobs with duplicate and eligibility protection
- Application status workflow: `APPLIED`, `SHORTLISTED`, `REJECTED`, `SELECTED`
- Search and filter jobs by keyword, CGPA and branch
- Role-based access (ADMIN and STUDENT) using HTTP Basic authentication
- Consistent JSON error responses

## Getting started

### 1. Prerequisites

- JDK 21
- MySQL running locally

### 2. Create the database

```sql
CREATE DATABASE placement_db;
```

### 3. Configure the application

Copy `src/main/resources/application.properties.example` to
`src/main/resources/application.properties` and fill in your own values
(MySQL username/password and the two login passwords).
This file is gitignored, so your passwords are never committed.

### 4. Run

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

The API starts on http://localhost:8080.

### 5. Run the tests

```bash
./mvnw test                   # Linux / macOS
mvnw.cmd test                 # Windows
```

Tests use an in-memory H2 database, so MySQL is not needed to run them.

## Authentication

The API uses HTTP Basic authentication with two accounts:

| Username  | Role    | Password                                  |
|-----------|---------|-------------------------------------------|
| `admin`   | ADMIN   | `app.security.admin-password` you set     |
| `student` | STUDENT | `app.security.student-password` you set   |

## API endpoints

### Public

| Method | Endpoint     | Description  |
|--------|--------------|--------------|
| GET    | `/api/hello` | Health check |

### Jobs

| Method | Endpoint                                              | Access        |
|--------|-------------------------------------------------------|---------------|
| GET    | `/api/jobs`                                           | ADMIN, STUDENT |
| GET    | `/api/jobs/{id}`                                      | ADMIN, STUDENT |
| GET    | `/api/jobs/search?keyword=`                           | ADMIN, STUDENT |
| GET    | `/api/jobs/filter?cgpa=&branch=`                      | ADMIN, STUDENT |
| GET    | `/api/jobs/search/filter?keyword=&cgpa=&branch=`      | ADMIN, STUDENT |
| POST   | `/api/jobs`                                           | ADMIN         |
| PUT    | `/api/jobs/{id}`                                      | ADMIN         |
| DELETE | `/api/jobs/{id}`                                      | ADMIN         |

### Students (ADMIN only)

| Method | Endpoint             |
|--------|----------------------|
| POST   | `/api/students`      |
| GET    | `/api/students`      |
| GET    | `/api/students/{id}` |
| PUT    | `/api/students/{id}` |
| DELETE | `/api/students/{id}` |

### Applications

| Method | Endpoint                                  | Access         |
|--------|-------------------------------------------|----------------|
| POST   | `/api/applications?studentId=&jobId=`     | ADMIN, STUDENT |
| GET    | `/api/applications/{id}`                  | ADMIN, STUDENT |
| GET    | `/api/applications/student/{studentId}`   | ADMIN, STUDENT |
| GET    | `/api/applications/job/{jobId}`           | ADMIN          |
| PUT    | `/api/applications/{id}/status?status=`   | ADMIN          |
| DELETE | `/api/applications/{id}`                  | ADMIN          |

Valid status values (uppercase): `APPLIED`, `SHORTLISTED`, `REJECTED`, `SELECTED`.
A `REJECTED` or `SELECTED` application can no longer be changed.

### Eligibility

| Method | Endpoint                                             | Access         |
|--------|------------------------------------------------------|----------------|
| GET    | `/api/eligibility/student/{studentId}/job/{jobId}`   | ADMIN, STUDENT |

A student is eligible when **all** of these hold:

- CGPA is at least the job's minimum CGPA
- Branch is in the job's eligible branches (case-insensitive)
- The student has every required skill (case-insensitive)
- Backlogs are at most the job's maximum

## Error format

All errors use the same JSON shape:

```json
{
  "status": 404,
  "message": "Student not found",
  "errors": {},
  "timestamp": "2026-10-12T08:50:00Z"
}
```

Validation errors list each problem under `errors`, keyed by field name.

## Known limitations

- The `student` login is not linked to an individual student record, so a
  student account can view any application by ID.
- HTTP Basic authentication should only be used over HTTPS in production.