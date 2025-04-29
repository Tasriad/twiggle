package dev.solace.twiggle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for weather data.
 * This is the canonical DTO used for weather data across the application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {
    @NotBlank(message = "Location cannot be blank")
    private String location;

    // Location coordinates
    private Double latitude;
    private Double longitude;

    @NotNull(message = "Timestamp is required") @PastOrPresent(message = "Timestamp must be in the past or present")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    // Weather observation time
    private LocalDateTime observationTime;

    @NotNull(message = "Temperature is required") private Double temperature;

    @NotBlank(message = "Temperature unit is required")
    private String temperatureUnit = "Celsius";

    private Double feelsLikeTemperature;

    @NotNull(message = "Humidity is required") @Min(value = 0, message = "Humidity must be between 0 and 100")
    @Max(value = 100, message = "Humidity must be between 0 and 100")
    private Double humidity;

    @NotNull(message = "Wind speed is required") @PositiveOrZero(message = "Wind speed must be a positive number or zero")
    private Double windSpeed;

    @NotBlank(message = "Wind speed unit is required")
    private String windSpeedUnit = "km/h";

    private String windDirection;
    private String windDirectionText;

    @Min(value = 0, message = "Cloud cover must be between 0 and 100")
    @Max(value = 100, message = "Cloud cover must be between 0 and 100")
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

    @Size(max = 1000, message = "Gardening advice is too long")
    private String gardeningAdvice;

    private String condition;
    private String weatherIcon;
    private String timeZone;
    private List<Map<String, Object>> hourlyForecast = new ArrayList<>();

    @NotNull(message = "Is day is required") private Boolean isDay;

    @NotNull(message = "PM2.5 is required") private Double pm25;

    @NotNull(message = "PM10 is required") private Double pm10;

    @NotNull(message = "Ozone is required") private Double ozone;

    @NotNull(message = "NO2 is required") private Double no2;

    private String airQualityIndex;

    @NotBlank(message = "Overall hazard level is required")
    private String overallHazardLevel;

    private List<String> airHazards = new ArrayList<>();
    private List<String> plantHazards = new ArrayList<>();

    @Size(max = 10, message = "Too many alerts listed")
    private List<String> alerts = new ArrayList<>();

    private List<ForecastItem> forecast = new ArrayList<>();

    @NotBlank(message = "Hour is required")
    private String hour;

    /**
     * Nested class for forecast items
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
        private String conditions;
        private String cloudType;
        private Integer cloudCover;
        private List<String> alerts = new ArrayList<>();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }

    public void setFeelsLikeTemperature(double feelsLikeTemperature) {
        this.feelsLikeTemperature = feelsLikeTemperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindDirection() {
        return Integer.parseInt(windDirection);
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = String.valueOf(windDirection);
    }

    public String getWindDirectionText() {
        return windDirection;
    }

    public void setWindDirectionText(String windDirectionText) {
        this.windDirection = windDirectionText;
    }

    public String getPrecipitationType() {
        return precipitationType;
    }

    public void setPrecipitationType(String precipitationType) {
        this.precipitationType = precipitationType;
    }

    public double getPrecipitationAmount() {
        return precipitation;
    }

    public void setPrecipitationAmount(double precipitationAmount) {
        this.precipitation = precipitationAmount;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public int getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(int cloudCover) {
        this.cloudCover = cloudCover;
    }

    public String getCloudType() {
        return cloudType;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

    public Double getVisibility() {
        return visibility;
    }

    public void setVisibility(Double visibility) {
        this.visibility = visibility;
    }

    public String getVisibilityType() {
        return visibilityType;
    }

    public void setVisibilityType(String visibilityType) {
        this.visibilityType = visibilityType;
    }

    public double getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(double uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getAirQualityIndex() {
        return airQualityIndex;
    }

    public void setAirQualityIndex(String airQualityIndex) {
        this.airQualityIndex = airQualityIndex;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }

    public List<Map<String, Object>> getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(List<Map<String, Object>> hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public Boolean getIsDay() {
        return isDay;
    }

    public void setIsDay(Boolean isDay) {
        this.isDay = isDay;
    }

    public String getPressureUnit() {
        return pressureUnit;
    }

    public void setPressureUnit(String pressureUnit) {
        this.pressureUnit = pressureUnit;
    }

    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    public Double getOzone() {
        return ozone;
    }

    public void setOzone(Double ozone) {
        this.ozone = ozone;
    }

    public Double getNo2() {
        return no2;
    }

    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    public String getOverallHazardLevel() {
        return overallHazardLevel;
    }

    public void setOverallHazardLevel(String overallHazardLevel) {
        this.overallHazardLevel = overallHazardLevel;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
