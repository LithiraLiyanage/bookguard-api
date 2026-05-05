package com.bookguard.config;

import com.bookguard.model.Booking;
import com.bookguard.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner seedData(BookingRepository bookingRepository) {
        return args -> {
            log.info("Seeding sample bookings for testing...");

            LocalDateTime base = LocalDateTime.now().plusDays(1)
                    .withHour(9).withMinute(0).withSecond(0).withNano(0);

            bookingRepository.save(Booking.builder()
                    .resourceId("ROOM-101").userName("Lithira")
                    .title("Java Project Meeting").notes("Smart Campus backend discussion")
                    .startTime(base).endTime(base.plusHours(2)).build());

            bookingRepository.save(Booking.builder()
                    .resourceId("ROOM-101").userName("Kavindu")
                    .title("Database Design Session").notes("MySQL schema review")
                    .startTime(base.plusHours(3)).endTime(base.plusHours(5)).build());

            bookingRepository.save(Booking.builder()
                    .resourceId("LAB-205").userName("Sachini")
                    .title("Spring Boot Workshop").notes("REST API hands-on lab")
                    .startTime(base.plusHours(1)).endTime(base.plusHours(3)).build());

            bookingRepository.save(Booking.builder()
                    .resourceId("CONF-A").userName("Dilshan")
                    .title("Tech Talk: Microservices")
                    .startTime(base.plusDays(1)).endTime(base.plusDays(1).plusHours(2)).build());

            log.info("Sample bookings seeded! Total: {}", bookingRepository.count());
        };
    }
}
