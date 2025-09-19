package logic.threat;

import logic.prediction.PathPredictor;
import logic.prediction.PathPredictor.TrajectoryType;

import java.util.HashMap;
import java.util.Map;

/**
 * 🧠 ThreatDecisionEngine - Determines threat level using rules, AI, or hybrid logic.
 * Outputs classification, intent, and context-aware flags.
 */
public class ThreatDecisionEngine {

    public enum LogicMode { RULE, AI, HYBRID }
    private LogicMode mode = LogicMode.HYBRID;

    // Context cache for audit/logging
    private final Map<String, String> lastReasoning = new HashMap<>();

    // 🆕 Stores latest threat classifications
    private final Map<String, ThreatInfo> lastThreats = new HashMap<>();

    /**
     * 🧱 Inner class holding threat info for UI or override logic.
     */
    public static class ThreatInfo {
        public final String id;
        public final String classification;
        public final double[] position;
        public final double confidence;
        public final String zone;
        public final String type;

        public ThreatInfo(String id, String classification, double[] position, double confidence, String zone, String type) {
            this.id = id;
            this.classification = classification;
            this.position = position;
            this.confidence = confidence;
            this.zone = zone;
            this.type = type;
        }
    }

    /**
     * Analyzes the threat's behavior based on path, velocity, and context.
     * Returns threat classification (e.g. hostile, decoy, unknown).
     */
    public String analyzeThreat(String id, double[] position, double[] velocity, TrajectoryType trajectory, double altitude) {
        String result;

        switch (mode) {
            case RULE -> result = ruleBasedAnalysis(trajectory, velocity, altitude);
            case AI -> result = aiBasedAnalysis(position, velocity, trajectory, altitude);
            case HYBRID -> {
                String aiResult = aiBasedAnalysis(position, velocity, trajectory, altitude);
                String ruleResult = ruleBasedAnalysis(trajectory, velocity, altitude);
                result = ruleResult.equals(aiResult) ? ruleResult : "UNCERTAIN";
            }
            default -> result = "UNKNOWN";
        }

        lastReasoning.put(id, result);

        // Simulate realistic confidence and zone data
        double confidence = switch (result) {
            case "MISSILE" -> 0.97;
            case "DECOY" -> 0.75;
            case "EVASIVE" -> 0.85;
            case "GROUND-THREAT" -> 0.60;
            case "UNCERTAIN" -> 0.50;
            default -> 0.40;
        };

        String zone = determineZone(position);
        String type = result;

        lastThreats.put(id, new ThreatInfo(id, result, position, confidence, zone, type));
        return result;
    }

    private String ruleBasedAnalysis(TrajectoryType traj, double[] vel, double altitude) {
        if (traj == TrajectoryType.CIRCULAR && altitude > 200) return "DECOY";
        if (traj == TrajectoryType.STRAIGHT && vel[0] * vel[0] + vel[1] * vel[1] > 2500) return "MISSILE";
        if (traj == TrajectoryType.ZIGZAG) return "EVASIVE";
        if (altitude < 10) return "GROUND-THREAT";
        return "UNKNOWN";
    }

    private String aiBasedAnalysis(double[] pos, double[] vel, TrajectoryType traj, double alt) {
        // Placeholder for offline AI model integration
        return "UNKNOWN"; // Replace with AI model prediction logic later
    }

    private String determineZone(double[] position) {
        if (position == null || position.length < 2) return "Zone-Unknown";

        double lat = position[0];
        double lon = position[1];

        // Basic example logic (replace with ZoneManager integration if needed)
        if (lat > 50) return "Zone-North";
        if (lat < 30) return "Zone-South";
        if (lon < -50) return "Zone-West";
        return "Zone-Central";
    }

    public void setLogicMode(LogicMode mode) {
        this.mode = mode;
    }

    public String getLogicMode() {
        return mode.name();
    }

    public Map<String, String> getLastReasoning() {
        return lastReasoning;
    }

    public void reset() {
        lastReasoning.clear();
        lastThreats.clear();
    }

    /**
     * Returns a map of all known threats and their most recent classification.
     */
    public Map<String, ThreatInfo> getLastThreats() {
        return lastThreats;
    }
}
