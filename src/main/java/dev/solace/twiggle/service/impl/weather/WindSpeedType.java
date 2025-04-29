package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing different wind speed categories based on the Beaufort scale.
 */
public enum WindSpeedType {
    CALM("Calm", "Smoke rises vertically", 0, 1),
    LIGHT_AIR("Light Air", "Wind motion visible in smoke", 1, 3),
    LIGHT_BREEZE("Light Breeze", "Wind felt on exposed skin, leaves rustle", 4, 7),
    GENTLE_BREEZE("Gentle Breeze", "Leaves and small twigs in constant motion", 8, 12),
    MODERATE_BREEZE("Moderate Breeze", "Dust and loose paper raised, small branches move", 13, 18),
    FRESH_BREEZE("Fresh Breeze", "Small trees sway", 19, 24),
    STRONG_BREEZE("Strong Breeze", "Large branches in motion, umbrella use difficult", 25, 31),
    HIGH_WIND("High Wind", "Whole trees in motion, effort to walk against the wind", 32, 38),
    GALE("Gale", "Twigs break off trees, difficult to walk", 39, 46),
    STRONG_GALE("Strong Gale", "Slight structural damage occurs", 47, 54),
    STORM("Storm", "Trees uprooted, considerable structural damage", 55, 63),
    VIOLENT_STORM("Violent Storm", "Widespread damage", 64, 72),
    HURRICANE("Hurricane", "Severe widespread damage to structures", 73, Integer.MAX_VALUE);

    private final String displayName;
    private final String description;
    private final int minSpeedMph;
    private final int maxSpeedMph;

    WindSpeedType(String displayName, String description, int minSpeedMph, int maxSpeedMph) {
        this.displayName = displayName;
        this.description = description;
        this.minSpeedMph = minSpeedMph;
        this.maxSpeedMph = maxSpeedMph;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getMinSpeedMph() {
        return minSpeedMph;
    }

    public int getMaxSpeedMph() {
        return maxSpeedMph;
    }

    /**
     * Determine wind speed type based on wind speed in mph
     */
    public static WindSpeedType fromWindSpeed(int windSpeedMph) {
        for (WindSpeedType type : WindSpeedType.values()) {
            if (windSpeedMph >= type.minSpeedMph && windSpeedMph <= type.maxSpeedMph) {
                return type;
            }
        }
        return HURRICANE; // Default for extremely high wind speeds
    }
}
