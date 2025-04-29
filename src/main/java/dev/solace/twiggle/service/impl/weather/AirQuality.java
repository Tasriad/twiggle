package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing air quality levels based on EPA standards.
 */
public enum AirQuality {
    GOOD("Good", 1),
    MODERATE("Moderate", 2),
    UNHEALTHY_FOR_SENSITIVE_GROUPS("Unhealthy for Sensitive Groups", 3),
    UNHEALTHY("Unhealthy", 4),
    VERY_UNHEALTHY("Very Unhealthy", 5),
    HAZARDOUS("Hazardous", 6);

    private final String displayName;
    private final int epaIndex;

    AirQuality(String displayName, int epaIndex) {
        this.displayName = displayName;
        this.epaIndex = epaIndex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getEpaIndex() {
        return epaIndex;
    }

    /**
     * Get AirQuality enum from EPA index
     */
    public static AirQuality fromEpaIndex(int epaIndex) {
        for (AirQuality airQuality : values()) {
            if (airQuality.epaIndex == epaIndex) {
                return airQuality;
            }
        }
        return MODERATE; // Default
    }

    /**
     * Get AirQuality enum from display name
     */
    public static AirQuality fromDisplayName(String name) {
        for (AirQuality airQuality : values()) {
            if (airQuality.displayName.equals(name)) {
                return airQuality;
            }
        }
        return MODERATE; // Default
    }
}
