package logic.zones;

import java.util.HashMap;
import java.util.Map;

/**
 * 🧠 ZoneRiskScorer - Provides a composite risk score for a given location.
 * Used by AI to prioritize response, reroute drones, and assess safety.
 */
public class ZoneRiskScorer {

    public static class RiskReport {
        public final double score;
        public final Map<String, Double> components;

        public RiskReport(double score, Map<String, Double> components) {
            this.score = score;
            this.components = components;
        }

        public String toString() {
            return String.format("Risk: %.2f (%s)", score, components.toString());
        }
    }

    /**
     * Returns a risk report for the given position.
     */
    public static RiskReport evaluateRisk(double[] pos) {
        Map<String, Double> parts = new HashMap<>();

        // Component 1: Proximity to No-Fly Zone
        boolean inNoFly = ZoneManager.isInsideNoFly(pos);
        parts.put("NoFly", inNoFly ? 1.0 : 0.0);

        // Component 2: Proximity to Assets
        boolean nearAsset = ZoneManager.isNearAsset(pos);
        parts.put("AssetProximity", nearAsset ? 0.8 : 0.0);

        // Component 3: Obstacle Presence
        boolean isBlocked = ObstacleManager.isBlocked(pos[0], pos[1], 30); // assume z=30m
        parts.put("Obstacle", isBlocked ? 0.7 : 0.0);

        // Component 4: Bad Weather
        boolean degraded = WeatherAdaptationManager.isSensorImpairmentLikely();
        parts.put("Weather", degraded ? 0.6 : 0.0);

        // Component 5: Elevation Danger
        double elev = TerrainAwarenessManager.getElevation(pos[1], pos[0]);
        double elevationPenalty = elev > 200 ? Math.min((elev - 200) / 300.0, 1.0) : 0.0;
        parts.put("Elevation", elevationPenalty);

        // Weighted sum
        double total = 0;
        for (double val : parts.values()) total += val;

        double normalized = Math.min(total / 5.0, 1.0);
        return new RiskReport(normalized, parts);
    }

    /**
     * Returns only the normalized risk score for fast usage.
     */
    public static double getRiskScore(double[] pos) {
        return evaluateRisk(pos).score;
    }
}
