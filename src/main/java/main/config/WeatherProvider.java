package main.config;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * 🌤️ WeatherProvider – Centralised source of lightweight weather data for the
 * simulation (wind vector, temperature, humidity).  In production this could be
 * wired to a real API; for now we provide a pluggable stub with:
 *
 *   • Static weather set via setters (tests / headless CI)
 *   • Randomised “live” mode that updates every REFRESH_INTERVAL
 *
 * The EnergyEfficientRouting class only needs wind direction + speed, but
 * getter stubs for temperature / humidity are included for future use.
 */
public class WeatherProvider {

    /* Default refresh cadence for randomised weather (ms). */
    private static final long REFRESH_INTERVAL = Duration.ofMinutes(10).toMillis();

    /* Current cached values */
    private double windDirDeg   = 45.0;   // 0-360  (0 = east, CCW positive)
    private double windSpeedMs  = 2.5;    // m/s
    private double temperatureC = 20.0;   // °C
    private double humidityPct  = 40.0;   // %
    private Instant lastUpdate  = Instant.now();

    private final Random rng = new Random();

    /* ── Public Getters ─────────────────────────────────────────── */

    /** Wind direction in degrees (0 = east, 90 = north). */
    public double getWindDirection() {
        maybeRefreshRandom();
        return windDirDeg;
    }

    /** Wind speed in metres / second. */
    public double getWindSpeed() {
        maybeRefreshRandom();
        return windSpeedMs;
    }

    public double getTemperature() { return temperatureC; }
    public double getHumidity()    { return humidityPct; }

    /* ── Manual setters (tests / external feed) ─────────────────── */

    public void setWind(double dirDeg, double speedMs) {
        this.windDirDeg  = dirDeg % 360;
        this.windSpeedMs = Math.max(0, speedMs);
    }

    public void setTemperature(double celsius) { this.temperatureC = celsius; }
    public void setHumidity(double percent)    { this.humidityPct  = percent; }

    /* ── Internal random update (simple noise) ──────────────────── */
    private void maybeRefreshRandom() {
        if (EnvironmentDetector.isSimulation() &&
            Duration.between(lastUpdate, Instant.now()).toMillis() > REFRESH_INTERVAL) {

            windDirDeg   = (windDirDeg + rng.nextGaussian() * 5)  % 360;
            windSpeedMs  = Math.max(0,
                          windSpeedMs + rng.nextGaussian() * 0.5);
            temperatureC = temperatureC + rng.nextGaussian() * 0.2;
            humidityPct  = Math.min(100,
                          Math.max(0, humidityPct + rng.nextGaussian() * 2));

            lastUpdate = Instant.now();
        }
    }
}
