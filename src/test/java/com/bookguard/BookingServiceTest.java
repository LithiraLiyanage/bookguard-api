package com.bookguard;

import com.bookguard.dto.BookingRequest;
import com.bookguard.dto.BookingResponse;
import com.bookguard.dto.ConflictCheckResponse;
import com.bookguard.exception.BookingConflictException;
import com.bookguard.exception.BookingNotFoundException;
import com.bookguard.exception.InvalidBookingTimeException;
import com.bookguard.model.Booking;
import com.bookguard.repository.BookingRepository;
import com.bookguard.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookGuard - Booking Service Tests")
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private LocalDateTime tomorrow;
    private BookingRequest validRequest;

    @BeforeEach
    void setUp() {
        tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        validRequest = BookingRequest.builder()
                .resourceId("ROOM-101")
                .userName("Lithira")
                .title("Test Meeting")
                .startTime(tomorrow)
                .endTime(tomorrow.plusHours(2))
                .build();
    }

    // ===================== CREATE BOOKING TESTS =====================

    @Test
    @DisplayName("✅ Should create booking when no conflict exists")
    void shouldCreateBookingSuccessfully() {
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(List.of());
        Booking saved = buildBooking(1L, "ROOM-101", "Lithira", tomorrow, tomorrow.plusHours(2));
        when(bookingRepository.save(any())).thenReturn(saved);

        BookingResponse response = bookingService.createBooking(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResourceId()).isEqualTo("ROOM-101");
        assertThat(response.getUserName()).isEqualTo("Lithira");
        assertThat(response.getDurationMinutes()).isEqualTo(120);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("❌ Should throw BookingConflictException when time slot overlaps")
    void shouldThrowConflictExceptionWhenOverlapping() {
        Booking existing = buildBooking(1L, "ROOM-101", "Kavindu", tomorrow, tomorrow.plusHours(2));
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> bookingService.createBooking(validRequest))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("ROOM-101");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("❌ Should throw InvalidBookingTimeException when end before start")
    void shouldThrowWhenEndBeforeStart() {
        validRequest.setEndTime(tomorrow.minusHours(1));

        assertThatThrownBy(() -> bookingService.createBooking(validRequest))
                .isInstanceOf(InvalidBookingTimeException.class)
                .hasMessageContaining("End time must be after start time");
    }

    @Test
    @DisplayName("❌ Should throw InvalidBookingTimeException when start time is in the past")
    void shouldThrowWhenStartTimeInPast() {
        validRequest.setStartTime(LocalDateTime.now().minusHours(1));
        validRequest.setEndTime(LocalDateTime.now().plusHours(1));

        assertThatThrownBy(() -> bookingService.createBooking(validRequest))
                .isInstanceOf(InvalidBookingTimeException.class)
                .hasMessageContaining("past");
    }

    @Test
    @DisplayName("❌ Should throw when booking duration less than 15 minutes")
    void shouldThrowWhenDurationTooShort() {
        validRequest.setEndTime(tomorrow.plusMinutes(10));

        assertThatThrownBy(() -> bookingService.createBooking(validRequest))
                .isInstanceOf(InvalidBookingTimeException.class)
                .hasMessageContaining("15 minutes");
    }

    @Test
    @DisplayName("❌ Should throw when booking duration exceeds 8 hours")
    void shouldThrowWhenDurationTooLong() {
        validRequest.setEndTime(tomorrow.plusHours(9));

        assertThatThrownBy(() -> bookingService.createBooking(validRequest))
                .isInstanceOf(InvalidBookingTimeException.class)
                .hasMessageContaining("8 hours");
    }

    // ===================== CONFLICT CHECK TESTS =====================

    @Test
    @DisplayName("✅ Conflict check should return no conflict for free slot")
    void shouldReturnNoConflictForFreeSlot() {
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(List.of());

        ConflictCheckResponse result = bookingService.checkConflict("ROOM-101", tomorrow, tomorrow.plusHours(2));

        assertThat(result.isHasConflict()).isFalse();
        assertThat(result.getConflictingBookings()).isEmpty();
    }

    @Test
    @DisplayName("❌ Conflict check should detect overlapping booking")
    void shouldDetectConflictForOverlappingSlot() {
        Booking existing = buildBooking(1L, "ROOM-101", "Kavindu", tomorrow, tomorrow.plusHours(2));
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(List.of(existing));

        ConflictCheckResponse result = bookingService.checkConflict("ROOM-101", tomorrow.plusHours(1), tomorrow.plusHours(3));

        assertThat(result.isHasConflict()).isTrue();
        assertThat(result.getConflictingBookings()).hasSize(1);
    }

    // ===================== GET / DELETE TESTS =====================

    @Test
    @DisplayName("✅ Should return all bookings")
    void shouldReturnAllBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(
                buildBooking(1L, "ROOM-101", "Lithira", tomorrow, tomorrow.plusHours(1)),
                buildBooking(2L, "LAB-205", "Sachini", tomorrow.plusHours(2), tomorrow.plusHours(4))
        ));

        List<BookingResponse> result = bookingService.getAllBookings();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("✅ Should return booking by ID")
    void shouldReturnBookingById() {
        Booking booking = buildBooking(1L, "ROOM-101", "Lithira", tomorrow, tomorrow.plusHours(2));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(1L);
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("❌ Should throw BookingNotFoundException for missing ID")
    void shouldThrowWhenBookingNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(999L))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("✅ Should delete booking successfully")
    void shouldDeleteBookingSuccessfully() {
        Booking booking = buildBooking(1L, "ROOM-101", "Lithira", tomorrow, tomorrow.plusHours(2));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository, times(1)).delete(booking);
    }

    // ===================== HELPERS =====================

    private Booking buildBooking(Long id, String resourceId, String userName,
                                  LocalDateTime start, LocalDateTime end) {
        return Booking.builder()
                .id(id)
                .resourceId(resourceId)
                .userName(userName)
                .title("Test")
                .startTime(start)
                .endTime(end)
                .build();
    }
}
