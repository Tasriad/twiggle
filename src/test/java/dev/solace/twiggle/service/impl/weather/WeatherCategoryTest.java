package dev.solace.twiggle.service.impl.weather;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class WeatherCategoryTest {

    @ParameterizedTest
    @CsvSource({
        "clear sky, CLEAR",
        "Clear, CLEAR",
        "sunny, CLEAR",
        "Sunshine, CLEAR",
        "partly cloudy, PARTLY_CLOUDY",
        "Partly Cloudy, PARTLY_CLOUDY",
        "some clouds, CLOUDY",
        "cloudy weather, CLOUDY",
        "Cloudy with scattered clouds, CLOUDY",
        "overcast skies, OVERCAST",
        "rain, RAINY",
        "heavy rain, RAINY",
        "Showers, RAINY",
        "light drizzle, DRIZZLE",
        "drizzling, DRIZZLE",
        "thunderstorm, STORMY",
        "Thunder and lightning, STORMY",
        "snow, SNOWY",
        "heavy snowfall, SNOWY",
        "sleet, SLEET",
        "sleet and freezing rain, SLEET",
        "hail storm, HAIL",
        "hailing, HAIL",
        "foggy conditions, FOGGY",
        "dense fog, FOGGY",
        "misty morning, MISTY",
        "light mist, MISTY",
        "hazy, HAZY",
        "smoke, HAZY",
        "dust storm, HAZY",
        "windy conditions, WINDY",
        "high winds, WINDY",
        "strong gusts, WINDY",
        "unknown condition, PARTLY_CLOUDY" // Default case
    })
    void fromDescription_variousDescriptions_shouldReturnCorrectCategory(
            String description, String expectedCategoryName) {
        // Act
        WeatherCategory result = WeatherCategory.fromDescription(description);

        // Assert
        assertEquals(WeatherCategory.valueOf(expectedCategoryName), result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void fromDescription_nullOrEmpty_shouldReturnDefaultCategory(String description) {
        // Act
        WeatherCategory result = WeatherCategory.fromDescription(description);

        // Assert
        assertEquals(WeatherCategory.PARTLY_CLOUDY, result);
    }

    @Test
    void fromDescription_mixedCaseInput_shouldBeCaseInsensitive() {
        // Act
        WeatherCategory result1 = WeatherCategory.fromDescription("RAINY");
        WeatherCategory result2 = WeatherCategory.fromDescription("rainy");
        WeatherCategory result3 = WeatherCategory.fromDescription("Rainy");

        // Assert
        assertEquals(WeatherCategory.RAINY, result1);
        assertEquals(WeatherCategory.RAINY, result2);
        assertEquals(WeatherCategory.RAINY, result3);
    }

    @Test
    void enumProperties_shouldHaveCorrectValues() {
        // Assert
        assertEquals("Clear", WeatherCategory.CLEAR.getDisplayName());
        assertEquals("Cloudy", WeatherCategory.CLOUDY.getDisplayName());
        assertEquals("Rainy", WeatherCategory.RAINY.getDisplayName());

        assertTrue(WeatherCategory.CLEAR.getDescription().contains("Clear skies"));
        assertTrue(WeatherCategory.FOGGY.getDescription().contains("visibility"));
        assertTrue(WeatherCategory.STORMY.getDescription().contains("thunder"));
    }

    @Test
    void fromDescription_partialMatches_shouldReturnCorrectCategory() {
        // Act & Assert
        assertEquals(WeatherCategory.SNOWY, WeatherCategory.fromDescription("light snow flurries"));
        assertEquals(WeatherCategory.RAINY, WeatherCategory.fromDescription("scattered rain showers"));
        assertEquals(WeatherCategory.STORMY, WeatherCategory.fromDescription("lightning strikes with rain"));
    }
}
