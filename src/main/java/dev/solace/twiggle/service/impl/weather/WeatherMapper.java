package dev.solace.twiggle.service.impl.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.solace.twiggle.dto.WeatherDTO;
import dev.solace.twiggle.dto.WeatherForecastDTO;
import dev.solace.twiggle.service.dto.weather.WeatherResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping API responses to DTOs.
 * Isolates JSON parsing logic to reduce complexity in the service layer.
 */
@Component
public class WeatherMapper {
    private final ObjectMapper objectMapper;

    public WeatherMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parse weather JSON response into WeatherResponse object.
     * This is the main entry point for the facade to parse API responses.
     *
     * @param jsonData Raw JSON data from the weather API
     * @return Parsed WeatherResponse object
     * @throws IOException If there's an error parsing the JSON data
     */
    public WeatherResponse parseWeatherJson(String jsonData) throws IOException {
        if (jsonData == null || jsonData.trim().isEmpty()) {
            throw new dev.solace.twiggle.exception.CustomException(
                    "Weather API returned empty or null response",
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    dev.solace.twiggle.exception.ErrorCode.EXTERNAL_API_ERROR);
        }

        try {
            JsonNode root = objectMapper.readTree(jsonData);
            WeatherResponse response = new WeatherResponse();

            // Parse location information
            parseLocation(root, response);

            // Parse current weather data
            parseCurrentWeather(root, response);

            // Parse air quality data if available
            parseAirQuality(root, response);

            // Parse forecast data if available
            parseForecast(root, response);

            // Set default values for potentially null fields
            if (response.getLocationName() == null) {
                response.setLocationName("Unknown Location");
            }

            if (response.getTemperature() == null) {
                response.setTemperature(20.0); // Default temperature in Celsius
            }

            if (response.getUvIndex() == null) {
                response.setUvIndex(0.0);
            }

            if (response.getVisibility() == null) {
                response.setVisibility(10.0); // Default visibility in km
            }

            return response;
        } catch (Exception e) {
            throw new dev.solace.twiggle.exception.CustomException(
                    "Failed to parse weather data: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    dev.solace.twiggle.exception.ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    /**
     * Parse location information from the JSON response
     */
    private void parseLocation(JsonNode root, WeatherResponse response) {
        if (root.has("location")) {
            JsonNode location = root.get("location");
            response.setLocationName(extractString(location, "name"));
            response.setLatitude(extractDouble(location, "lat"));
            response.setLongitude(extractDouble(location, "lon"));
            response.setTimeZone(extractString(location, "tz_id"));
        } else if (root.has("nearest_area") && root.path("nearest_area").size() > 0) {
            JsonNode area = root.path("nearest_area").get(0);
            if (area.has("areaName") && area.path("areaName").size() > 0) {
                response.setLocationName(
                        area.path("areaName").get(0).path("value").asText());
            }
            if (area.has("latitude") && area.has("longitude")) {
                response.setLatitude(Double.parseDouble(area.path("latitude").asText()));
                response.setLongitude(Double.parseDouble(area.path("longitude").asText()));
            }
        }
    }

    /**
     * Parse current weather data from the JSON response
     */
    private void parseCurrentWeather(JsonNode root, WeatherResponse response) {
        if (root.has("current")) {
            JsonNode current = root.get("current");

            // Basic weather properties
            response.setTemperature(extractDouble(current, "temp_c"));
            response.setFeelsLikeTemperature(extractDouble(current, "feelslike_c"));
            response.setHumidity(extractInt(current, "humidity"));
            response.setWindSpeed(extractDouble(current, "wind_kph"));
            response.setWindDirection(extractString(current, "wind_dir"));
            response.setWindDirectionDegrees(extractInt(current, "wind_degree"));
            response.setCloudCover(extractInt(current, "cloud"));
            response.setPrecipitation(extractDouble(current, "precip_mm"));
            response.setPrecipitationAmount(extractDouble(current, "precip_mm"));
            response.setPressure(extractDouble(current, "pressure_mb"));
            response.setVisibility(extractDouble(current, "vis_km"));
            response.setUvIndex(extractDouble(current, "uv"));
            response.setIsDay(extractInt(current, "is_day") == 1);

            // Parse observation time
            if (current.has("last_updated_epoch")) {
                long epochTime = current.get("last_updated_epoch").asLong();
                response.setObservationTime(
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), ZoneId.systemDefault()));
            }

            // Parse condition
            if (current.has("condition")) {
                response.setCondition(extractString(current, "condition", "text"));
                response.setWeatherIcon(extractString(current, "condition", "icon"));
            }

            // Determine cloud type based on cloud cover
            Integer cloudCover = extractInt(current, "cloud");
            CloudType cloudType = CloudType.fromCloudCover(cloudCover != null ? cloudCover : 0);
            response.setCloudType(cloudType.getDisplayName());

            // Determine precipitation type based on temperature and condition
            Double temperature = extractDouble(current, "temp_c");
            String condition = extractString(current, "condition", "text");
            // If no condition text is available, use "none" as default
            String conditionText = condition != null && !condition.isEmpty() ? condition : "none";
            response.setPrecipitationType(
                    PrecipitationType.fromString(conditionText).getDisplayName());

            // Determine visibility type
            Double visibility = extractDouble(current, "vis_km");
            Double visibilityMiles = null;
            if (visibility != null) {
                visibilityMiles = visibility * 0.621371; // Convert km to miles
            }
            response.setVisibilityType(
                    VisibilityType.fromVisibility(visibilityMiles).getDisplayName());
        }
    }

    /**
     * Parse air quality data from the JSON response
     */
    private void parseAirQuality(JsonNode root, WeatherResponse response) {
        if (root.has("current") && root.get("current").has("air_quality")) {
            JsonNode airQuality = root.get("current").get("air_quality");

            // Get EPA index and convert to AirQuality enum
            Integer epaIndex = extractInt(airQuality, "us-epa-index");
            AirQuality airQualityEnum =
                    AirQuality.fromEpaIndex(epaIndex != null ? epaIndex : 2); // Default to moderate (2)
            response.setAirQualityIndex(airQualityEnum.getDisplayName());

            // Extract pollutant data
            response.setPm25(extractDouble(airQuality, "pm2_5"));
            response.setPm10(extractDouble(airQuality, "pm10"));
            response.setOzone(extractDouble(airQuality, "o3"));
            response.setNo2(extractDouble(airQuality, "no2"));

            // Get air hazards based on air quality
            response.setAirHazards(getAirHazards(
                    airQualityEnum, response.getPm25(), response.getPm10(), response.getOzone(), response.getNo2()));
        } else {
            // Default to moderate if no air quality data
            response.setAirQualityIndex(AirQuality.MODERATE.getDisplayName());
        }
    }

    /**
     * Parse forecast data from the JSON response
     */
    private void parseForecast(JsonNode root, WeatherResponse response) {
        if (root.has("forecast") && root.get("forecast").has("forecastday")) {
            JsonNode forecastDays = root.get("forecast").get("forecastday");
            List<WeatherResponse.ForecastItem> forecastItems = new ArrayList<>();

            for (JsonNode day : forecastDays) {
                WeatherResponse.ForecastItem item = new WeatherResponse.ForecastItem();

                item.setDate(extractString(day, "date"));

                // Parse day information
                if (day.has("day")) {
                    JsonNode dayInfo = day.get("day");
                    item.setMaxTemperature(extractDouble(dayInfo, "maxtemp_c"));
                    item.setMinTemperature(extractDouble(dayInfo, "mintemp_c"));
                    item.setTemperature(extractDouble(dayInfo, "avgtemp_c"));
                    item.setHumidity(extractDouble(dayInfo, "avghumidity"));
                    item.setPrecipitation(extractDouble(dayInfo, "totalprecip_mm"));
                    item.setWindSpeed(extractDouble(dayInfo, "maxwind_kph"));
                    item.setCloudCover(0); // Often not available in day summary

                    // Parse condition
                    if (dayInfo.has("condition")) {
                        item.setCondition(extractString(dayInfo, "condition", "text"));
                    }
                }

                // Add any alerts
                if (root.has("alerts") && root.get("alerts").has("alert")) {
                    JsonNode alerts = root.get("alerts").get("alert");
                    for (JsonNode alert : alerts) {
                        item.getAlerts().add(extractString(alert, "headline"));
                    }
                }

                forecastItems.add(item);
            }

            response.setForecast(forecastItems);

            // Also process hourly forecast data if available
            if (forecastDays.size() > 0 && forecastDays.get(0).has("hour")) {
                response.setHourlyForecast(
                        processHourlyForecast(forecastDays.get(0).get("hour")));
            }
        }
    }

    /**
     * Process hourly forecast data
     */
    private List<Map<String, Object>> processHourlyForecast(JsonNode hourlyData) {
        List<Map<String, Object>> hourlyForecast = new ArrayList<>();

        for (JsonNode hour : hourlyData) {
            Map<String, Object> hourData = new HashMap<>();

            // Extract timestamp
            if (hour.has("time_epoch")) {
                long epochTime = hour.get("time_epoch").asLong();
                LocalDateTime dateTime =
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), ZoneId.systemDefault());
                hourData.put("time", dateTime);
            } else if (hour.has("time")) {
                hourData.put("timeString", hour.get("time").asText());
            }

            // Basic weather properties
            hourData.put("temperature", extractDouble(hour, "temp_c"));
            hourData.put("feelsLike", extractDouble(hour, "feelslike_c"));
            hourData.put("humidity", extractDouble(hour, "humidity"));
            hourData.put("windSpeed", extractDouble(hour, "wind_kph"));
            hourData.put("windDirection", extractString(hour, "wind_dir"));
            hourData.put("precipitation", extractDouble(hour, "precip_mm"));
            hourData.put("chanceOfRain", extractDouble(hour, "chance_of_rain"));
            hourData.put("isDay", extractInt(hour, "is_day") == 1);

            // Condition
            if (hour.has("condition")) {
                hourData.put("condition", extractString(hour, "condition", "text"));
                hourData.put("icon", extractString(hour, "condition", "icon"));
            }

            hourlyForecast.add(hourData);
        }

