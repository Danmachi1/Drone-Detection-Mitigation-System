package logic.strategy;

import main.config.TerrainDataLoader;
import main.config.WeatherProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * ⚡ EnergyEfficientRouting - Calculates optimized paths with minimal energy use.
 * Uses terrain slope and wind conditions to adjust energy cost dynamically.
 */
public class EnergyEfficientRouting {

    private final TerrainDataLoader terrainLoader = new TerrainDataLoader();
    private final WeatherProvider weatherProvider = new WeatherProvider();
    private final double baseEnergyPerMeter = 1.0;
    private final double slopeEnergyFactor = 10.0;
    private final double windEnergyFactor = 5.0;

    /**
     * Estimates the energy cost between two coordinates.
     * @param startX start X position
     * @param startY start Y position
     * @param endX   end X position
     * @param endY   end Y position
     * @return estimated energy cost (arbitrary units)
     */
    public double estimateEnergyCost(double startX, double startY, double endX, double endY) {
        double dx = endX - startX;
        double dy = endY - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double startElev = terrainLoader.getElevation(startX, startY);
        double endElev = terrainLoader.getElevation(endX, endY);
        double slope = (endElev - startElev) / (distance > 0 ? distance : 1);

        double windDir = weatherProvider.getWindDirection(); // degrees
        double windSpeed = weatherProvider.getWindSpeed();    // m/s

        double routeAngle = Math.toDegrees(Math.atan2(dy, dx));
        double angleDiff = Math.abs(routeAngle - windDir) % 360;
        if (angleDiff > 180) angleDiff = 360 - angleDiff;
        double windFactor = 1 + (windSpeed / 10.0) * Math.cos(Math.toRadians(angleDiff));

        double cost = baseEnergyPerMeter * distance
                + slopeEnergyFactor * Math.abs(slope) * distance
                + windEnergyFactor * (windFactor - 1) * distance;

        return cost;
    }

    /**
     * Computes a simple straight-line segmented route between two points.
     * @param startX start X position
     * @param startY start Y position
     * @param endX   end X position
     * @param endY   end Y position
     * @return List of [x, y] waypoints forming the route
     */
    public List<double[]> computeRoute(double startX, double startY, double endX, double endY) {
        int segments = 10;
        List<double[]> route = new ArrayList<>();
        for (int i = 0; i <= segments; i++) {
            double t = i / (double) segments;
            double x = startX + t * (endX - startX);
            double y = startY + t * (endY - startY);
            route.add(new double[]{x, y});
        }
        return route;
    }
}