package dev.solace.twiggle.service.impl.weather;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.solace.twiggle.exception.CustomException;
import dev.solace.twiggle.service.dto.weather.WeatherResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherMapperTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private WeatherMapper weatherMapper;

    private String validCurrentWeatherJson;
    private String validForecastWeatherJson;
    private String invalidWeatherJson;

    @BeforeEach
    void setUp() {
        // Create sample JSON strings for testing
        validCurrentWeatherJson = "{\n" + "  \"data\": {\n"
                + "    \"request\": [\n"
                + "      {\n"
                + "        \"type\": \"City\",\n"
                + "        \"query\": \"London, United Kingdom\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"current_condition\": [\n"
                + "      {\n"
                + "        \"observation_time\": \"10:31 AM\",\n"
                + "        \"temp_C\": \"18\",\n"
                + "        \"temp_F\": \"64\",\n"
                + "        \"weatherCode\": \"116\",\n"
                + "        \"weatherIconUrl\": [\n"
                + "          {\n"
                + "            \"value\": \"https://cdn.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0002_sunny_intervals.png\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"weatherDesc\": [\n"
                + "          {\n"
                + "            \"value\": \"Partly cloudy\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"windspeedMiles\": \"11\",\n"
                + "        \"windspeedKmph\": \"18\",\n"
                + "        \"winddirDegree\": \"240\",\n"
                + "        \"winddir16Point\": \"WSW\",\n"
                + "        \"precipMM\": \"0.0\",\n"
                + "        \"precipInches\": \"0.0\",\n"
                + "        \"humidity\": \"73\",\n"
                + "        \"visibility\": \"10\",\n"
                + "        \"visibilityMiles\": \"6\",\n"
                + "        \"pressure\": \"1016\",\n"
                + "        \"pressureInches\": \"30\",\n"
                + "        \"cloudcover\": \"50\",\n"
                + "        \"FeelsLikeC\": \"18\",\n"
                + "        \"FeelsLikeF\": \"64\",\n"
                + "        \"uvIndex\": \"5\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"weather\": [],\n"
                + "    \"nearest_area\": [\n"
                + "      {\n"
                + "        \"latitude\": \"51.517\",\n"
                + "        \"longitude\": \"-0.106\",\n"
                + "        \"areaName\": [\n"
                + "          {\n"
                + "            \"value\": \"London\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"country\": [\n"
                + "          {\n"
                + "            \"value\": \"United Kingdom\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"region\": [\n"
                + "          {\n"
                + "            \"value\": \"City of London, Greater London\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"timezone\": [\n"
                + "          {\n"
                + "            \"value\": \"Europe/London\"\n"
                + "          }\n"
                + "        ]\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "}";

        validForecastWeatherJson = "{\n" + "  \"data\": {\n"
                + "    \"request\": [\n"
                + "      {\n"
                + "        \"type\": \"City\",\n"
                + "        \"query\": \"London, United Kingdom\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"current_condition\": [\n"
                + "      {\n"
                + "        \"observation_time\": \"10:31 AM\",\n"
                + "        \"temp_C\": \"18\",\n"
                + "        \"temp_F\": \"64\",\n"
                + "        \"weatherCode\": \"116\",\n"
                + "        \"weatherIconUrl\": [\n"
                + "          {\n"
                + "            \"value\": \"https://cdn.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0002_sunny_intervals.png\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"weatherDesc\": [\n"
                + "          {\n"
                + "            \"value\": \"Partly cloudy\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"windspeedMiles\": \"11\",\n"
                + "        \"windspeedKmph\": \"18\",\n"
                + "        \"winddirDegree\": \"240\",\n"
                + "        \"winddir16Point\": \"WSW\",\n"
                + "        \"precipMM\": \"0.0\",\n"
                + "        \"precipInches\": \"0.0\",\n"
                + "        \"humidity\": \"73\",\n"
                + "        \"visibility\": \"10\",\n"
                + "        \"visibilityMiles\": \"6\",\n"
                + "        \"pressure\": \"1016\",\n"
                + "        \"pressureInches\": \"30\",\n"
                + "        \"cloudcover\": \"50\",\n"
                + "        \"FeelsLikeC\": \"18\",\n"
                + "        \"FeelsLikeF\": \"64\",\n"
                + "        \"uvIndex\": \"5\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"weather\": [\n"
                + "      {\n"
                + "        \"date\": \"2023-06-01\",\n"
                + "        \"astronomy\": [\n"
                + "          {\n"
                + "            \"sunrise\": \"04:49 AM\",\n"
                + "            \"sunset\": \"09:07 PM\",\n"
                + "            \"moonrise\": \"05:19 PM\",\n"
                + "            \"moonset\": \"03:36 AM\",\n"
                + "            \"moon_phase\": \"Waxing Gibbous\",\n"
                + "            \"moon_illumination\": \"87\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"maxtempC\": \"22\",\n"
                + "        \"maxtempF\": \"72\",\n"
                + "        \"mintempC\": \"14\",\n"
                + "        \"mintempF\": \"57\",\n"
                + "        \"avgtempC\": \"18\",\n"
                + "        \"avgtempF\": \"65\",\n"
                + "        \"totalSnow_cm\": \"0.0\",\n"
                + "        \"sunHour\": \"15.9\",\n"
                + "        \"uvIndex\": \"6\",\n"
                + "        \"hourly\": [\n"
                + "          {\n"
                + "            \"time\": \"0\",\n"
                + "            \"tempC\": \"15\",\n"
                + "            \"tempF\": \"59\",\n"
                + "            \"windspeedMiles\": \"8\",\n"
                + "            \"windspeedKmph\": \"12\",\n"
                + "            \"winddirDegree\": \"248\",\n"
                + "            \"winddir16Point\": \"WSW\",\n"
                + "            \"weatherCode\": \"116\",\n"
                + "            \"weatherIconUrl\": [\n"
                + "              {\n"
                + "                \"value\": \"https://cdn.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0004_black_low_cloud.png\"\n"
                + "              }\n"
                + "            ],\n"
                + "            \"weatherDesc\": [\n"
                + "              {\n"
                + "                \"value\": \"Partly cloudy\"\n"
                + "              }\n"
                + "            ],\n"
                + "            \"precipMM\": \"0.0\",\n"
                + "            \"precipInches\": \"0.0\",\n"
                + "            \"humidity\": \"82\",\n"
                + "            \"visibility\": \"10\",\n"
                + "            \"visibilityMiles\": \"6\",\n"
                + "            \"pressure\": \"1017\",\n"
                + "            \"pressureInches\": \"30\",\n"
                + "            \"cloudcover\": \"26\",\n"
                + "            \"HeatIndexC\": \"15\",\n"
                + "            \"HeatIndexF\": \"59\",\n"
                + "            \"DewPointC\": \"12\",\n"
                + "            \"DewPointF\": \"54\",\n"
                + "            \"WindChillC\": \"14\",\n"
                + "            \"WindChillF\": \"57\",\n"
                + "            \"WindGustMiles\": \"13\",\n"
                + "            \"WindGustKmph\": \"20\",\n"
                + "            \"FeelsLikeC\": \"14\",\n"
                + "            \"FeelsLikeF\": \"57\",\n"
                + "            \"chanceofrain\": \"0\",\n"
                + "            \"chanceofremdry\": \"89\",\n"
                + "            \"chanceofwindy\": \"0\",\n"
                + "            \"chanceofovercast\": \"33\",\n"
                + "            \"chanceofsunshine\": \"85\",\n"
                + "            \"chanceoffrost\": \"0\",\n"
                + "            \"chanceofhightemp\": \"0\",\n"
                + "            \"chanceoffog\": \"0\",\n"
                + "            \"chanceofsnow\": \"0\",\n"
                + "            \"chanceofthunder\": \"0\",\n"
                + "            \"uvIndex\": \"1\"\n"
                + "          }\n"
                + "        ]\n"
                + "      }\n"
                + "    ],\n"
                + "    \"nearest_area\": [\n"
                + "      {\n"
                + "        \"latitude\": \"51.517\",\n"
                + "        \"longitude\": \"-0.106\",\n"
                + "        \"areaName\": [\n"
                + "          {\n"
                + "            \"value\": \"London\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"country\": [\n"
                + "          {\n"
                + "            \"value\": \"United Kingdom\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"region\": [\n"
                + "          {\n"
                + "            \"value\": \"City of London, Greater London\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"timezone\": [\n"
                + "          {\n"
                + "            \"value\": \"Europe/London\"\n"
                + "          }\n"
                + "        ]\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "}";

        invalidWeatherJson = "{ \"invalid\": \"json\" }";
    }

    @Test
    void parseWeatherJson_validCurrentWeather_shouldReturnWeatherResponse() throws IOException {
        // Act
        WeatherResponse response = weatherMapper.parseWeatherJson(validCurrentWeatherJson);

        // Assert
        assertNotNull(response);
        assertEquals("London", response.getLocationName());
        assertEquals(18, response.getTemperature());
        assertEquals(73, response.getHumidity());
        assertEquals(18, response.getWindSpeed());
        assertEquals("WSW", response.getWindDirection());
        assertEquals(240, response.getWindDirectionDegrees());
        assertEquals(50, response.getCloudCover());
        assertEquals("Partly cloudy", response.getCondition());
        assertEquals(0.0, response.getPrecipitation());
        assertEquals(5, response.getUvIndex());
        assertEquals(51.517, response.getLatitude(), 0.001);
        assertEquals(-0.106, response.getLongitude(), 0.001);
    }

    @Test
    void parseWeatherJson_validForecastWeather_shouldReturnWeatherResponseWithForecast() throws IOException {
        // Act
        WeatherResponse response = weatherMapper.parseWeatherJson(validForecastWeatherJson);

        // Assert
        assertNotNull(response);
        assertEquals("London", response.getLocationName());
        assertNotNull(response.getForecast());
        assertFalse(response.getForecast().isEmpty());
        assertEquals(1, response.getForecast().size());

        // Verify the forecast item
        WeatherResponse.ForecastItem forecastItem = response.getForecast().get(0);
        assertEquals("2023-06-01", forecastItem.getDate());
        assertEquals(22, forecastItem.getMaxTemperature());
        assertEquals(14, forecastItem.getMinTemperature());
        assertEquals(18, forecastItem.getTemperature());
    }

    @Test
    void parseWeatherJson_invalidJson_shouldThrowException() throws IOException {
        // Act & Assert
        assertThrows(CustomException.class, () -> weatherMapper.parseWeatherJson(invalidWeatherJson));
    }

    @Test
    void parseWeatherJson_null_shouldThrowException() throws IOException {
        // Act & Assert
        assertThrows(CustomException.class, () -> weatherMapper.parseWeatherJson(null));
    }

    @Test
    void parseWeatherJson_emptyJson_shouldThrowException() throws IOException {
        // Act & Assert
        assertThrows(CustomException.class, () -> weatherMapper.parseWeatherJson(""));
    }

    @Test
    void parseWeatherJson_missingRequiredFields_shouldHandleGracefully() throws IOException {
        // Arrange
        String missingFieldsJson = "{\n" + "  \"data\": {\n"
                + "    \"request\": [\n"
                + "      {\n"
                + "        \"type\": \"City\",\n"
                + "        \"query\": \"London, United Kingdom\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"nearest_area\": [\n"
                + "      {\n"
                + "        \"areaName\": [\n"
                + "          {\n"
                + "            \"value\": \"London\"\n"
                + "          }\n"
                + "        ]\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "}";

        // Act
        WeatherResponse response = weatherMapper.parseWeatherJson(missingFieldsJson);

        // Assert
        assertNotNull(response);
        assertEquals("London", response.getLocationName());
        assertEquals(0, response.getTemperature()); // Default values for missing data
        assertEquals(0, response.getHumidity());
    }

    @Test
    void parseWeatherJson_missingVisibility_shouldLeaveVisibilityNull() throws IOException {
        // Arrange
        String noVisibilityJson = "{\n" + "  \"data\": {\n"
                + "    \"request\": [\n"
                + "      {\n"
                + "        \"type\": \"City\",\n"
                + "        \"query\": \"London, United Kingdom\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"current_condition\": [\n"
                + "      {\n"
                + "        \"observation_time\": \"10:31 AM\",\n"
                + "        \"temp_C\": \"18\",\n"
                + "        \"temp_F\": \"64\",\n"
                + "        \"weatherCode\": \"116\",\n"
                + "        \"weatherDesc\": [\n"
                + "          {\n"
                + "            \"value\": \"Partly cloudy\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"windspeedMiles\": \"11\",\n"
                + "        \"windspeedKmph\": \"18\",\n"
                + "        \"winddirDegree\": \"240\",\n"
                + "        \"winddir16Point\": \"WSW\",\n"
                + "        \"precipMM\": \"0.0\",\n"
                + "        \"humidity\": \"73\",\n"
                + "        \"pressure\": \"1016\",\n"
                + "        \"cloudcover\": \"50\",\n"
                + "        \"FeelsLikeC\": \"18\",\n"
                + "        \"uvIndex\": \"5\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"nearest_area\": [\n"
                + "      {\n"
                + "        \"latitude\": \"51.517\",\n"
                + "        \"longitude\": \"-0.106\",\n"
                + "        \"areaName\": [\n"
                + "          {\n"
                + "            \"value\": \"London\"\n"
                + "          }\n"
                + "        ]\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "}";

        // Act
        WeatherResponse response = weatherMapper.parseWeatherJson(noVisibilityJson);

        // Assert
        assertNotNull(response);
        assertNull(response.getVisibility(), "Visibility should be null when missing from the API response");
        assertEquals("Unknown", response.getVisibilityType());
    }
}
