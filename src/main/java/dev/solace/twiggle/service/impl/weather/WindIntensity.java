package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing wind intensity based on the Beaufort scale.
 */
public enum WindIntensity {
    CALM("Calm", "Smoke rises vertically", 0, 1),
    LIGHT_AIR("Light Air", "Direction shown by smoke drift but not by wind vanes", 1, 4),
    LIGHT_BREEZE("Light Breeze", "Wind felt on face; leaves rustle; wind vanes move", 4, 8),
    GENTLE_BREEZE("Gentle Breeze", "Leaves and small twigs in constant motion; light flags extended", 8, 13),
    MODERATE_BREEZE("Moderate Breeze", "Raises dust and loose paper; small branches move", 13, 19),
    FRESH_BREEZE("Fresh Breeze", "Small trees in leaf begin to sway; crested wavelets form on inland waters", 19, 25),
    STRONG_BREEZE("Strong Breeze", "Large branches in motion; whistling heard in telegraph wires", 25, 32),
    NEAR_GALE("Near Gale", "Whole trees in motion; resistance felt when walking against wind", 32, 39),
    GALE("Gale", "Twigs break off trees; generally impedes progress", 39, 47),
    STRONG_GALE("Strong Gale", "Slight structural damage occurs; slate blown from roofs", 47, 55),
    STORM("Storm", "Trees uprooted; considerable structural damage", 55, 64),
    VIOLENT_STORM("Violent Storm", "Widespread damage", 64, 73),
    HURRICANE("Hurricane", "Severe and extensive damage", 73, Integer.MAX_VALUE);

    private final String displayName;
    private final String description;
    private final int minSpeedMph;
    private final int maxSpeedMph;

    WindIntensity(String displayName, String description, int minSpeedMph, int maxSpeedMph) {
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
     * Determine wind intensity based on wind speed in mph.
     *
     * @param windSpeedMph Wind speed in miles per hour
     * @return The corresponding wind intensity
     */
    public static WindIntensity fromWindSpeed(double windSpeedMph) {
        for (WindIntensity intensity : values()) {
            if (windSpeedMph >= intensity.minSpeedMph && windSpeedMph < intensity.maxSpeedMph) {
                return intensity;
            }
        }

        // Default to HURRICANE for extremely high values
        return HURRICANE;
    }
}