        return hourlyForecast;
    }

    /**
     * Parse current weather response from the API.
     */
    public WeatherDTO parseCurrentWeatherResponse(String responseBody) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);

        WeatherDTO weatherDTO = new WeatherDTO();
        weatherDTO.setTemperature(extractDouble(rootNode, "current", "temp_c"));
        weatherDTO.setHumidity(extractDouble(rootNode, "current", "humidity"));
        weatherDTO.setWindSpeed(extractDouble(rootNode, "current", "wind_kph"));
        weatherDTO.setWindDirection(extractInt(rootNode, "current", "wind_degree"));
        weatherDTO.setPrecipitation(extractDouble(rootNode, "current", "precip_mm"));
        weatherDTO.setUvIndex(extractDouble(rootNode, "current", "uv"));
        weatherDTO.setCloudCover(extractInt(rootNode, "current", "cloud"));
        weatherDTO.setCondition(extractString(rootNode, "current", "condition", "text"));
        weatherDTO.setIsDay(extractInt(rootNode, "current", "is_day") == 1);

        // Extract and set visibility
        Double visibility = extractDouble(rootNode, "current", "vis_km");
        weatherDTO.setVisibility(visibility);
        String visibilityType = visibility != null
                ? VisibilityType.fromVisibility(visibility * 0.621371).getDisplayName()
                : "Unknown";
        weatherDTO.setVisibilityType(visibilityType);

        // Determine cloud type based on cloud cover
        int cloudCover =
                extractInt(rootNode, "current", "cloud") != null ? extractInt(rootNode, "current", "cloud") : 0;
        CloudType cloudType = CloudType.fromCloudCover(cloudCover);
        weatherDTO.setCloudType(cloudType.getDisplayName());

        // Process Air Quality if available
        if (rootNode.path("current").has("air_quality")) {
            JsonNode airQuality = rootNode.path("current").path("air_quality");
            double epaIndex = extractDouble(airQuality, "us-epa-index");
            AirQuality airQualityEnum = AirQuality.fromEpaIndex((int) epaIndex);
            weatherDTO.setAirQualityIndex(airQualityEnum.getDisplayName());

            weatherDTO.setPm25(extractDouble(airQuality, "pm2_5"));
            weatherDTO.setPm10(extractDouble(airQuality, "pm10"));
            weatherDTO.setOzone(extractDouble(airQuality, "o3"));
            weatherDTO.setNo2(extractDouble(airQuality, "no2"));
        } else {
            weatherDTO.setAirQualityIndex(AirQuality.MODERATE.getDisplayName());
        }

        // Determine precipitation type based on temperature and condition
        double temperature = extractDouble(rootNode, "current", "temp_c");
        String condition = extractString(rootNode, "current", "condition", "text");
        weatherDTO.setPrecipitationType(PrecipitationType.fromString(condition).getDisplayName());

        return weatherDTO;
    }

    /**
     * Parse weather response from the API with a specified number of days.
     *
     * @param responseBody Raw JSON from the API
     * @param days Number of days to include in the forecast
     * @param location Location string for the weather data
     * @return Weather DTO with all parsed information
     * @throws IOException If there's an error parsing the JSON
     */
    public WeatherDTO parseWeatherResponse(String responseBody, int days, String location) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        WeatherDTO weatherDTO = parseCurrentWeatherResponse(responseBody);

        // Set location from API response or use provided location
        String locationName = extractLocationName(rootNode, location);
        weatherDTO.setLocation(locationName);

        // Add forecast if requested more than 1 day
        if (days > 1 && rootNode.has("forecast")) {
            weatherDTO.setForecast(parseForecast(rootNode, days));
        }

        // Add plant hazards
        List<String> plantHazards = generatePlantHazards(weatherDTO);
        weatherDTO.setPlantHazards(plantHazards);

        return weatherDTO;
    }

    /**
     * Get air hazards based on air quality and pollutant data.
     */
    private List<String> getAirHazards(AirQuality airQuality, Double pm25, Double pm10, Double ozone, Double no2) {
        List<String> hazards = new ArrayList<>();

        // Add hazards based on air quality level
        switch (airQuality) {
            case GOOD:
                // No hazards for good air quality
                break;
            case MODERATE:
                hazards.add("Mild pollen and low-level particulates");
                break;
            case UNHEALTHY_FOR_SENSITIVE_GROUPS:
                hazards.add("May cause respiratory symptoms in sensitive individuals");
                break;
            case UNHEALTHY:
                hazards.add("Increased likelihood of adverse respiratory effects in general population");
                break;
            case VERY_UNHEALTHY:
                hazards.add("Significant respiratory effects can be expected in general population");
                break;
            case HAZARDOUS:
                hazards.add("Serious respiratory effects and health impacts for all");
                break;
        }

        // Add specific pollutant hazards if high levels
        if (pm25 != null && pm25 > 35) {
            hazards.add("High PM2.5 (fine particulate matter) levels");
        }
        if (pm10 != null && pm10 > 150) {
            hazards.add("High PM10 (coarse particulate matter) levels");
        }
        if (ozone != null && ozone > 100) {
            hazards.add("High ozone levels");
        }
        if (no2 != null && no2 > 100) {
            hazards.add("High nitrogen dioxide levels");
        }

        return hazards;
    }

    /**
     * Generate plant hazards based on weather conditions.
     * Note: This is a simplified version and would typically be handled by HazardEngine.
     */
    private List<String> generatePlantHazards(WeatherDTO weather) {
        List<String> hazards = new ArrayList<>();

        // Add basic hazards based on conditions
        if (weather.getTemperature() > 28) {
            hazards.add("Heat stress risk for sensitive plants");
        }
        if (weather.getTemperature() < 5) {
            hazards.add("Frost risk for outdoor plants");
        }
        if (weather.getHumidity() > 85) {
            hazards.add("High humidity may increase fungal disease risk");
        }

        return hazards;
    }

    /**
     * Extract location name from the JSON response.
     */
    private String extractLocationName(JsonNode data, String defaultLocation) {
        if (data.has("location") && data.get("location").has("name")) {
            return data.get("location").get("name").asText();
        } else if (data.has("nearest_area") && data.path("nearest_area").size() > 0) {
            JsonNode nearestArea = data.path("nearest_area").get(0);
            if (nearestArea.has("areaName") && nearestArea.path("areaName").size() > 0) {
                return nearestArea.path("areaName").get(0).path("value").asText();
            }
        }
        return defaultLocation;
    }

    /**
     * Parse forecast weather response from the API.
     */
    public List<WeatherForecastDTO> parseForecastResponse(String responseBody, int days) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode forecastDays = rootNode.path("forecast").path("forecastday");

        List<WeatherForecastDTO> forecastList = new ArrayList<>();
        int daysToProcess = Math.min(days, forecastDays.size());

        for (int i = 0; i < daysToProcess; i++) {
            JsonNode dayNode = forecastDays.get(i);
            WeatherForecastDTO forecast = new WeatherForecastDTO();

            // Create a forecast day
            WeatherForecastDTO.ForecastDayDTO forecastDay = new WeatherForecastDTO.ForecastDayDTO();
            forecastDay.setDate(extractString(dayNode, "date"));
            forecastDay.setMaxTemperature(extractDouble(dayNode, "day", "maxtemp_c"));
            forecastDay.setMinTemperature(extractDouble(dayNode, "day", "mintemp_c"));
            forecastDay.setAvgTemperature(extractDouble(dayNode, "day", "avgtemp_c"));
            forecastDay.setMaxWindSpeed(extractDouble(dayNode, "day", "maxwind_kph"));
            forecastDay.setTotalPrecipitation(extractDouble(dayNode, "day", "totalprecip_mm"));
            forecastDay.setAvgHumidity(extractDouble(dayNode, "day", "avghumidity"));
            forecastDay.setUvIndex(extractDouble(dayNode, "day", "uv"));
            forecastDay.setCondition(extractString(dayNode, "day", "condition", "text"));

            // Determine cloud type based on conditions
            String condition =
                    extractString(dayNode, "day", "condition", "text").toLowerCase();
            CloudType cloudType;
            if (condition.contains("clear") || condition.contains("sunny")) {
                cloudType = CloudType.CLEAR;
            } else if (condition.contains("partly cloudy") || condition.contains("scattered clouds")) {
                cloudType = CloudType.CUMULUS;
            } else if (condition.contains("cloudy") || condition.contains("broken clouds")) {
                cloudType = CloudType.STRATOCUMULUS;
            } else {
                cloudType = CloudType.STRATUS;
            }
            forecastDay.setCloudType(cloudType.getDisplayName());

            // Determine precipitation type based on condition
            PrecipitationType precipType = PrecipitationType.fromString(condition);
            forecastDay.setPrecipitationType(precipType.getDisplayName());

            // Add forecast day to forecast list
            forecast.setLocation(extractLocationName(rootNode, ""));
            forecast.getDays().add(forecastDay);
            forecastList.add(forecast);
        }

        return forecastList;
    }

    /**
     * Parse hourly forecast response from the API.
     */
    public List<WeatherDTO> parseHourlyForecastResponse(String responseBody, int hours) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode forecastDay = rootNode.path("forecast").path("forecastday").get(0);
        JsonNode hourlyData = forecastDay.path("hour");

        List<WeatherDTO> hourlyForecast = new ArrayList<>();
        int hoursToProcess = Math.min(hours, hourlyData.size());

        for (int i = 0; i < hoursToProcess; i++) {
            JsonNode hourNode = hourlyData.get(i);
            WeatherDTO weatherDTO = new WeatherDTO();

            weatherDTO.setHour(extractString(hourNode, "time").substring(11, 16)); // Extract time (HH:MM)
            weatherDTO.setTemperature(extractDouble(hourNode, "temp_c"));
            weatherDTO.setHumidity(extractDouble(hourNode, "humidity"));
            weatherDTO.setWindSpeed(extractDouble(hourNode, "wind_kph"));
            weatherDTO.setWindDirection(extractInt(hourNode, "wind_degree"));
            weatherDTO.setPrecipitation(extractDouble(hourNode, "precip_mm"));
            weatherDTO.setUvIndex(extractDouble(hourNode, "uv"));
            weatherDTO.setCloudCover(extractInt(hourNode, "cloud"));
            weatherDTO.setCondition(extractString(hourNode, "condition", "text"));
            weatherDTO.setIsDay(extractInt(hourNode, "is_day") == 1);

            // Determine cloud type based on cloud cover
            int cloudCover = extractInt(hourNode, "cloud");
            CloudType cloudType = CloudType.fromCloudCover(cloudCover);
            weatherDTO.setCloudType(cloudType.getDisplayName());

            // Determine precipitation type based on condition
            String condition = extractString(hourNode, "condition", "text");
            PrecipitationType precipType = PrecipitationType.fromString(condition);
            weatherDTO.setPrecipitationType(precipType.getDisplayName());

            // Set a default moderate air quality since hourly doesn't typically include this
            weatherDTO.setAirQualityIndex(AirQuality.MODERATE.getDisplayName());

            hourlyForecast.add(weatherDTO);
        }

        return hourlyForecast;
    }

    private Double extractDouble(JsonNode node, String... pathElements) {
        JsonNode currentNode = node;
        for (String element : pathElements) {
            currentNode = currentNode.path(element);
            if (currentNode.isMissingNode()) {
                return null;
            }
        }
        return currentNode.isDouble() || currentNode.isInt() ? currentNode.asDouble() : null;
    }

    private Integer extractInt(JsonNode node, String... pathElements) {
        JsonNode currentNode = node;
        for (String element : pathElements) {
            currentNode = currentNode.path(element);
            if (currentNode.isMissingNode()) {
                return null;
            }
        }
        return currentNode.isInt() ? currentNode.asInt() : null;
    }

    private String extractString(JsonNode node, String... pathElements) {
        JsonNode currentNode = node;
        for (String element : pathElements) {
            currentNode = currentNode.path(element);
            if (currentNode.isMissingNode()) {
                return "";
            }
        }
        return currentNode.isTextual() ? currentNode.asText() : "";
    }

    /**
     * Maps raw JSON weather data to a WeatherDTO object.
     *
     * @param jsonData The raw JSON weather data
     * @return A mapped WeatherDTO object
     * @throws IOException If there's an error parsing the JSON
     */
    public WeatherDTO mapWeatherData(String jsonData) throws IOException {
        JsonNode root = objectMapper.readTree(jsonData);

        WeatherDTO weatherDTO = new WeatherDTO();

        // Map basic weather info
        weatherDTO.setTemperature(extractTemperature(root));
        weatherDTO.setWindSpeed(extractWindSpeed(root));
        weatherDTO.setWindDirection(extractWindDirection(root));
        weatherDTO.setPrecipitationType(extractPrecipitationType(root));
        weatherDTO.setPrecipitationAmount(extractPrecipitationAmount(root));
        weatherDTO.setHumidity(extractHumidity(root));
        weatherDTO.setPressure(extractPressure(root));
        weatherDTO.setCloudCover(extractCloudCover(root));

        // Handle visibility which can be null
        Double visibility = extractVisibility(root);
        weatherDTO.setVisibility(visibility);

        weatherDTO.setHourlyForecast(extractHourlyForecast(root));

        return weatherDTO;
    }

    private double extractTemperature(JsonNode root) {
        // Extract temperature from API-specific JSON structure
        if (root.has("current") && root.get("current").has("temp_c")) {
            return root.get("current").get("temp_c").asDouble();
        }
        return 0.0; // Default if not found
    }

    private double extractWindSpeed(JsonNode root) {
        // Extract wind speed from API-specific JSON structure
        if (root.has("current") && root.get("current").has("wind_kph")) {
            return root.get("current").get("wind_kph").asDouble();
        }
        return 0.0; // Default if not found
    }

    private int extractWindDirection(JsonNode root) {
        // Extract wind direction in degrees from API-specific JSON structure
        if (root.has("current") && root.get("current").has("wind_degree")) {
            return root.get("current").get("wind_degree").asInt();
        }
        return 0; // Default if not found
    }

    private String extractPrecipitationType(JsonNode root) {
        // Extract precipitation type based on API-specific JSON structure
        if (root.has("current")
                && root.get("current").has("condition")
                && root.get("current").get("condition").has("text")) {
            String conditionText =
                    root.get("current").get("condition").get("text").asText().toLowerCase();

            if (conditionText.contains("snow")) {
                return PrecipitationType.SNOW.getDisplayName();
            } else if (conditionText.contains("sleet")) {
                return PrecipitationType.SLEET.getDisplayName();
            } else if (conditionText.contains("hail")) {
                return PrecipitationType.HAIL.getDisplayName();
            } else if (conditionText.contains("drizzle")) {
                return PrecipitationType.DRIZZLE.getDisplayName();
            } else if (conditionText.contains("rain") && conditionText.contains("freezing")) {
                return PrecipitationType.FREEZING_RAIN.getDisplayName();
            } else if (conditionText.contains("rain")) {
                return PrecipitationType.RAIN.getDisplayName();
            } else if (conditionText.contains("mixed")) {
                return PrecipitationType.MIXED.getDisplayName();
            }
        }

        // Check precipitation amount to determine if there's any precipitation
        double precipAmount = extractPrecipitationAmount(root);
        if (precipAmount > 0) {
            return PrecipitationType.RAIN.getDisplayName(); // Default to rain if precipitation exists but type unknown
        }

        return PrecipitationType.NONE.getDisplayName();
    }

    private double extractPrecipitationAmount(JsonNode root) {
        // Extract precipitation amount from API-specific JSON structure
        if (root.has("current") && root.get("current").has("precip_mm")) {
            return root.get("current").get("precip_mm").asDouble();
        }
        return 0.0; // Default if not found
    }

    private int extractHumidity(JsonNode root) {
        // Extract humidity percentage from API-specific JSON structure
        if (root.has("current") && root.get("current").has("humidity")) {
            return root.get("current").get("humidity").asInt();
        }
        return 0; // Default if not found
    }

    private double extractPressure(JsonNode root) {
        // Extract pressure in hPa from API-specific JSON structure
        if (root.has("current") && root.get("current").has("pressure_mb")) {
            return root.get("current").get("pressure_mb").asDouble();
        }
        return 0.0; // Default if not found
    }

    private int extractCloudCover(JsonNode root) {
        // Extract cloud cover percentage from API-specific JSON structure
        if (root.has("current") && root.get("current").has("cloud")) {
            return root.get("current").get("cloud").asInt();
        }
        return 0; // Default if not found
    }

    private Double extractVisibility(JsonNode root) {
        // Extract visibility in km from API-specific JSON structure
        if (root.has("current") && root.get("current").has("vis_km")) {
            return root.get("current").get("vis_km").asDouble();
        }
        return 10.0; // Default visibility of 10.0 km if not found
    }

    private List<Map<String, Object>> extractHourlyForecast(JsonNode root) {
        List<Map<String, Object>> hourlyForecast = new ArrayList<>();

        if (root.has("forecast") && root.get("forecast").has("forecastday")) {
            JsonNode forecastDays = root.get("forecast").get("forecastday");

            for (JsonNode day : forecastDays) {
                if (day.has("hour")) {
                    for (JsonNode hour : day.get("hour")) {
                        Map<String, Object> forecastEntry = new HashMap<>();

                        // Extract timestamp
                        if (hour.has("time_epoch")) {
                            long epochTime = hour.get("time_epoch").asLong();
                            LocalDateTime dateTime =
                                    LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), ZoneId.systemDefault());
                            forecastEntry.put("time", dateTime);
                        }

                        // Extract temperature
                        if (hour.has("temp_c")) {
                            forecastEntry.put("temperature", hour.get("temp_c").asDouble());
                        }

                        // Extract precipitation
                        if (hour.has("precip_mm")) {
                            forecastEntry.put(
                                    "precipitationAmount", hour.get("precip_mm").asDouble());
                        }

                        // Extract precipitation type
                        if (hour.has("condition") && hour.get("condition").has("text")) {
                            String conditionText =
                                    hour.get("condition").get("text").asText().toLowerCase();
                            PrecipitationType precipType = PrecipitationType.NONE;

                            if (conditionText.contains("snow")) {
                                precipType = PrecipitationType.SNOW;
                            } else if (conditionText.contains("sleet")) {
                                precipType = PrecipitationType.SLEET;
                            } else if (conditionText.contains("hail")) {
                                precipType = PrecipitationType.HAIL;
                            } else if (conditionText.contains("drizzle")) {
                                precipType = PrecipitationType.DRIZZLE;
                            } else if (conditionText.contains("rain") && conditionText.contains("freezing")) {
                                precipType = PrecipitationType.FREEZING_RAIN;
                            } else if (conditionText.contains("rain")) {
                                precipType = PrecipitationType.RAIN;
                            } else if (conditionText.contains("mixed")) {
                                precipType = PrecipitationType.MIXED;
                            }

                            forecastEntry.put("precipitationType", precipType.getDisplayName());
                        }

                        hourlyForecast.add(forecastEntry);
                    }
                }
            }
        }

        return hourlyForecast;
    }

    /**
     * Handles conversion of WeatherResponse to WeatherDTO for backward compatibility
     */
    public WeatherDTO convertResponseToDTO(WeatherResponse response) {
        WeatherDTO weatherDTO = new WeatherDTO();

        // Location information
        weatherDTO.setLocation(response.getLocationName() != null ? response.getLocationName() : "Unknown Location");
        weatherDTO.setLatitude(response.getLatitude());
        weatherDTO.setLongitude(response.getLongitude());

        // Basic weather properties
        weatherDTO.setTemperature(response.getTemperature() != null ? response.getTemperature() : 0.0);
        weatherDTO.setTemperatureUnit(
                response.getTemperatureUnit() != null ? response.getTemperatureUnit() : "Celsius");
        weatherDTO.setFeelsLikeTemperature(
                response.getFeelsLikeTemperature() != null ? response.getFeelsLikeTemperature() : 0.0);
        weatherDTO.setHumidity(
                response.getHumidity() != null ? response.getHumidity().doubleValue() : 0.0);
        weatherDTO.setWindSpeed(response.getWindSpeed() != null ? response.getWindSpeed() : 0.0);
        weatherDTO.setWindSpeedUnit(response.getWindSpeedUnit() != null ? response.getWindSpeedUnit() : "km/h");

        if (response.getWindDirectionDegrees() != null) {
            weatherDTO.setWindDirection(response.getWindDirectionDegrees());
        } else if (response.getWindDirection() != null) {
            weatherDTO.setWindDirectionText(response.getWindDirection());
        } else {
            weatherDTO.setWindDirection(0); // Default direction
        }

        weatherDTO.setCloudCover(response.getCloudCover() != null ? response.getCloudCover() : 0);
        weatherDTO.setCloudType(response.getCloudType() != null ? response.getCloudType() : "Clear");
        weatherDTO.setPrecipitation(response.getPrecipitation() != null ? response.getPrecipitation() : 0.0);
        weatherDTO.setPrecipitationType(
                response.getPrecipitationType() != null ? response.getPrecipitationType() : "None");
        weatherDTO.setPrecipitationAmount(
                response.getPrecipitationAmount() != null ? response.getPrecipitationAmount() : 0.0);
        weatherDTO.setPressure(response.getPressure() != null ? response.getPressure() : 1013.0); // Standard pressure
        weatherDTO.setPressureUnit(response.getPressureUnit() != null ? response.getPressureUnit() : "hPa");

        // Pass visibility directly without default value to preserve null
        weatherDTO.setVisibility(response.getVisibility());
        weatherDTO.setVisibilityType(response.getVisibilityType() != null ? response.getVisibilityType() : "Unknown");
        weatherDTO.setUvIndex(response.getUvIndex() != null ? response.getUvIndex() : 0.0);
        weatherDTO.setIsDay(response.isDay());

        // Set timestamp (required field)
        weatherDTO.setTimestamp(
                response.getObservationTime() != null ? response.getObservationTime() : LocalDateTime.now());
        weatherDTO.setObservationTime(response.getObservationTime());

        // Air quality information
        weatherDTO.setAirQualityIndex(
                response.getAirQualityIndex() != null
                        ? response.getAirQualityIndex()
                        : AirQuality.MODERATE.getDisplayName());
        weatherDTO.setPm25(response.getPm25() != null ? response.getPm25() : 0.0);
        weatherDTO.setPm10(response.getPm10() != null ? response.getPm10() : 0.0);
        weatherDTO.setOzone(response.getOzone() != null ? response.getOzone() : 0.0);
        weatherDTO.setNo2(response.getNo2() != null ? response.getNo2() : 0.0);

        // Condition summary
        weatherDTO.setCondition(response.getCondition() != null ? response.getCondition() : "Clear");
        weatherDTO.setWeatherIcon(response.getWeatherIcon());

        // Hazard information
        weatherDTO.setAirHazards(response.getAirHazards() != null ? response.getAirHazards() : new ArrayList<>());
        weatherDTO.setPlantHazards(response.getPlantHazards() != null ? response.getPlantHazards() : new ArrayList<>());
        weatherDTO.setGardeningAdvice(response.getGardeningAdvice());

        if (response.getOverallHazardLevel() != null) {
            weatherDTO.setOverallHazardLevel(response.getOverallHazardLevel().getDisplayName());
        } else {
            weatherDTO.setOverallHazardLevel(HazardLevel.NONE.getDisplayName());
        }

        // Set hour (required field)
        weatherDTO.setHour(weatherDTO.getTimestamp().getHour() + ":00");

        // Convert forecast items
        if (response.getForecast() != null && !response.getForecast().isEmpty()) {
            List<WeatherDTO.ForecastItem> forecastItems = new ArrayList<>();
            for (WeatherResponse.ForecastItem item : response.getForecast()) {
                WeatherDTO.ForecastItem forecastItem = WeatherDTO.ForecastItem.builder()
                        .forecastTime(item.getForecastTime())
                        .date(item.getDate())
                        .temperature(item.getTemperature())
                        .minTemperature(item.getMinTemperature())
                        .maxTemperature(item.getMaxTemperature())
                        .humidity(item.getHumidity())
                        .precipitation(item.getPrecipitation())
                        .windSpeed(item.getWindSpeed())
                        .windDirection(item.getWindDirection())
                        .conditions(item.getCondition())
                        .cloudType(item.getCloudType())
                        .cloudCover(item.getCloudCover())
                        .alerts(item.getAlerts() != null ? item.getAlerts() : new ArrayList<>())
                        .build();
                forecastItems.add(forecastItem);
            }
            weatherDTO.setForecast(forecastItems);
        } else {
            weatherDTO.setForecast(new ArrayList<>());
        }

        return weatherDTO;
    }

    private List<WeatherDTO.ForecastItem> parseForecast(JsonNode rootNode, int days) {
        List<WeatherDTO.ForecastItem> forecastItems = new ArrayList<>();

        if (rootNode.has("forecast") && rootNode.get("forecast").has("forecastday")) {
            JsonNode forecastDays = rootNode.get("forecast").get("forecastday");
            int daysToProcess = Math.min(days, forecastDays.size());

            for (int i = 0; i < daysToProcess; i++) {
                JsonNode day = forecastDays.get(i);

                WeatherDTO.ForecastItem item = WeatherDTO.ForecastItem.builder()
                        .forecastTime(LocalDateTime.now().plusDays(i)) // Placeholder, should extract from day
                        .temperature(extractDouble(day, "day", "avgtemp_c"))
                        .minTemperature(extractDouble(day, "day", "mintemp_c"))
                        .maxTemperature(extractDouble(day, "day", "maxtemp_c"))
                        .humidity(extractDouble(day, "day", "avghumidity"))
                        .precipitation(extractDouble(day, "day", "totalprecip_mm"))
                        .conditions(extractString(day, "day", "condition", "text"))
                        .build();

                forecastItems.add(item);
            }
        }

        return forecastItems;
    }
}
