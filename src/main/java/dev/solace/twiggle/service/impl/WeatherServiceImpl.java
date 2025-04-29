package dev.solace.twiggle.service.impl;

import dev.solace.twiggle.dto.WeatherDTO;
import dev.solace.twiggle.exception.CustomException;
import dev.solace.twiggle.exception.ErrorCode;
import dev.solace.twiggle.service.WeatherService;
import dev.solace.twiggle.service.dto.weather.WeatherResponse;
import dev.solace.twiggle.service.impl.weather.WeatherFacade;
import dev.solace.twiggle.service.impl.weather.WeatherMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Implementation of WeatherService that provides weather data using the World
 * Weather Online API. Acts as an adapter between the Weather API and our internal DTOs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private static final int GARDEN_WEATHER_FORECAST_DAYS = 3;

    // Use the WeatherFacade instead of individual components
    private final WeatherFacade weatherFacade;
    private final WeatherMapper weatherMapper;

    @Override
    public WeatherDTO getCurrentWeather(String location) {
        log.info("Fetching current weather for location: {}", location);
        try {
            WeatherResponse response = weatherFacade.getCurrentWeather(location);
            return weatherMapper.convertResponseToDTO(response);
        } catch (Exception e) {
            log.error("Error fetching current weather for {}: {}", location, e.getMessage(), e);
            throw new CustomException(
                    "Failed to retrieve current weather data",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    @Override
    public WeatherDTO getCurrentWeatherByCoordinates(double latitude, double longitude) {
        log.info("Fetching current weather for coordinates: {}, {}", latitude, longitude);
        try {
            WeatherResponse response = weatherFacade.getCurrentWeatherByCoordinates(latitude, longitude);
            return weatherMapper.convertResponseToDTO(response);
        } catch (Exception e) {
            log.error(
                    "Error fetching current weather for coordinates {},{}: {}", latitude, longitude, e.getMessage(), e);
            throw new CustomException(
                    "Failed to retrieve current weather data",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    @Override
    public WeatherDTO getWeatherForecast(String location, int days) {
        log.info("Fetching weather forecast for location: {} for {} days", location, days);
        try {
            WeatherResponse response = weatherFacade.getWeatherForecast(location, days);
            return weatherMapper.convertResponseToDTO(response);
        } catch (Exception e) {
            log.error("Error fetching weather forecast for {}: {}", location, e.getMessage(), e);
            throw new CustomException(
                    "Failed to retrieve weather forecast data",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    @Override
    public WeatherDTO getWeatherForecastByCoordinates(double latitude, double longitude, int days) {
        log.info("Fetching weather forecast for coordinates: {}, {} for {} days", latitude, longitude, days);
        try {
            WeatherResponse response = weatherFacade.getWeatherForecastByCoordinates(latitude, longitude, days);
            return weatherMapper.convertResponseToDTO(response);
        } catch (Exception e) {
            log.error(
                    "Error fetching weather forecast for coordinates {},{}: {}",
                    latitude,
                    longitude,
                    e.getMessage(),
                    e);
            throw new CustomException(
                    "Failed to retrieve weather forecast data",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    @Override
    public WeatherDTO getGardenWeather(String location, Optional<String> gardenPlanId) {
        log.info(
                "Fetching garden weather for location: {}, garden plan ID: {}",
                location,
                gardenPlanId.orElse("not provided"));

        WeatherDTO weather = getWeatherForecast(location, GARDEN_WEATHER_FORECAST_DAYS);
        addGardeningAdvice(weather);
        return weather;
    }

    @Override
    public WeatherDTO getGardenWeatherByCoordinates(double latitude, double longitude, Optional<String> gardenPlanId) {
        log.info(
                "Fetching garden weather for coordinates: {}, {}, garden plan ID: {}",
                latitude,
                longitude,
                gardenPlanId.orElse("not provided"));

        WeatherDTO weather = getWeatherForecastByCoordinates(latitude, longitude, GARDEN_WEATHER_FORECAST_DAYS);
        addGardeningAdvice(weather);
        return weather;
    }

    /**
     * Add gardening advice to weather data
     */
    private void addGardeningAdvice(WeatherDTO weather) {
        if (weather.getHumidity() > 80) {
            weather.setGardeningAdvice("High humidity may promote fungal growth. Consider fungicide application.");
        } else if (weather.getTemperature() > 30) {
            weather.setGardeningAdvice("High temperatures expected. Ensure plants are well watered.");
        } else if (weather.getPrecipitation() > 10) {
            weather.setGardeningAdvice("Heavy rain expected. Check drainage systems and protect sensitive plants.");
        } else {
            weather.setGardeningAdvice("Weather conditions are favorable for gardening activities.");
        }
    }
}
