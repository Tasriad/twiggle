package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing categories of weather hazards.
 * Used to group hazards by their general nature or source.
 */
public enum HazardCategory {
    TEMPERATURE("Temperature", "Temperature-related hazards"),
    PRECIPITATION("Precipitation", "Precipitation-related hazards"),
    WIND("Wind", "Wind-related hazards"),
    VISIBILITY("Visibility", "Visibility-related hazards"),
    ATMOSPHERIC("Atmospheric", "Atmospheric condition hazards"),
    NATURAL_DISASTER("Natural Disaster", "Major natural disaster related hazards");

    private final String displayName;
    private final String description;

    HazardCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the human-readable display name for this hazard category.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description of this hazard category.
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
