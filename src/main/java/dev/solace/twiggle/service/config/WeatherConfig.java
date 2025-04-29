package dev.solace.twiggle.service.config;

import dev.solace.twiggle.service.impl.weather.HazardEngine;
import dev.solace.twiggle.service.impl.weather.WeatherApiClient;
import dev.solace.twiggle.service.impl.weather.WeatherFacade;
import dev.solace.twiggle.service.impl.weather.WeatherMapper;
import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for weather-related beans.
 */
@Configuration
public class WeatherConfig {

    /**
     * Configured RestTemplate for weather API calls.
     * Sets reasonable timeouts to handle API delays.
     */
    @Bean
    public RestTemplate weatherRestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Creates the weather facade for centralized weather management.
     * This demonstrates how to manually wire beans if needed, but Spring
     * would normally handle this injection automatically.
     */
    @Bean
    public WeatherFacade weatherFacade(
            WeatherApiClient weatherApiClient, WeatherMapper weatherMapper, HazardEngine hazardEngine) {
        return new WeatherFacade(weatherApiClient, weatherMapper, hazardEngine);
    }
}
