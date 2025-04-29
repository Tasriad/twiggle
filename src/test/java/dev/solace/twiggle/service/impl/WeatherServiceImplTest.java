package dev.solace.twiggle.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.solace.twiggle.dto.WeatherDTO;
import dev.solace.twiggle.exception.CustomException;
import dev.solace.twiggle.service.dto.weather.WeatherResponse;
import dev.solace.twiggle.service.impl.weather.HazardLevel;
import dev.solace.twiggle.service.impl.weather.WeatherFacade;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private WeatherFacade weatherFacade;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    private static final String LONDON = "London";
    private static final double LAT = 51.5074;
    private static final double LON = -0.1278;
    private static final int FORECAST_DAYS = 5;
    private static final String CURRENT_WEATHER_FILE = "current_weather.json";
    private static final String FORECAST_WEATHER_FILE = "forecast_weather.json";
    private static final String GARDEN_FORECAST_WEATHER_FILE = "garden_forecast_weather.json";
    private static final String HIGH_HUMIDITY_WEATHER_FILE = "high_humidity_weather.json";
    private static final String HIGH_TEMP_WEATHER_FILE = "high_temp_weather.json";
    private static final String HEAVY_RAIN_WEATHER_FILE = "heavy_rain_weather.json";
    private static final String POOR_AIR_QUALITY_WEATHER_FILE = "poor_air_quality_weather.json";
    private static final String WEATHER_ALERT_FILE = "weather_alert.json";

    private WeatherResponse mockWeatherResponse;
    private WeatherResponse mockForecastResponse;
    private WeatherResponse mockGardenForecastResponse;
    private WeatherResponse mockHighHumidityResponse;
    private WeatherResponse mockHighTempResponse;
    private WeatherResponse mockHeavyRainResponse;
    private WeatherResponse mockPoorAirQualityResponse;
    private WeatherResponse mockWeatherAlertResponse;

    @BeforeEach
    void setUp() {
        // Initialize mock responses
        mockWeatherResponse = createMockWeatherResponse(20, 70, 0, 5, "Good", "Clear");
        mockForecastResponse = createMockForecastResponse(20, 70, 0, 5, "Good", "Clear", FORECAST_DAYS);
        mockGardenForecastResponse = createMockForecastResponse(20, 70, 0, 5, "Good", "Clear", 3);
        mockHighHumidityResponse = createMockForecastResponse(20, 85, 0, 5, "Good", "Clear", 3);
        mockHighTempResponse = createMockForecastResponse(32, 70, 0, 5, "Good", "Clear", 3);
        mockHeavyRainResponse = createMockForecastResponse(20, 70, 15, 5, "Good", "Rain", 3);
        mockPoorAirQualityResponse = createMockWeatherResponse(20, 70, 0, 5, "Unhealthy", "Clear");
        mockWeatherAlertResponse = createMockForecastResponseWithAlerts(20, 70, 0, 5, "Good", "Clear", FORECAST_DAYS);
    }

    private WeatherResponse createMockWeatherResponse(
            double temperature,
            int humidity,
            double precipitation,
            double uvIndex,
            String airQuality,
            String condition) {
        WeatherResponse response = new WeatherResponse();
        response.setLocationName(LONDON);
        response.setTemperature(temperature);
        response.setHumidity(humidity);
        response.setPrecipitation(precipitation);
        response.setUvIndex(uvIndex);
        response.setAirQualityIndex(airQuality);
        response.setCondition(condition);
        response.setCloudType("Clear");
        response.setPrecipitationType(precipitation > 0 ? "Rain" : "None");
        response.setWindSpeed(10.0);
        response.setWindDirection("N");
        response.setCloudCover(30);
        response.setAirHazards(new ArrayList<>());
        response.setPlantHazards(new ArrayList<>());
        response.setOverallHazardLevel(HazardLevel.LOW);

        // Add appropriate hazards based on conditions
        if (humidity > 80) {
            response.getPlantHazards().add("High humidity may increase fungal disease risk");
        }
        if (temperature > 30) {
            response.getPlantHazards().add("Heat stress risk for sensitive plants");
        }
        if (precipitation > 10) {
            response.getPlantHazards().add("Heavy rain may lead to soil erosion and waterlogging");
        }
        if (!airQuality.equals("Good") && !airQuality.equals("Moderate")) {
            response.getAirHazards().add("Poor air quality may affect sensitive plant species");
        }

        return response;
    }

    private WeatherResponse createMockForecastResponse(
            double temperature,
            int humidity,
            double precipitation,
            double uvIndex,
            String airQuality,
            String condition,
            int days) {
        WeatherResponse response =
                createMockWeatherResponse(temperature, humidity, precipitation, uvIndex, airQuality, condition);

        List<WeatherResponse.ForecastItem> forecastItems = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            WeatherResponse.ForecastItem item = new WeatherResponse.ForecastItem();
            item.setDate(String.format("2023-05-%02d", i + 1));
            item.setForecastTime(LocalDateTime.now().plusDays(i));
            item.setTemperature(temperature);
            item.setHumidity(Double.valueOf(humidity));
            item.setPrecipitation(precipitation);
            item.setWindSpeed(10.0);
            item.setWindDirection("N");
            item.setCondition(condition);
            item.setCloudType("Clear");
            item.setCloudCover(30);
            item.setAlerts(new ArrayList<>());
            forecastItems.add(item);
        }
        response.setForecast(forecastItems);

        return response;
    }

    private WeatherResponse createMockForecastResponseWithAlerts(
            double temperature,
            int humidity,
            double precipitation,
            double uvIndex,
            String airQuality,
            String condition,
            int days) {
        WeatherResponse response =
                createMockForecastResponse(temperature, humidity, precipitation, uvIndex, airQuality, condition, days);

        // Add alerts to the first forecast item
        List<String> alerts = new ArrayList<>();
        alerts.add("Severe Weather Alert: Heavy wind expected");
        response.getForecast().get(0).setAlerts(alerts);

        return response;
    }

    @Test
    void getCurrentWeather_shouldReturnWeatherDTO() throws IOException {
        // Arrange
        when(weatherFacade.getCurrentWeather(LONDON)).thenReturn(mockWeatherResponse);

        // Act
        WeatherDTO result = weatherService.getCurrentWeather(LONDON);

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocation());
        assertNotNull(result.getTemperature());
        assertNotNull(result.getHumidity());
        verify(weatherFacade, times(1)).getCurrentWeather(LONDON);
    }

    @Test
    void getCurrentWeatherByCoordinates_shouldReturnWeatherDTO() throws IOException {
        // Arrange
        when(weatherFacade.getCurrentWeatherByCoordinates(LAT, LON)).thenReturn(mockWeatherResponse);

        // Act
        WeatherDTO result = weatherService.getCurrentWeatherByCoordinates(LAT, LON);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTemperature());
        assertNotNull(result.getHumidity());
        verify(weatherFacade, times(1)).getCurrentWeatherByCoordinates(LAT, LON);
    }

    @Test
    void getWeatherForecast_shouldReturnWeatherDTOWithForecast() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecast(LONDON, FORECAST_DAYS)).thenReturn(mockForecastResponse);

        // Act
        WeatherDTO result = weatherService.getWeatherForecast(LONDON, FORECAST_DAYS);

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocation());
        assertNotNull(result.getForecast());
        verify(weatherFacade, times(1)).getWeatherForecast(LONDON, FORECAST_DAYS);
    }

    @Test
    void getWeatherForecastByCoordinates_shouldReturnWeatherDTOWithForecast() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecastByCoordinates(LAT, LON, FORECAST_DAYS))
                .thenReturn(mockForecastResponse);

        // Act
        WeatherDTO result = weatherService.getWeatherForecastByCoordinates(LAT, LON, FORECAST_DAYS);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getForecast());
        verify(weatherFacade, times(1)).getWeatherForecastByCoordinates(LAT, LON, FORECAST_DAYS);
    }

    @Test
    void getGardenWeather_shouldReturnWeatherDTOWithGardeningAdvice() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecast(LONDON, 3)).thenReturn(mockGardenForecastResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeather(LONDON, Optional.empty());

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocation());
        assertNotNull(result.getGardeningAdvice());
        verify(weatherFacade, times(1)).getWeatherForecast(LONDON, 3);
    }

    @Test
    void getGardenWeatherByCoordinates_shouldReturnWeatherDTOWithGardeningAdvice() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecastByCoordinates(LAT, LON, 3)).thenReturn(mockGardenForecastResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeatherByCoordinates(LAT, LON, Optional.empty());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGardeningAdvice());
        verify(weatherFacade, times(1)).getWeatherForecastByCoordinates(LAT, LON, 3);
    }

    @Test
    void handleApiError_shouldThrowCustomException() throws IOException {
        // Arrange
        when(weatherFacade.getCurrentWeather(LONDON))
                .thenThrow(new CustomException("API Error", HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> weatherService.getCurrentWeather(LONDON));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void verifyHighHumidityAdvice_shouldProvideAppropriateAdvice() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecast(LONDON, 3)).thenReturn(mockHighHumidityResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeather(LONDON, Optional.empty());

        // Assert
        assertNotNull(result.getGardeningAdvice());
        assertEquals(
                "High humidity may promote fungal growth. Consider fungicide application.",
                result.getGardeningAdvice());

        // Verify plant hazards contain humidity-related warning
        List<String> plantHazards = result.getPlantHazards();
        assertTrue(
                plantHazards.stream()
                        .anyMatch(hazard -> hazard.toLowerCase().contains("humidity")
                                || hazard.toLowerCase().contains("fungal")),
                "Plant hazards should include humidity or fungal related warning");
    }

    @Test
    void verifyHighTemperatureAdvice_shouldProvideAppropriateAdvice() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecast(LONDON, 3)).thenReturn(mockHighTempResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeather(LONDON, Optional.empty());

        // Assert
        assertNotNull(result.getGardeningAdvice());
        assertEquals("High temperatures expected. Ensure plants are well watered.", result.getGardeningAdvice());

        // Verify plant hazards contain temperature-related warning
        List<String> plantHazards = result.getPlantHazards();
        assertTrue(
                plantHazards.stream()
                        .anyMatch(hazard -> hazard.toLowerCase().contains("heat")
                                || hazard.toLowerCase().contains("temperature")),
                "Plant hazards should include heat or temperature related warning");
    }

    @Test
    void verifyHeavyRainAdvice_shouldProvideAppropriateAdvice() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecast(LONDON, 3)).thenReturn(mockHeavyRainResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeather(LONDON, Optional.empty());

        // Assert
        assertNotNull(result.getGardeningAdvice());
        assertEquals(
                "Heavy rain expected. Check drainage systems and protect sensitive plants.",
                result.getGardeningAdvice());

        // Verify plant hazards contain rain-related warning
        List<String> plantHazards = result.getPlantHazards();
        assertTrue(
                plantHazards.stream()
                        .anyMatch(hazard -> hazard.toLowerCase().contains("rain")
                                || hazard.toLowerCase().contains("water")),
                "Plant hazards should include rain or water related warning");
    }

    @Test
    void verifyDefaultGardeningAdvice_shouldProvideAppropriateAdvice() throws IOException {
        // Arrange
        WeatherResponse defaultResponse = createMockWeatherResponse(20, 70, 5, 5, "Good", "Clear");
        when(weatherFacade.getWeatherForecast(LONDON, 3)).thenReturn(defaultResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeather(LONDON, Optional.empty());

        // Assert
        assertNotNull(result.getGardeningAdvice());
        assertEquals("Weather conditions are favorable for gardening activities.", result.getGardeningAdvice());
    }

    @Test
    void verifyGardenWeatherWithGardenPlanId_shouldReturnWeatherDTO() throws IOException {
        // Arrange
        String gardenPlanId = "garden-123";
        when(weatherFacade.getWeatherForecast(LONDON, 3)).thenReturn(mockGardenForecastResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeather(LONDON, Optional.of(gardenPlanId));

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocation());
        assertNotNull(result.getGardeningAdvice());
        verify(weatherFacade, times(1)).getWeatherForecast(LONDON, 3);
    }

    @Test
    void verifyGardenWeatherByCoordinatesWithGardenPlanId_shouldReturnWeatherDTO() throws IOException {
        // Arrange
        String gardenPlanId = "garden-123";
        when(weatherFacade.getWeatherForecastByCoordinates(LAT, LON, 3)).thenReturn(mockGardenForecastResponse);

        // Act
        WeatherDTO result = weatherService.getGardenWeatherByCoordinates(LAT, LON, Optional.of(gardenPlanId));

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGardeningAdvice());
        verify(weatherFacade, times(1)).getWeatherForecastByCoordinates(LAT, LON, 3);
    }

    @Test
    void verifyParseWeatherResponseWithInvalidJson_shouldThrowCustomException() throws IOException {
        // Arrange
        when(weatherFacade.getCurrentWeather(LONDON)).thenThrow(new IOException("Parse error"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> weatherService.getCurrentWeather(LONDON));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void weatherResponseToWeatherDTOConversion_shouldPreserveAllData() throws IOException {
        // Arrange
        when(weatherFacade.getCurrentWeather(LONDON)).thenReturn(mockWeatherResponse);

        // Act
        WeatherDTO result = weatherService.getCurrentWeather(LONDON);

        // Assert
        assertEquals(mockWeatherResponse.getLocationName(), result.getLocation());
        assertEquals(mockWeatherResponse.getTemperature(), result.getTemperature());
        assertEquals(mockWeatherResponse.getHumidity(), result.getHumidity(), 0.01);
        assertEquals(mockWeatherResponse.getWindSpeed(), result.getWindSpeed());
        assertEquals(mockWeatherResponse.getWindDirection(), result.getWindDirection());
        assertEquals(mockWeatherResponse.getCloudCover(), result.getCloudCover());
        assertEquals(mockWeatherResponse.getCloudType(), result.getCloudType());
        assertEquals(mockWeatherResponse.getPrecipitation(), result.getPrecipitation());
        assertEquals(mockWeatherResponse.getPrecipitationType(), result.getPrecipitationType());
        assertEquals(mockWeatherResponse.getUvIndex(), result.getUvIndex());
        assertEquals(mockWeatherResponse.getAirQualityIndex(), result.getAirQualityIndex());
        assertEquals(mockWeatherResponse.getCondition(), result.getCondition());
        assertEquals(mockWeatherResponse.getPlantHazards(), result.getPlantHazards());
        assertEquals(mockWeatherResponse.getAirHazards(), result.getAirHazards());
        assertEquals(mockWeatherResponse.getOverallHazardLevel().getDisplayName(), result.getOverallHazardLevel());
    }

    @Test
    void weatherResponseToWeatherDTOConversion_withForecast_shouldPreserveAllData() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecast(LONDON, FORECAST_DAYS)).thenReturn(mockForecastResponse);

        // Act
        WeatherDTO result = weatherService.getWeatherForecast(LONDON, FORECAST_DAYS);

        // Assert
        assertEquals(FORECAST_DAYS, result.getForecast().size());

        // Check first forecast item
        WeatherResponse.ForecastItem expectedItem =
                mockForecastResponse.getForecast().get(0);
        WeatherDTO.ForecastItem actualItem = result.getForecast().get(0);

        assertEquals(expectedItem.getForecastTime(), actualItem.getForecastTime());
        assertEquals(expectedItem.getDate(), actualItem.getDate());
        assertEquals(expectedItem.getTemperature(), actualItem.getTemperature());
        assertEquals(expectedItem.getHumidity(), actualItem.getHumidity());
        assertEquals(expectedItem.getPrecipitation(), actualItem.getPrecipitation());
        assertEquals(expectedItem.getWindDirection(), actualItem.getWindDirection());
        assertEquals(expectedItem.getCondition(), actualItem.getConditions());
        assertEquals(expectedItem.getCloudType(), actualItem.getCloudType());
        assertEquals(expectedItem.getCloudCover(), actualItem.getCloudCover());
        assertEquals(expectedItem.getAlerts(), actualItem.getAlerts());
    }

    @Test
    void verifyWeatherAlerts_inForecastItems() throws IOException {
        // Arrange
        when(weatherFacade.getWeatherForecast(LONDON, FORECAST_DAYS)).thenReturn(mockWeatherAlertResponse);

        // Act
        WeatherDTO result = weatherService.getWeatherForecast(LONDON, FORECAST_DAYS);

        // Assert
        assertTrue(
                result.getForecast().stream()
                        .anyMatch(item ->
                                item.getAlerts() != null && !item.getAlerts().isEmpty()),
                "Forecast items should contain weather alerts");
        assertEquals(
                "Severe Weather Alert: Heavy wind expected",
                result.getForecast().get(0).getAlerts().get(0));
    }
}
