package com.bookguard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private String resourceId;
    private String userName;
    private String title;
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private long durationMinutes;

    public BookingResponse() {}

    // Getters
    public Long getId() { return id; }
    public String getResourceId() { return resourceId; }
    public String getUserName() { return userName; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getDurationMinutes() { return durationMinutes; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setTitle(String title) { this.title = title; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setDurationMinutes(long durationMinutes) { this.durationMinutes = durationMinutes; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String resourceId, userName, title, notes;
        private LocalDateTime startTime, endTime, createdAt;
        private long durationMinutes;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder resourceId(String v) { this.resourceId = v; return this; }
        public Builder userName(String v) { this.userName = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder notes(String v) { this.notes = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { this.endTime = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder durationMinutes(long v) { this.durationMinutes = v; return this; }

        public BookingResponse build() {
            BookingResponse r = new BookingResponse();
            r.id = id; r.resourceId = resourceId; r.userName = userName;
            r.title = title; r.notes = notes; r.startTime = startTime;
            r.endTime = endTime; r.createdAt = createdAt; r.durationMinutes = durationMinutes;
            return r;
        }
    }
}
