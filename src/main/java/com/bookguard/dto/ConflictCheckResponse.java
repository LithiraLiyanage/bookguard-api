package com.bookguard.dto;

import java.util.List;

public class ConflictCheckResponse {

    private boolean hasConflict;
    private String message;
    private List<BookingResponse> conflictingBookings;

    public ConflictCheckResponse() {}

    public ConflictCheckResponse(boolean hasConflict, String message, List<BookingResponse> conflictingBookings) {
        this.hasConflict = hasConflict;
        this.message = message;
        this.conflictingBookings = conflictingBookings;
    }

    // Getters
    public boolean isHasConflict() { return hasConflict; }
    public String getMessage() { return message; }
    public List<BookingResponse> getConflictingBookings() { return conflictingBookings; }

    // Setters
    public void setHasConflict(boolean hasConflict) { this.hasConflict = hasConflict; }
    public void setMessage(String message) { this.message = message; }
    public void setConflictingBookings(List<BookingResponse> conflictingBookings) { this.conflictingBookings = conflictingBookings; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private boolean hasConflict;
        private String message;
        private List<BookingResponse> conflictingBookings;

        public Builder hasConflict(boolean v) { this.hasConflict = v; return this; }
        public Builder message(String v) { this.message = v; return this; }
        public Builder conflictingBookings(List<BookingResponse> v) { this.conflictingBookings = v; return this; }

        public ConflictCheckResponse build() {
            return new ConflictCheckResponse(hasConflict, message, conflictingBookings);
        }
    }
}
