package com.bookguard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when booking time validations fail.
 * Examples:
 * - End time before start time
 * - Booking in the past
 * - Duration too short or too long
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBookingTimeException extends RuntimeException {

    public InvalidBookingTimeException(String message) {
        super(message);
    }
}
