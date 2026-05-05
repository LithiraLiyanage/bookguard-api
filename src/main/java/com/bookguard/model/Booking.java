package com.bookguard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String resourceId;

    @NotBlank
    @Column(nullable = false)
    private String userName;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endTime;

    private String title;
    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Booking() {}

    public Booking(Long id, String resourceId, String userName, LocalDateTime startTime,
                   LocalDateTime endTime, String title, String notes) {
        this.id = id;
        this.resourceId = resourceId;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.notes = notes;
    }

    // Getters
    public Long getId() { return id; }
    public String getResourceId() { return resourceId; }
    public String getUserName() { return userName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setTitle(String title) { this.title = title; }
    public void setNotes(String notes) { this.notes = notes; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String resourceId;
        private String userName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String title;
        private String notes;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }
        public Builder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public Builder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public Booking build() {
            return new Booking(id, resourceId, userName, startTime, endTime, title, notes);
        }
    }
}
