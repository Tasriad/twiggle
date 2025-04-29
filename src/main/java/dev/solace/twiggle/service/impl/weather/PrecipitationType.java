package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing different types of precipitation.
 */
public enum PrecipitationType {
    NONE("None", "No precipitation"),
    DRIZZLE("Drizzle", "Very light rain consisting of tiny water droplets"),
    RAIN("Rain", "Water droplets falling from clouds"),
    FREEZING_RAIN("Freezing Rain", "Rain that freezes on contact with surfaces"),
    SLEET("Sleet", "Mixture of rain and snow or ice pellets"),
    SNOW("Snow", "Precipitation in the form of ice crystals"),
    HAIL("Hail", "Solid precipitation in the form of balls or lumps of ice"),
    MIXED("Mixed", "Mixture of different precipitation types");

    private final String displayName;
    private final String description;

    PrecipitationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Parse precipitation type from string description.
     *
     * @param precipText Text description of precipitation
     * @return Matching precipitation type, defaulting to NONE if no match found
     */
    public static PrecipitationType fromString(String precipText) {
        if (precipText == null || precipText.isEmpty()) {
            return NONE;
        }

        String normalized = precipText.toLowerCase().trim();

        if (normalized.contains("drizzle")) {
            return DRIZZLE;
        } else if (normalized.contains("freezing rain")) {
            return FREEZING_RAIN;
        } else if (normalized.contains("rain")) {
            return RAIN;
        } else if (normalized.contains("sleet")) {
            return SLEET;
        } else if (normalized.contains("snow")) {
            return SNOW;
        } else if (normalized.contains("hail")) {
            return HAIL;
        } else if (normalized.contains("mixed") || (normalized.contains("rain") && normalized.contains("snow"))) {
            return MIXED;
        }

        return NONE;
    }

    /**
     * Determine precipitation type based on temperature in Celsius.
     * This is a simplified approach maintained for backward compatibility.
     *
     * @param temperature Temperature in Celsius
     * @return Precipitation type based on temperature
     */
    public static PrecipitationType fromTemperature(double temperature) {
        if (temperature < 0) {
            return SNOW;
        } else if (temperature < 4) {
            return SLEET;
        } else {
            return RAIN;
        }
    }
}
