package dev.solace.twiggle.service.impl.weather;

/**
 * Interface for weather API clients.
 * Abstracts the API implementation details to allow for different providers.
 */
public interface WeatherApiClient {

    /**
     * Get current weather data for a location.
     *
     * @param location The location to get weather for (city name, postal code, etc.)
     * @return Raw JSON string from the API
     */
    String getCurrentWeather(String location);

    /**
     * Get current weather data for coordinates.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return Raw JSON string from the API
     */
    String getCurrentWeatherByCoordinates(double latitude, double longitude);

    /**
     * Get weather forecast data for a location.
     *
     * @param location The location to get forecast for (city name, postal code, etc.)
     * @param days Number of forecast days to retrieve
     * @return Raw JSON string from the API
     */
    String getWeatherForecast(String location, int days);

    /**
     * Get weather forecast data for coordinates.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param days Number of forecast days to retrieve
     * @return Raw JSON string from the API
     */
    String getWeatherForecastByCoordinates(double latitude, double longitude, int days);
}
