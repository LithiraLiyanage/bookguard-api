package com.bookguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BookGuard API - Booking Conflict Checker
 * A Spring Boot REST API that prevents overlapping bookings
 * using date-time conflict detection logic.
 *
 * @author Lithira
 * @version 1.0.0
 */
@SpringBootApplication
public class BookGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookGuardApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════════╗
                ║       BookGuard API is Running! 🛡️        ║
                ║   http://localhost:8080/api/bookings      ║
                ║   H2 Console: http://localhost:8080/      ║
                ║              h2-console                   ║
                ╚══════════════════════════════════════════╝
                """);
    }
}
