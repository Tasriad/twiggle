package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing various hazard levels for weather conditions.
 * Used to indicate the severity of a weather condition.
 */
public enum HazardLevel {
    NONE("None", "No significant hazard", 0),
    LOW("Low", "Low level hazard", 1),
    MODERATE("Moderate", "Moderate level hazard", 2),
    HIGH("High", "High level hazard", 3),
    VERY_HIGH("Very High", "Very high level hazard", 4),
    SEVERE("Severe", "Severe and dangerous hazard", 5),
    EXTREME("Extreme", "Extreme hazard level", 6);

    private final String displayName;
    private final String description;
    private final int level;

    HazardLevel(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    /**
     * Gets the human-readable display name for this hazard level.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description of this hazard level.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the numeric value of this hazard level.
     *
     * @return The numeric level (higher is more severe)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Checks if this hazard level is more severe than another.
     *
     * @param other The hazard level to compare with
     * @return true if this level is more severe than the other
     */
    public boolean isMoreSevereThan(HazardLevel other) {
        return this.level > other.level;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
