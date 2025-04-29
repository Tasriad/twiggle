package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing different visibility conditions based on distance visible in miles.
 */
public enum VisibilityType {
    DENSE_FOG("Dense Fog", "Very limited visibility", 0, 0.25),
    THICK_FOG("Thick Fog", "Severely restricted visibility", 0.25, 0.5),
    MODERATE_FOG("Moderate Fog", "Considerably reduced visibility", 0.5, 1),
    LIGHT_FOG("Light Fog", "Reduced visibility", 1, 2),
    MIST("Mist", "Slightly reduced visibility", 2, 3),
    HAZE("Haze", "Somewhat reduced visibility due to atmospheric particles", 3, 6),
    GOOD("Good", "Good visibility", 6, 10),
    EXCELLENT("Excellent", "Excellent visibility", 10, 50),
    UNLIMITED("Unlimited", "Visibility stretches to the horizon", 50, Integer.MAX_VALUE),
    UNKNOWN("Unknown", "Visibility data not available", 0, 0);

    private final String displayName;
    private final String description;
    private final double minVisibilityMiles;
    private final double maxVisibilityMiles;

    VisibilityType(String displayName, String description, double minVisibilityMiles, double maxVisibilityMiles) {
        this.displayName = displayName;
        this.description = description;
        this.minVisibilityMiles = minVisibilityMiles;
        this.maxVisibilityMiles = maxVisibilityMiles;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public double getMinVisibilityMiles() {
        return minVisibilityMiles;
    }

    public double getMaxVisibilityMiles() {
        return maxVisibilityMiles;
    }

    /**
     * Determine visibility type based on visibility distance in miles.
     *
     * @param visibilityMiles Visibility distance in miles, can be null if visibility data is not available
     * @return The corresponding visibility type, or UNKNOWN if visibility is null
     */
    public static VisibilityType fromVisibility(Double visibilityMiles) {
        // Handle null visibility (data not available)
        if (visibilityMiles == null) {
            return UNKNOWN;
        }

        for (VisibilityType type : values()) {
            if (visibilityMiles >= type.minVisibilityMiles && visibilityMiles < type.maxVisibilityMiles) {
                return type;
            }
        }

        // Default to UNLIMITED for any extremely high values
        return UNLIMITED;
    }
}
