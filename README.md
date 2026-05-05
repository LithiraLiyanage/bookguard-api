# BookGuard API - Booking Conflict Checker 🛡️

> A Spring Boot REST API that prevents overlapping bookings using date-time conflict detection logic.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=flat-square&logo=springboot)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Conflict Logic](#conflict-logic)
- [Validations](#validations)
- [Error Responses](#error-responses)
- [Postman Testing Guide](#postman-testing-guide)
- [Database](#database)

---

## Overview

**BookGuard API** is a RESTful backend service that manages resource bookings (rooms, labs, conference halls) and automatically prevents overlapping time slots. Built with Spring Boot as part of the Smart Campus project ecosystem.

**Key Features:**
- ✅ Create bookings with full validation
- ✅ Conflict detection before saving
- ✅ Dry-run conflict check without creating a booking
- ✅ Filter by resource, user, or upcoming time
- ✅ Standardized JSON error responses
- ✅ H2 in-memory DB (dev) + MySQL support (prod)

---

## Tech Stack

| Layer        | Technology              |
|--------------|-------------------------|
| Language     | Java 17                 |
| Framework    | Spring Boot 3.2.5       |
| Database     | H2 (dev) / MySQL (prod) |
| ORM          | Spring Data JPA         |
| Validation   | Jakarta Bean Validation |
| Build Tool   | Maven                   |
| Testing      | JUnit 5 + Mockito       |
| API Testing  | Postman                 |

---

## Project Structure

```
src/main/java/com/bookguard/
├── BookGuardApplication.java       ← Entry point
├── controller/
│   └── BookingController.java      ← REST endpoints
├── dto/
│   ├── ApiResponse.java            ← Standardized response wrapper
│   ├── BookingRequest.java         ← Input DTO (with validation)
│   ├── BookingResponse.java        ← Output DTO
│   └── ConflictCheckResponse.java  ← Conflict check result
├── model/
│   └── Booking.java                ← JPA entity
├── repository/
│   └── BookingRepository.java      ← DB queries + conflict JPQL
├── service/
│   ├── BookingService.java         ← Interface
│   └── BookingServiceImpl.java     ← Business logic + validations
├── exception/
│   ├── BookingConflictException.java
│   ├── BookingNotFoundException.java
│   ├── InvalidBookingTimeException.java
│   └── GlobalExceptionHandler.java ← Catches all errors
└── config/
    ├── CorsConfig.java             ← CORS settings
    └── DataInitializer.java        ← Sample data on startup
```

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- (Optional) MySQL for production

### Run the Application

```bash
# Clone the project
git clone https://github.com/yourusername/bookguard-api.git
cd bookguard-api

# Run with Maven
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/bookguard-api-1.0.0.jar
```

The server starts at: **http://localhost:8080**

### H2 Console (Dev Only)
Access the in-memory database UI at:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:bookguarddb
Username: bookguard
Password: bookguard123
```

---

## API Endpoints

### Base URL: `http://localhost:8080/api/bookings`

| Method | Endpoint                        | Description                     |
|--------|---------------------------------|---------------------------------|
| POST   | `/api/bookings`                 | Create a new booking            |
| GET    | `/api/bookings`                 | Get all bookings                |
| GET    | `/api/bookings/{id}`            | Get booking by ID               |
| GET    | `/api/bookings/resource/{id}`   | Get bookings by resource        |
| GET    | `/api/bookings/user/{name}`     | Get bookings by user            |
| GET    | `/api/bookings/upcoming`        | Get all upcoming bookings       |
| GET    | `/api/bookings/check-conflict`  | Check conflict (no booking)     |
| DELETE | `/api/bookings/{id}`            | Delete a booking                |
| GET    | `/api/bookings/health`          | API health check                |

---

## Conflict Logic

A booking conflict is detected when:

```
newStart < existingEnd  AND  newEnd > existingStart
```

### Example — Conflict ❌
```
Existing:  10:00 AM ──────────── 12:00 PM
New:               11:00 AM ──────────── 01:00 PM
                   ^-- OVERLAP --^
Result: CONFLICT ❌ (HTTP 409)
```

### Example — No Conflict ✅
```
Existing:  10:00 AM ──────────── 12:00 PM
New:                             12:00 PM ──── 01:00 PM
Result: ALLOWED ✅ (HTTP 200)
```

---

## Validations

| Field        | Rule                                               |
|--------------|----------------------------------------------------|
| `resourceId` | Required, format: `ROOM-101`, `LAB-205`, etc.      |
| `userName`   | Required, 2–50 letters/spaces only                 |
| `title`      | Required                                           |
| `startTime`  | Required, cannot be in the past                    |
| `endTime`    | Required, must be after startTime                  |
| Duration     | Minimum 15 minutes, maximum 8 hours                |
| Conflict     | No overlapping bookings for the same resource      |

---

## Error Responses

All errors follow this standardized format:

```json
{
  "success": false,
  "message": "Booking Conflict Detected ❌",
  "error": "'ROOM-101' is already booked during the requested time.",
  "timestamp": "2026-05-06T10:30:00"
}
```

| HTTP Status | Scenario                          |
|-------------|-----------------------------------|
| 201         | Booking created successfully      |
| 200         | Successful GET / DELETE           |
| 400         | Validation error / Invalid time   |
| 404         | Booking ID not found              |
| 409         | Time slot conflict detected       |
| 500         | Internal server error             |

---

## Postman Testing Guide

### 1. Create a Booking (POST)
```
POST http://localhost:8080/api/bookings
Content-Type: application/json

{
  "resourceId": "ROOM-101",
  "userName": "Lithira",
  "title": "Java Project Meeting",
  "notes": "Smart Campus backend discussion",
  "startTime": "2026-05-10T10:00:00",
  "endTime": "2026-05-10T12:00:00"
}
```

### 2. Create Conflicting Booking (should return 409)
```
POST http://localhost:8080/api/bookings

{
  "resourceId": "ROOM-101",
  "userName": "Kavindu",
  "title": "Another Meeting",
  "startTime": "2026-05-10T11:00:00",
  "endTime": "2026-05-10T13:00:00"
}
```

### 3. Get All Bookings (GET)
```
GET http://localhost:8080/api/bookings
```

### 4. Check Conflict Without Booking (GET)
```
GET http://localhost:8080/api/bookings/check-conflict
    ?resourceId=ROOM-101
    &startTime=2026-05-10T11:00:00
    &endTime=2026-05-10T13:00:00
```

### 5. Delete Booking (DELETE)
```
DELETE http://localhost:8080/api/bookings/1
```

---

## Database

### Development (H2 - Default)
No setup needed. H2 in-memory database starts automatically.

### Production (MySQL)
1. Create database:
```sql
CREATE DATABASE bookguard;
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookguard
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

3. Uncomment the MySQL dependency in `pom.xml`.

---

## Author

**Lithira** — Smart Campus Project  
Built with ❤️ using Spring Boot
