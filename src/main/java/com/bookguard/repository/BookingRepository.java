package com.bookguard.repository;

import com.bookguard.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BookingRepository
 * Handles all database operations for bookings.
 * Key method: findConflictingBookings() implements the core conflict detection logic.
 *
 * Conflict Logic:
 *   newStart < existingEnd AND newEnd > existingStart
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Find all bookings that conflict with the given time range for a resource.
     * A conflict exists when: newStart < existingEnd AND newEnd > existingStart
     *
     * @param resourceId  the resource to check (e.g., "ROOM-101")
     * @param startTime   proposed start time
     * @param endTime     proposed end time
     * @return list of conflicting bookings
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.resourceId = :resourceId
            AND b.startTime < :endTime
            AND b.endTime > :startTime
            """)
    List<Booking> findConflictingBookings(
            @Param("resourceId") String resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Find conflicts excluding a specific booking ID (used for update scenarios)
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.resourceId = :resourceId
            AND b.startTime < :endTime
            AND b.endTime > :startTime
            AND b.id != :excludeId
            """)
    List<Booking> findConflictingBookingsExcluding(
            @Param("resourceId") String resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId
    );

    /**
     * Find all bookings for a specific resource
     */
    List<Booking> findByResourceIdOrderByStartTimeAsc(String resourceId);

    /**
     * Find all bookings for a specific user
     */
    List<Booking> findByUserNameIgnoreCaseOrderByStartTimeAsc(String userName);

    /**
     * Find all upcoming bookings (start time after now)
     */
    @Query("SELECT b FROM Booking b WHERE b.startTime >= :now ORDER BY b.startTime ASC")
    List<Booking> findUpcomingBookings(@Param("now") LocalDateTime now);
}
