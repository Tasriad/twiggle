package dev.solace.twiggle.service.impl.weather;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class WeatherConditionTest {

    @Test
    void enumValues_shouldHaveCorrectProperties() {
        // Assert basic properties
        assertEquals("Clear", WeatherCondition.CLEAR.getDisplayName());
        assertEquals("Partly cloudy skies", WeatherCondition.PARTLY_CLOUDY.getDescription());
        assertEquals(HazardLevel.HIGH, WeatherCondition.HEAVY_RAIN.getHazardLevel());
        assertEquals(HazardCategory.PRECIPITATION, WeatherCondition.SNOW.getCategory());

        // Assert the new weatherCategory property
        assertEquals(WeatherCategory.CLEAR, WeatherCondition.CLEAR.getWeatherCategory());
        assertEquals(WeatherCategory.PARTLY_CLOUDY, WeatherCondition.PARTLY_CLOUDY.getWeatherCategory());
        assertEquals(WeatherCategory.CLOUDY, WeatherCondition.CLOUDY.getWeatherCategory());
        assertEquals(WeatherCategory.RAINY, WeatherCondition.RAIN.getWeatherCategory());
        assertEquals(WeatherCategory.SNOWY, WeatherCondition.SNOW.getWeatherCategory());
        assertEquals(WeatherCategory.STORMY, WeatherCondition.THUNDERSTORM.getWeatherCategory());
        assertEquals(WeatherCategory.FOGGY, WeatherCondition.FOG.getWeatherCategory());
    }

    @ParameterizedTest
    @CsvSource({
        "clear sky, CLEAR",
        "sunny day, SUNNY",
        "partly cloudy, PARTLY_CLOUDY",
        "cloudy with a chance of rain, CLOUDY",
        "overcast skies, OVERCAST",
        "light rain, LIGHT_RAIN",
        "heavy rain, HEAVY_RAIN",
        "thunderstorm, THUNDERSTORM",
        "snow, SNOW",
        "sleet, SLEET",
        "hail, HAIL",
        "windy, WINDY",
        "foggy, FOG",
        "blizzard conditions, BLIZZARD",
        "tornado warning, TORNADO",
        "hurricane conditions, HURRICANE",
        "dust storm, DUST",
        "extreme heat, HEAT_WAVE"
    })
    void fromDescription_validDescriptions_shouldReturnCorrectCondition(String description, String expectedCondition) {
        // Act
        WeatherCondition result = WeatherCondition.fromDescription(description);

        // Assert
        assertEquals(WeatherCondition.valueOf(expectedCondition), result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void fromDescription_nullOrEmpty_shouldReturnUnknown(String description) {
        // Act
        WeatherCondition result = WeatherCondition.fromDescription(description);

        // Assert
        assertEquals(WeatherCondition.UNKNOWN, result);
    }

    @Test
    void fromDescription_categoryMatching_shouldReturnAppropriateCondition() {
        // Act & Assert
        // These descriptions should match based on the WeatherCategory they belong to
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromDescription("rainy weather"));
        assertEquals(WeatherCondition.SNOW, WeatherCondition.fromDescription("snowfall"));
        assertEquals(WeatherCondition.FOG, WeatherCondition.fromDescription("foggy morning"));
    }

    @Test
    void fromDescription_caseInsensitivity_shouldMatchRegardlessOfCase() {
        // Act & Assert
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromDescription("CLEAR"));
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromDescription("clear"));
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromDescription("Clear"));
    }

    @Test
    void fromDescription_unknownDescription_shouldReturnUnknown() {
        // Act & Assert
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromDescription("something completely unrelated"));
    }

    @Test
    void weatherConditionToString_shouldReturnDisplayName() {
        // Act & Assert
        assertEquals("Clear", WeatherCondition.CLEAR.toString());
        assertEquals("Partly Cloudy", WeatherCondition.PARTLY_CLOUDY.toString());
        assertEquals("Rain", WeatherCondition.RAIN.toString());
    }
}
