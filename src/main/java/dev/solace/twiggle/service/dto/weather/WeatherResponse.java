package dev.solace.twiggle.service.dto.weather;

import dev.solace.twiggle.service.impl.weather.HazardLevel;
import dev.solace.twiggle.service.impl.weather.HazardType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for weather data response with hazard information.
 * Used as the main response object from the weather facade.
 */
@Data
@NoArgsConstructor
public class WeatherResponse {
    // Location information
    private String locationName;
    private Double latitude;
    private Double longitude;

    // Basic weather properties
    private Double temperature;
    private Double feelsLikeTemperature;
    private String temperatureUnit = "Celsius";
    private Integer humidity;
    private Double windSpeed;
    private String windSpeedUnit = "km/h";
    private String windDirection;
    private Integer windDirectionDegrees;
    private Integer cloudCover;
    private String cloudType;
    private Double precipitation;
    private String precipitationType;
    private Double precipitationAmount;
    private Double pressure;
    private String pressureUnit = "hPa";
    private Double visibility;
    private String visibilityType;
    private Double uvIndex;

    // Time information
    private LocalDateTime observationTime;
    private Boolean isDay;
    private String timeZone;

    // Air quality information
    private String airQualityIndex;
    private Double pm25;
    private Double pm10;
    private Double ozone;
    private Double no2;

    // Condition summary
    private String condition;
    private String weatherIcon;

    // Hazard information
    private Map<HazardType, HazardLevel> hazards = new HashMap<>();
    private HazardLevel overallHazardLevel;
    private List<String> airHazards = new ArrayList<>();
    private List<String> plantHazards = new ArrayList<>();
    private String gardeningAdvice;

    // Forecast information
    private List<ForecastItem> forecast = new ArrayList<>();
    private List<Map<String, Object>> hourlyForecast = new ArrayList<>();

    /**
     * Nested class for forecast items
     */
    @Data
    @NoArgsConstructor
    public static class ForecastItem {
        private LocalDateTime forecastTime;
        private String date;
        private Double temperature;
        private Double minTemperature;
        private Double maxTemperature;
        private Double humidity;
        private Double precipitation;
        private Double windSpeed;
        private String windDirection;
        private String condition;
        private String cloudType;
        private Integer cloudCover;
        private List<String> alerts = new ArrayList<>();
    }

    // Additional getters and setters to handle primitives vs wrapper types

    public boolean isDay() {
        return isDay != null && isDay;
    }

    public void setDay(boolean isDay) {
        this.isDay = isDay;
    }
}
