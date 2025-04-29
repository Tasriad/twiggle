package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing different types of weather hazards.
 * Each type corresponds to a specific weather condition that could pose
 * a risk to users.
 */
public enum HazardType {
    EXTREME_HEAT("Extreme Heat", "High temperatures that may cause heat-related illnesses"),
    EXTREME_COLD("Extreme Cold", "Low temperatures that may cause cold-related illnesses"),
    HIGH_WIND("High Wind", "Strong winds that may cause damage or pose risks"),
    FLOODING("Flooding", "Heavy rain that may cause water accumulation and flooding"),
    SNOW_ICE("Snow/Ice", "Snow or ice accumulation that may affect travel and safety"),
    LOW_VISIBILITY("Low Visibility", "Reduced visibility due to fog, mist, or precipitation"),
    THUNDERSTORM("Thunderstorm", "Electrical storms with risks of lightning"),
    TORNADO("Tornado", "Violent rotating columns of air"),
    HURRICANE("Hurricane", "Large tropical storms with high winds");

    private final String displayName;
    private final String description;

    HazardType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the human-readable display name for this hazard type.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description of this hazard type.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
