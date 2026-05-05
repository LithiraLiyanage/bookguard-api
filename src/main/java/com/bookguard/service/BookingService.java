package com.bookguard.service;

import com.bookguard.dto.BookingRequest;
import com.bookguard.dto.BookingResponse;
import com.bookguard.dto.ConflictCheckResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BookingService Interface
 * Defines all business operations for the booking system.
 */
public interface BookingService {

    /**
     * Create a new booking (validates no conflicts exist)
     */
    BookingResponse createBooking(BookingRequest request);

    /**
     * Get all bookings (sorted by start time)
     */
    List<BookingResponse> getAllBookings();

    /**
     * Get booking by ID
     */
    BookingResponse getBookingById(Long id);

    /**
     * Get all bookings for a specific resource
     */
    List<BookingResponse> getBookingsByResource(String resourceId);

    /**
     * Get all bookings for a specific user
     */
    List<BookingResponse> getBookingsByUser(String userName);

    /**
     * Get all upcoming bookings
     */
    List<BookingResponse> getUpcomingBookings();

    /**
     * Check if a time slot conflicts with existing bookings (without creating)
     */
    ConflictCheckResponse checkConflict(String resourceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Delete a booking by ID
     */
    void deleteBooking(Long id);
}
