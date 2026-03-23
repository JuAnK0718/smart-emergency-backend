package com.sigeu.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Incoming request body for POST /emergency.
 * Represents the data sent by the citizen from the frontend.
 */
public class EmergencyRequest {

    /**
     * Type of emergency.
     * Accepted values: "accident", "fire", "robbery", "medical", "other"
     */
    @NotBlank(message = "El tipo de emergencia es obligatorio")
    private String type;

    /**
     * Optional list of answers provided by the citizen
     * to help determine priority level.
     * Example: ["hay heridos", "el fuego se está expandiendo"]
     */
    private List<String> answers;

    /**
     * Location object with latitude and longitude.
     */
    @NotNull(message = "La ubicación es obligatoria")
    private Location location;

    /**
     * Optional free-text description of the emergency.
     */
    private String description;

    // ── Getters and setters ──

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<String> getAnswers() { return answers; }
    public void setAnswers(List<String> answers) { this.answers = answers; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Nested location object.
     */
    public static class Location {

        private double lat;
        private double lng;

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }
}
