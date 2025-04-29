package dev.solace.twiggle.service.impl.weather;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.solace.twiggle.exception.CustomException;
import dev.solace.twiggle.exception.ErrorCode;
import dev.solace.twiggle.service.dto.weather.WeatherResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WeatherFacadeTest {

    @Mock
    private WeatherApiClient weatherApiClient;

    @Mock
    private WeatherMapper weatherMapper;

    @Mock
    private HazardEngine hazardEngine;

    @InjectMocks
    private WeatherFacade weatherFacade;

    private static final String LONDON = "London";
    private static final double LAT = 51.5074;
    private static final double LON = -0.1278;
    private static final int FORECAST_DAYS = 5;
    private static final String MOCK_WEATHER_JSON = "{\"data\": {\"current_condition\": [{\"temp_C\": 20}]}}";

    private WeatherResponse mockWeatherResponse;

    @BeforeEach
    void setUp() {
        mockWeatherResponse = new WeatherResponse();
        mockWeatherResponse.setLocationName(LONDON);
        mockWeatherResponse.setTemperature(20.0);
        mockWeatherResponse.setHumidity(70);
        mockWeatherResponse.setWindSpeed(10.0);
        mockWeatherResponse.setCloudCover(30);
        mockWeatherResponse.setCloudType("Clear");

        // Set up other necessary properties
        mockWeatherResponse.setHazards(new HashMap<>());
        mockWeatherResponse.setAirHazards(Collections.emptyList());
        mockWeatherResponse.setPlantHazards(Collections.emptyList());

        // Set up common mocks with lenient mode
        lenient()
                .when(hazardEngine.generatePlantHazards(
                        anyDouble(), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyString()))
                .thenReturn(Collections.emptyList());

        lenient()
                .when(hazardEngine.generateAirQualityHazards(
                        anyString(), any(Double.class), any(Double.class), any(Double.class), any(Double.class)))
                .thenReturn(Collections.emptyList());
    }

    @Test
    void getCurrentWeather_shouldReturnWeatherResponse() throws IOException {
        // Arrange
        when(weatherApiClient.getCurrentWeather(LONDON)).thenReturn(MOCK_WEATHER_JSON);
        when(weatherMapper.parseWeatherJson(MOCK_WEATHER_JSON)).thenReturn(mockWeatherResponse);

        Map<HazardType, HazardLevel> hazards = new HashMap<>();
        hazards.put(HazardType.EXTREME_HEAT, HazardLevel.LOW);

        when(hazardEngine.determineHazards(any(WeatherResponse.class))).thenReturn(hazards);
        when(hazardEngine.determineOverallHazardLevel(hazards)).thenReturn(HazardLevel.LOW);

        // Act
        WeatherResponse result = weatherFacade.getCurrentWeather(LONDON);

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocationName());
        assertEquals(20.0, result.getTemperature());
        assertEquals(HazardLevel.LOW, result.getOverallHazardLevel());
        verify(weatherApiClient, times(1)).getCurrentWeather(LONDON);
        verify(weatherMapper, times(1)).parseWeatherJson(MOCK_WEATHER_JSON);
    }

    @Test
    void getCurrentWeatherByCoordinates_shouldReturnWeatherResponse() throws IOException {
        // Arrange
        when(weatherApiClient.getCurrentWeatherByCoordinates(LAT, LON)).thenReturn(MOCK_WEATHER_JSON);
        when(weatherMapper.parseWeatherJson(MOCK_WEATHER_JSON)).thenReturn(mockWeatherResponse);

        Map<HazardType, HazardLevel> hazards = new HashMap<>();
        hazards.put(HazardType.EXTREME_HEAT, HazardLevel.LOW);

        when(hazardEngine.determineHazards(any(WeatherResponse.class))).thenReturn(hazards);
        when(hazardEngine.determineOverallHazardLevel(hazards)).thenReturn(HazardLevel.LOW);

        // Act
        WeatherResponse result = weatherFacade.getCurrentWeatherByCoordinates(LAT, LON);

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocationName());
        assertEquals(20.0, result.getTemperature());
        assertEquals(HazardLevel.LOW, result.getOverallHazardLevel());
        verify(weatherApiClient, times(1)).getCurrentWeatherByCoordinates(LAT, LON);
        verify(weatherMapper, times(1)).parseWeatherJson(MOCK_WEATHER_JSON);
    }

    @Test
    void getWeatherForecast_shouldReturnWeatherResponseWithForecast() throws IOException {
        // Arrange
        when(weatherApiClient.getWeatherForecast(LONDON, FORECAST_DAYS)).thenReturn(MOCK_WEATHER_JSON);
        when(weatherMapper.parseWeatherJson(MOCK_WEATHER_JSON)).thenReturn(mockWeatherResponse);

        Map<HazardType, HazardLevel> hazards = new HashMap<>();
        hazards.put(HazardType.EXTREME_HEAT, HazardLevel.LOW);

        when(hazardEngine.determineHazards(any(WeatherResponse.class))).thenReturn(hazards);
        when(hazardEngine.determineOverallHazardLevel(hazards)).thenReturn(HazardLevel.LOW);

        // Act
        WeatherResponse result = weatherFacade.getWeatherForecast(LONDON, FORECAST_DAYS);

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocationName());
        assertEquals(20.0, result.getTemperature());
        assertEquals(HazardLevel.LOW, result.getOverallHazardLevel());
        verify(weatherApiClient, times(1)).getWeatherForecast(LONDON, FORECAST_DAYS);
        verify(weatherMapper, times(1)).parseWeatherJson(MOCK_WEATHER_JSON);
    }

    @Test
    void getWeatherForecastByCoordinates_shouldReturnWeatherResponseWithForecast() throws IOException {
        // Arrange
        when(weatherApiClient.getWeatherForecastByCoordinates(LAT, LON, FORECAST_DAYS))
                .thenReturn(MOCK_WEATHER_JSON);
        when(weatherMapper.parseWeatherJson(MOCK_WEATHER_JSON)).thenReturn(mockWeatherResponse);

        Map<HazardType, HazardLevel> hazards = new HashMap<>();
        hazards.put(HazardType.EXTREME_HEAT, HazardLevel.LOW);

        when(hazardEngine.determineHazards(any(WeatherResponse.class))).thenReturn(hazards);
        when(hazardEngine.determineOverallHazardLevel(hazards)).thenReturn(HazardLevel.LOW);

        // Act
        WeatherResponse result = weatherFacade.getWeatherForecastByCoordinates(LAT, LON, FORECAST_DAYS);

        // Assert
        assertNotNull(result);
        assertEquals(LONDON, result.getLocationName());
        assertEquals(20.0, result.getTemperature());
        assertEquals(HazardLevel.LOW, result.getOverallHazardLevel());
        verify(weatherApiClient, times(1)).getWeatherForecastByCoordinates(LAT, LON, FORECAST_DAYS);
        verify(weatherMapper, times(1)).parseWeatherJson(MOCK_WEATHER_JSON);
    }

    @Test
    void enrichWithHazards_shouldAddHazardInformation() throws IOException {
        // Arrange
        Map<HazardType, HazardLevel> hazards = new HashMap<>();
        hazards.put(HazardType.EXTREME_HEAT, HazardLevel.HIGH);
        hazards.put(HazardType.THUNDERSTORM, HazardLevel.MODERATE);

        // Setup all mocks needed for the public method
        when(weatherApiClient.getCurrentWeather(LONDON)).thenReturn(MOCK_WEATHER_JSON);
        when(weatherMapper.parseWeatherJson(MOCK_WEATHER_JSON)).thenReturn(mockWeatherResponse);

        when(hazardEngine.determineHazards(mockWeatherResponse)).thenReturn(hazards);
        when(hazardEngine.determineOverallHazardLevel(hazards)).thenReturn(HazardLevel.HIGH);

        // Act - call a public method that internally uses enrichWithHazards
        WeatherResponse result = weatherFacade.getCurrentWeather(LONDON);

        // Assert
        assertNotNull(result);
        assertEquals(HazardLevel.HIGH, result.getOverallHazardLevel());
        assertEquals(2, result.getHazards().size());
        assertTrue(result.getHazards().containsKey(HazardType.EXTREME_HEAT));
        assertEquals(HazardLevel.HIGH, result.getHazards().get(HazardType.EXTREME_HEAT));
    }

    @Test
    void handleApiError_shouldPropagateCustomException() throws IOException {
        // Arrange
        CustomException mockException =
                new CustomException("API Error", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.EXTERNAL_API_ERROR);
        when(weatherApiClient.getCurrentWeather(LONDON)).thenThrow(mockException);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            weatherFacade.getCurrentWeather(LONDON);
        });
        assertEquals("API Error", exception.getMessage());
    }

    @Test
    void handleParsingError_shouldThrowCustomException() throws IOException {
        // Arrange
        when(weatherApiClient.getCurrentWeather(LONDON)).thenReturn(MOCK_WEATHER_JSON);
        when(weatherMapper.parseWeatherJson(MOCK_WEATHER_JSON)).thenThrow(new RuntimeException("Parsing error"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            weatherFacade.getCurrentWeather(LONDON);
        });
        assertTrue(exception.getMessage().contains("Error processing weather data"));
    }
}
