package dev.solace.twiggle.service.impl.weather;

/**
 * Enum representing different cloud types based on cloud cover percentage.
 */
public enum CloudType {
    CLEAR("Clear", "Open sky with 0-10% cloud cover"),
    CIRRUS("Cirrus", "High, thin, wispy clouds"),
    CUMULUS("Cumulus", "Puffy, cotton-like clouds"),
    ALTOCUMULUS("Altocumulus", "Mid-level puffy clouds"),
    STRATOCUMULUS("Stratocumulus", "Low-level, puffy, gray clouds"),
    STRATUS("Stratus", "Low, gray overcast clouds"),
    NIMBOSTRATUS("Nimbostratus", "Dark, low rain clouds");

    private final String displayName;
    private final String description;

    CloudType(String displayName, String description) {
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
     * Determine cloud type based on cloud cover percentage
     */
    public static CloudType fromCloudCover(int cloudCover) {
        if (cloudCover < 10) {
            return CLEAR;
        } else if (cloudCover < 30) {
            return CIRRUS;
        } else if (cloudCover < 50) {
            return CUMULUS;
        } else if (cloudCover < 70) {
            return ALTOCUMULUS;
        } else if (cloudCover < 85) {
            return STRATOCUMULUS;
        } else {
            return STRATUS;
        }
    }
}
