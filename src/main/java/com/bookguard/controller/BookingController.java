package com.bookguard.controller;

import com.bookguard.dto.ApiResponse;
import com.bookguard.dto.BookingRequest;
import com.bookguard.dto.BookingResponse;
import com.bookguard.dto.ConflictCheckResponse;
import com.bookguard.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request) {
        log.info("POST /api/bookings - Creating booking for {}", request.getResourceId());
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully! ID: " + response.getId(), response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success("Found " + bookings.size() + " booking(s)", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success("Booking found", response));
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByResource(@PathVariable String resourceId) {
        List<BookingResponse> bookings = bookingService.getBookingsByResource(resourceId);
        return ResponseEntity.ok(ApiResponse.success("Found " + bookings.size() + " booking(s) for " + resourceId, bookings));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByUser(@PathVariable String userName) {
        List<BookingResponse> bookings = bookingService.getBookingsByUser(userName);
        return ResponseEntity.ok(ApiResponse.success("Found " + bookings.size() + " booking(s) for " + userName, bookings));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUpcomingBookings() {
        List<BookingResponse> bookings = bookingService.getUpcomingBookings();
        return ResponseEntity.ok(ApiResponse.success("Found " + bookings.size() + " upcoming booking(s)", bookings));
    }

    @GetMapping("/check-conflict")
    public ResponseEntity<ApiResponse<ConflictCheckResponse>> checkConflict(
            @RequestParam String resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        log.info("GET /api/bookings/check-conflict - {} from {} to {}", resourceId, startTime, endTime);
        ConflictCheckResponse result = bookingService.checkConflict(resourceId, startTime, endTime);
        HttpStatus status = result.isHasConflict() ? HttpStatus.CONFLICT : HttpStatus.OK;
        return ResponseEntity.status(status).body(ApiResponse.success(result.getMessage(), result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking with ID " + id + " deleted successfully", null));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("BookGuard API is running!", "Status: UP | Version: 1.0.0"));
    }
}
