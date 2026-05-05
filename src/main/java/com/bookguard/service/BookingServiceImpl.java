package com.bookguard.service;

import com.bookguard.dto.BookingRequest;
import com.bookguard.dto.BookingResponse;
import com.bookguard.dto.ConflictCheckResponse;
import com.bookguard.exception.BookingConflictException;
import com.bookguard.exception.BookingNotFoundException;
import com.bookguard.exception.InvalidBookingTimeException;
import com.bookguard.model.Booking;
import com.bookguard.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private static final int MIN_DURATION_MINUTES = 15;
    private static final int MAX_DURATION_HOURS = 8;

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for resource: {} by user: {}", request.getResourceId(), request.getUserName());

        validateBookingTimes(request.getStartTime(), request.getEndTime());

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.getResourceId(), request.getStartTime(), request.getEndTime());

        if (!conflicts.isEmpty()) {
            String conflictDetails = conflicts.stream()
                    .map(b -> "[" + b.getStartTime() + " to " + b.getEndTime() + " by " + b.getUserName() + "]")
                    .collect(Collectors.joining(", "));
            log.warn("Conflict detected for {}: {}", request.getResourceId(), conflictDetails);
            throw new BookingConflictException(
                    "'" + request.getResourceId() + "' is already booked. Conflicts: " + conflictDetails);
        }

        Booking booking = Booking.builder()
                .resourceId(request.getResourceId())
                .userName(request.getUserName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .title(request.getTitle())
                .notes(request.getNotes())
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + id));
        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByResource(String resourceId) {
        return bookingRepository.findByResourceIdOrderByStartTimeAsc(resourceId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(String userName) {
        return bookingRepository.findByUserNameIgnoreCaseOrderByStartTimeAsc(userName)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUpcomingBookings() {
        return bookingRepository.findUpcomingBookings(LocalDateTime.now())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConflictCheckResponse checkConflict(String resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        validateBookingTimes(startTime, endTime);
        List<Booking> conflicts = bookingRepository.findConflictingBookings(resourceId, startTime, endTime);

        if (conflicts.isEmpty()) {
            return new ConflictCheckResponse(false,
                    "'" + resourceId + "' is available from " + startTime + " to " + endTime + ". You can book it!",
                    List.of());
        }

        List<BookingResponse> conflictResponses = conflicts.stream().map(this::mapToResponse).collect(Collectors.toList());
        return new ConflictCheckResponse(true,
                "'" + resourceId + "' has " + conflicts.size() + " conflicting booking(s).",
                conflictResponses);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Cannot delete: Booking not found with ID: " + id));
        bookingRepository.delete(booking);
        log.info("Booking with ID {} deleted successfully", id);
    }

    private void validateBookingTimes(LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new InvalidBookingTimeException(
                    "End time must be after start time. start=" + startTime + ", end=" + endTime);
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidBookingTimeException("Start time cannot be in the past. Provided: " + startTime);
        }
        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < MIN_DURATION_MINUTES) {
            throw new InvalidBookingTimeException(
                    "Booking must be at least " + MIN_DURATION_MINUTES + " minutes. Your duration: " + durationMinutes + " minutes");
        }
        long durationHours = Duration.between(startTime, endTime).toHours();
        if (durationHours > MAX_DURATION_HOURS) {
            throw new InvalidBookingTimeException(
                    "Booking cannot exceed " + MAX_DURATION_HOURS + " hours. Your duration: " + durationHours + " hours");
        }
    }

    private BookingResponse mapToResponse(Booking booking) {
        long durationMinutes = Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes();
        return BookingResponse.builder()
                .id(booking.getId())
                .resourceId(booking.getResourceId())
                .userName(booking.getUserName())
                .title(booking.getTitle())
                .notes(booking.getNotes())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .createdAt(booking.getCreatedAt())
                .durationMinutes(durationMinutes)
                .build();
    }
}
