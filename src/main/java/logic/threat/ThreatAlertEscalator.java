package logic.threat;

import logic.engage.EngagementManager;
import logic.zones.ZoneRiskScorer;
import utils.alerts.AlertManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 🚨 ThreatAlertEscalator - Evaluates and tracks alert escalation levels for each detected object.
 * Escalation drives UI alerts, kill-chain engagement, and swarm activation.
 */
public class ThreatAlertEscalator {

    public enum ThreatLevel {
        OBSERVED, WARNING, THREAT, CRITICAL
    }

    private final Map<String, ThreatLevel> threatStates = new HashMap<>();
    private final Map<String, double[]> threatPositions = new HashMap<>();
    private final EngagementManager engagementManager;

    public ThreatAlertEscalator(EngagementManager engagementManager) {
        this.engagementManager = engagementManager;
    }

    /**
     * Updates threat data and possibly escalates alert level.
     */
    public void updateThreatState(String objectId, double[] pos, double[] velocity, String type) {
        ThreatLevel prev = threatStates.getOrDefault(objectId, ThreatLevel.OBSERVED);
        double speed = Math.sqrt(velocity[0]*velocity[0] + velocity[1]*velocity[1] + velocity[2]*velocity[2]);

        // Example logic (replace with better scoring later)
        ThreatLevel newLevel = prev;
        if (speed > 30) {
            newLevel = ThreatLevel.CRITICAL;
            AlertManager.push("🔺 Threat " + objectId + " escalated to " + newLevel);

        } else if (speed > 15) {
            newLevel = ThreatLevel.THREAT;
            AlertManager.push("🔺 Threat " + objectId + " escalated to " + newLevel);

        } else if (speed > 5) {
            newLevel = ThreatLevel.WARNING;
            AlertManager.push("🔺 Threat " + objectId + " lowered to " + newLevel);

        }

        // Store updated threat level and position
        threatStates.put(objectId, newLevel);
        threatPositions.put(objectId, pos);

        if (newLevel != prev) {
            engagementManager.onThreatEscalated(objectId, prev, newLevel);
        }
    }

    /**
     * Gets the most recent position of a threat.
     */
    public double[] getThreatPosition(String threatId) {
        return threatPositions.getOrDefault(threatId, null);
    }

    /**
     * Gets the current threat level.
     */
    public ThreatLevel getThreatLevel(String threatId) {
        return threatStates.getOrDefault(threatId, ThreatLevel.OBSERVED);
    }
}
