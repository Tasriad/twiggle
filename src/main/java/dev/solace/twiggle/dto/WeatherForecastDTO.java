package dev.solace.twiggle.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for weather forecast data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastDTO {
    private String location;
    private List<ForecastDayDTO> days = new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ForecastDayDTO> getDays() {
        return days;
    }

    public void setDays(List<ForecastDayDTO> days) {
        this.days = days;
    }

    /**
     * Represents forecast data for a single day.
     */
    public static class ForecastDayDTO {
        private LocalDate date;
        private double minTemperature;
        private double maxTemperature;
        private double avgTemperature;
        private double totalPrecipitation;
        private double avgHumidity;
        private double maxWindSpeed;
        private double uvIndex;
        private String condition;
        private String weatherIcon;
        private String cloudType;
        private String precipitationType;
        private List<String> alerts = new ArrayList<>();
        private List<HourlyForecastDTO> hourlyForecast = new ArrayList<>();

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public void setDate(String dateStr) {
            if (dateStr != null && !dateStr.isEmpty()) {
                // Try to parse in format like "2023-05-01"
                this.date = LocalDate.parse(dateStr);
            }
        }

        public double getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(double minTemperature) {
            this.minTemperature = minTemperature;
        }

        public void setMinTemperature(Double minTemperature) {
            if (minTemperature != null) {
                this.minTemperature = minTemperature;
            }
        }

        public double getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(double maxTemperature) {
            this.maxTemperature = maxTemperature;
        }

        public void setMaxTemperature(Double maxTemperature) {
            if (maxTemperature != null) {
                this.maxTemperature = maxTemperature;
            }
        }

        public double getAvgTemperature() {
            return avgTemperature;
        }

        public void setAvgTemperature(double avgTemperature) {
            this.avgTemperature = avgTemperature;
        }

        public void setAvgTemperature(Double avgTemperature) {
            if (avgTemperature != null) {
                this.avgTemperature = avgTemperature;
            }
        }

        public double getTotalPrecipitation() {
            return totalPrecipitation;
        }

        public void setTotalPrecipitation(double totalPrecipitation) {
            this.totalPrecipitation = totalPrecipitation;
        }

        public void setTotalPrecipitation(Double totalPrecipitation) {
            if (totalPrecipitation != null) {
                this.totalPrecipitation = totalPrecipitation;
            }
        }

        public double getAvgHumidity() {
            return avgHumidity;
        }

        public void setAvgHumidity(double avgHumidity) {
            this.avgHumidity = avgHumidity;
        }

        public void setAvgHumidity(Double avgHumidity) {
            if (avgHumidity != null) {
                this.avgHumidity = avgHumidity;
            }
        }

        public double getMaxWindSpeed() {
            return maxWindSpeed;
        }

        public void setMaxWindSpeed(double maxWindSpeed) {
            this.maxWindSpeed = maxWindSpeed;
        }

        public void setMaxWindSpeed(Double maxWindSpeed) {
            if (maxWindSpeed != null) {
                this.maxWindSpeed = maxWindSpeed;
            }
        }

        public double getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(double uvIndex) {
            this.uvIndex = uvIndex;
        }

        public void setUvIndex(Double uvIndex) {
            if (uvIndex != null) {
                this.uvIndex = uvIndex;
            }
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getCloudType() {
            return cloudType;
        }

        public void setCloudType(String cloudType) {
            this.cloudType = cloudType;
        }

        public String getPrecipitationType() {
            return precipitationType;
        }

        public void setPrecipitationType(String precipitationType) {
            this.precipitationType = precipitationType;
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

        public List<HourlyForecastDTO> getHourlyForecast() {
            return hourlyForecast;
        }

        public void setHourlyForecast(List<HourlyForecastDTO> hourlyForecast) {
            this.hourlyForecast = hourlyForecast;
        }
    }

    /**
     * Represents forecast data for a specific hour.
     */
    public static class HourlyForecastDTO {
        private LocalDateTime time;
        private double temperature;
        private double feelsLikeTemperature;
        private double humidity;
        private double windSpeed;
        private int windDirection;
        private String windDirectionText;
        private String precipitationType;
        private double precipitationAmount;
        private int cloudCover;
        private String cloudType;
        private double visibility;
        private String visibilityType;
        private String condition;
        private String weatherIcon;

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
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
            return windDirection;
        }

        public void setWindDirection(int windDirection) {
            this.windDirection = windDirection;
        }

        public String getWindDirectionText() {
            return windDirectionText;
        }

        public void setWindDirectionText(String windDirectionText) {
            this.windDirectionText = windDirectionText;
        }

        public String getPrecipitationType() {
            return precipitationType;
        }

        public void setPrecipitationType(String precipitationType) {
            this.precipitationType = precipitationType;
        }

        public double getPrecipitationAmount() {
            return precipitationAmount;
        }

        public void setPrecipitationAmount(double precipitationAmount) {
            this.precipitationAmount = precipitationAmount;
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

        public double getVisibility() {
            return visibility;
        }

        public void setVisibility(double visibility) {
            this.visibility = visibility;
        }

        public String getVisibilityType() {
            return visibilityType;
        }

        public void setVisibilityType(String visibilityType) {
            this.visibilityType = visibilityType;
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
    }
}
