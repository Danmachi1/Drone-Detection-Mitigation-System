package logic.threat;

import logic.zones.ZoneManager;

import java.util.List;

/**
 * 🎯 IntentPredictor - Predicts the likely behavior intent of a moving threat using heading, velocity, and past history.
 * Provides early cues for kill-chain acceleration and swarm interception.
 */
public class IntentPredictor {

    public enum IntentType {
        SCOUT, ATTACK, EVADE, UNKNOWN
    }

    private final ThreatHistoryTracker tracker;

    public IntentPredictor(ThreatHistoryTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Predicts the intent type for a given threat based on motion trend.
     */
    public IntentType predictIntent(String id) {
        List<ThreatHistoryTracker.ThreatSnapshot> history = tracker.getHistory(id);
        if (history.size() < 2) return IntentType.UNKNOWN;

        // Get average heading vector
        double[] avgVel = tracker.getAverageVelocity(id);
        double speed = Math.hypot(avgVel[0], avgVel[1]);
        if (speed < 2.0) return IntentType.SCOUT;

        // Get current position
        ThreatHistoryTracker.ThreatSnapshot latest = history.get(history.size() - 1);
        double[] pos = latest.position;

        // Projected point 3s ahead
        double[] projected = new double[]{
            pos[0] + avgVel[0] * 3,
            pos[1] + avgVel[1] * 3
        };

        boolean isHeadingToAsset = ZoneManager.isNearAsset(projected);
        boolean isFleeing = ZoneManager.isInsideNoFly(pos) && !ZoneManager.isInsideNoFly(projected);

        if (isFleeing) return IntentType.EVADE;
        if (isHeadingToAsset) return IntentType.ATTACK;

        return IntentType.SCOUT;
    }
}
