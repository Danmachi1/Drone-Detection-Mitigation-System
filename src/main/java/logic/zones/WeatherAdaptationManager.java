package logic.zones;

import main.config.*;

import java.util.Map;

/**
 * 🌦️ WeatherAdaptationManager - Models local weather conditions for AI behavior and flight logic.
 * Affects drone motion, sensor accuracy, recon strategy, and fusion confidence.
 */
public class WeatherAdaptationManager {

    private static double windSpeedMps = 0.0;
    private static double windDirectionDeg = 0.0;
    private static double visibilityMeters = 5000.0;
    private static double precipitationRate = 0.0; // mm/hr

    /**
     * Loads weather data from the config or uses default values.
     */
    public static void loadWeatherFromConfig() {
    	Map<String, Object> weather = Config.getMapAsObject("weather");

        if (weather != null) {	
            windSpeedMps = ((Number) weather.getOrDefault("windSpeed", 0.0)).doubleValue();
            windDirectionDeg = ((Number) weather.getOrDefault("windDirection", 0.0)).doubleValue();
            visibilityMeters = ((Number) weather.getOrDefault("visibility", 5000.0)).doubleValue();
            precipitationRate = ((Number) weather.getOrDefault("precipitation", 0.0)).doubleValue();
        }
    }

    public static double getWindSpeed() {
        return windSpeedMps;
    }

    public static double getWindDirection() {
        return windDirectionDeg;
    }

    public static double getVisibilityMeters() {
        return visibilityMeters;
    }

    public static double getPrecipitationRate() {
        return precipitationRate;
    }

    /**
     * Returns whether fusion or detection is impaired.
     */
    public static boolean isSensorImpairmentLikely() {
        return visibilityMeters < 1500 || precipitationRate > 3.0;
    }

    /**
     * Indicates risk of drone drift or instability.
     */
    public static boolean isFlightDriftLikely() {
        return windSpeedMps > 7.0;
    }

    /**
     * Basic weather summary string.
     */
    public static String getStatusSummary() {
        return String.format("Wind: %.1fm/s @ %.0f°, Vis: %.0fm, Rain: %.1fmm/h",
                windSpeedMps, windDirectionDeg, visibilityMeters, precipitationRate);
    }
}
