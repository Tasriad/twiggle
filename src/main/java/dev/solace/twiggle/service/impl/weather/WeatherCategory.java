package dev.solace.twiggle.service.impl.weather;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing categories of weather conditions.
 * Used for classifying different weather patterns.
 */
@Getter
@RequiredArgsConstructor
public enum WeatherCategory {
    CLEAR("Clear", "Clear skies with minimal cloud cover"),
    PARTLY_CLOUDY("Partly Cloudy", "Some clouds present but generally clear"),
    CLOUDY("Cloudy", "Significant cloud cover with limited sunlight"),
    OVERCAST("Overcast", "Complete cloud cover with no visible sky"),
    RAINY("Rainy", "Precipitation in the form of rain"),
    DRIZZLE("Drizzle", "Light rain with small water droplets"),
    STORMY("Stormy", "Severe weather with thunder and lightning"),
    SNOWY("Snowy", "Precipitation in the form of snow"),
    SLEET("Sleet", "Mixed precipitation of rain and snow or ice"),
    HAIL("Hail", "Precipitation in the form of ice pellets"),
    FOGGY("Foggy", "Reduced visibility due to water vapor near the ground"),
    MISTY("Misty", "Light fog with moderate visibility reduction"),
    HAZY("Hazy", "Reduced visibility due to dust or smoke particles"),
    WINDY("Windy", "Strong air movement without precipitation");

    private final String displayName;
    private final String description;

    /**
     * Find a WeatherCategory based on a description string.
     * Performs a case-insensitive partial match against category names and descriptions.
     *
     * @param description The weather description to match
     * @return The matching WeatherCategory or PARTLY_CLOUDY if no match is found
     */
    public static WeatherCategory fromDescription(String description) {
        if (description == null || description.isBlank()) {
            return PARTLY_CLOUDY; // Default value
        }

        String normalizedDesc = description.toLowerCase();

        // Check for exact matches in display names and descriptions
        for (WeatherCategory category : values()) {
            if (category.getDisplayName().toLowerCase().equals(normalizedDesc)) {
                return category;
            }
        }

        // Check for specific keywords in a particular order of precedence
        // Order matters here - more specific patterns should be checked first

        // Storm-related patterns
        if (normalizedDesc.contains("thunder")
                || normalizedDesc.contains("lightning")
                || normalizedDesc.contains("storm") && !normalizedDesc.contains("sand")
                || normalizedDesc.equals("stormy")) {
            return STORMY;
        }

        // Precipitation patterns
        if (normalizedDesc.contains("sleet") || normalizedDesc.contains("ice pellet")) {
            return SLEET;
        }
        if (normalizedDesc.contains("hail") || normalizedDesc.contains("ice ball")) {
            return HAIL;
        }
        if (normalizedDesc.contains("drizzle")
                || (normalizedDesc.contains("light") && normalizedDesc.contains("rain"))) {
            return DRIZZLE;
        }
        if (normalizedDesc.contains("rain")
                || normalizedDesc.contains("shower")
                || normalizedDesc.contains("precipitation") && !normalizedDesc.contains("snow")) {
            return RAINY;
        }
        if (normalizedDesc.contains("snow") || normalizedDesc.contains("blizzard")) {
            return SNOWY;
        }

        // Visibility patterns
        if (normalizedDesc.contains("fog")) {
            return FOGGY;
        }
        if (normalizedDesc.contains("mist")) {
            return MISTY;
        }
        if (normalizedDesc.contains("haz")
                || normalizedDesc.contains("dust")
                || normalizedDesc.contains("smoke")
                || normalizedDesc.contains("sand")) {
            return HAZY;
        }

        // Wind patterns
        if (normalizedDesc.contains("wind")
                || normalizedDesc.contains("breez")
                || normalizedDesc.contains("gust")
                || normalizedDesc.equals("windy")) {
            return WINDY;
        }

        // Sky conditions - less specific patterns should be checked last
        if (normalizedDesc.contains("clear") || normalizedDesc.contains("sun") || normalizedDesc.contains("sunny")) {
            return CLEAR;
        }
        if (normalizedDesc.contains("overcast")) {
            return OVERCAST;
        }
        if (normalizedDesc.contains("cloud") && normalizedDesc.contains("partly")
                || normalizedDesc.contains("scattered")) {
            return PARTLY_CLOUDY;
        }
        if (normalizedDesc.contains("cloud") || normalizedDesc.equals("cloudy")) {
            return CLOUDY;
        }

        // Default fallback
        return PARTLY_CLOUDY;
    }
}
