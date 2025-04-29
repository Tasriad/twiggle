package dev.solace.twiggle.service.impl.weather;

import dev.solace.twiggle.service.dto.weather.WeatherResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Facade for weather-related operations.
 * Orchestrates the collaboration between API client, mapper and hazard engine.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherFacade {

    private final WeatherApiClient weatherApiClient;
    private final WeatherMapper weatherMapper;
    private final HazardEngine hazardEngine;

    /**
     * Get current weather for a location.
     *
     * @param location The location to get weather for
     * @return Weather data with hazards
     * @throws IOException If there's an error fetching or parsing the data
     */
    public WeatherResponse getCurrentWeather(String location) throws IOException {
        log.debug("Fetching current weather through facade for location: {}", location);
        try {
            String apiResponse = weatherApiClient.getCurrentWeather(location);
            WeatherResponse response = weatherMapper.parseWeatherJson(apiResponse);

            // Enrich with hazard information
            enrichWithHazards(response);

            return response;
        } catch (IOException e) {
            throw e; // Re-throw IOExceptions as they might be handled by caller
        } catch (Exception e) {
            log.error("Error processing weather data: {}", e.getMessage(), e);
            throw new dev.solace.twiggle.exception.CustomException(
                    "Error processing weather data: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    dev.solace.twiggle.exception.ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    /**
     * Get current weather for coordinates.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return Weather data with hazards
     * @throws IOException If there's an error fetching or parsing the data
     */
    public WeatherResponse getCurrentWeatherByCoordinates(double latitude, double longitude) throws IOException {
        log.debug("Fetching current weather through facade for coordinates: {}, {}", latitude, longitude);
        try {
            String apiResponse = weatherApiClient.getCurrentWeatherByCoordinates(latitude, longitude);
            WeatherResponse response = weatherMapper.parseWeatherJson(apiResponse);

            // Enrich with hazard information
            enrichWithHazards(response);

            return response;
        } catch (IOException e) {
            throw e; // Re-throw IOExceptions as they might be handled by caller
        } catch (Exception e) {
            log.error("Error processing weather data: {}", e.getMessage(), e);
            throw new dev.solace.twiggle.exception.CustomException(
                    "Error processing weather data: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    dev.solace.twiggle.exception.ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    /**
     * Get weather forecast for a location.
     *
     * @param location The location to get forecast for
     * @param days Number of days to include in the forecast
     * @return Weather data with forecast and hazards
     * @throws IOException If there's an error fetching or parsing the data
     */
    public WeatherResponse getWeatherForecast(String location, int days) throws IOException {
        log.debug("Fetching weather forecast through facade for location: {} for {} days", location, days);
        try {
            String apiResponse = weatherApiClient.getWeatherForecast(location, days);
            WeatherResponse response = weatherMapper.parseWeatherJson(apiResponse);

            // Enrich with hazard information
            enrichWithHazards(response);

            return response;
        } catch (IOException e) {
            throw e; // Re-throw IOExceptions as they might be handled by caller
        } catch (Exception e) {
            log.error("Error processing weather data: {}", e.getMessage(), e);
            throw new dev.solace.twiggle.exception.CustomException(
                    "Error processing weather data: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    dev.solace.twiggle.exception.ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    /**
     * Get weather forecast for coordinates.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param days Number of days to include in the forecast
     * @return Weather data with forecast and hazards
     * @throws IOException If there's an error fetching or parsing the data
     */
    public WeatherResponse getWeatherForecastByCoordinates(double latitude, double longitude, int days)
            throws IOException {
        log.debug(
                "Fetching weather forecast through facade for coordinates: {}, {} for {} days",
                latitude,
                longitude,
                days);
        try {
            String apiResponse = weatherApiClient.getWeatherForecastByCoordinates(latitude, longitude, days);
            WeatherResponse response = weatherMapper.parseWeatherJson(apiResponse);

            // Enrich with hazard information
            enrichWithHazards(response);

            return response;
        } catch (IOException e) {
            throw e; // Re-throw IOExceptions as they might be handled by caller
        } catch (Exception e) {
            log.error("Error processing weather data: {}", e.getMessage(), e);
            throw new dev.solace.twiggle.exception.CustomException(
                    "Error processing weather data: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    dev.solace.twiggle.exception.ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    /**
     * Enrich the weather response with hazard information.
     *
     * @param response The weather response to enrich
     */
    private void enrichWithHazards(WeatherResponse response) {
        // Handle null values with defaults
        double temperature = response.getTemperature() != null ? response.getTemperature() : 20.0;
        int humidity = response.getHumidity() != null ? response.getHumidity() : 50;
        double uvIndex = response.getUvIndex() != null ? response.getUvIndex() : 5.0;
        double precipitation = response.getPrecipitation() != null ? response.getPrecipitation() : 0.0;
        double windSpeed = response.getWindSpeed() != null ? response.getWindSpeed() : 0.0;
        String airQualityIndex = response.getAirQualityIndex() != null
                ? response.getAirQualityIndex()
                : AirQuality.MODERATE.getDisplayName();

        // Note: visibility is intentionally not given a default value to allow proper null handling

        // Generate plant hazards based on weather conditions
        response.setPlantHazards(hazardEngine.generatePlantHazards(
                temperature, humidity, uvIndex, precipitation, windSpeed, airQualityIndex));

        // Generate air hazards based on air quality and pollutants
        if (response.getAirHazards() == null || response.getAirHazards().isEmpty()) {
            Double pm25 = response.getPm25() != null ? response.getPm25() : 0.0;
            Double pm10 = response.getPm10() != null ? response.getPm10() : 0.0;
            Double ozone = response.getOzone() != null ? response.getOzone() : 0.0;
            Double no2 = response.getNo2() != null ? response.getNo2() : 0.0;

            response.setAirHazards(hazardEngine.generateAirQualityHazards(airQualityIndex, pm25, pm10, ozone, no2));
        }

        // Determine overall hazard level
        response.setHazards(hazardEngine.determineHazards(response));
        response.setOverallHazardLevel(hazardEngine.determineOverallHazardLevel(response.getHazards()));
    }
}
