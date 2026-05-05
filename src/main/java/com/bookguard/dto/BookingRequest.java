package com.bookguard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class BookingRequest {

    @NotBlank(message = "Resource ID cannot be blank")
    @Pattern(regexp = "^[A-Z]+-\\d+$", message = "Resource ID format must be like ROOM-101 or LAB-205")
    private String resourceId;

    @NotBlank(message = "User name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "User name must be 2-50 characters, letters and spaces only")
    private String userName;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @NotBlank(message = "Booking title is required")
    private String title;

    private String notes;

    public BookingRequest() {}

    // Getters
    public String getResourceId() { return resourceId; }
    public String getUserName() { return userName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }

    // Setters
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setTitle(String title) { this.title = title; }
    public void setNotes(String notes) { this.notes = notes; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String resourceId, userName, title, notes;
        private LocalDateTime startTime, endTime;

        public Builder resourceId(String v) { this.resourceId = v; return this; }
        public Builder userName(String v) { this.userName = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { this.endTime = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder notes(String v) { this.notes = v; return this; }

        public BookingRequest build() {
            BookingRequest r = new BookingRequest();
            r.resourceId = resourceId; r.userName = userName;
            r.startTime = startTime; r.endTime = endTime;
            r.title = title; r.notes = notes;
            return r;
        }
    }
}
