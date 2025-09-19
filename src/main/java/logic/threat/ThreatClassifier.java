package logic.threat;

import sensors.SensorDataRecord;
import java.util.List;

/**
 * 📦 ThreatClassifier - Lightweight rule-only fallback classifier.
 * Used in minimal environments without full AI or behavior tracking.
 */
public class ThreatClassifier {

    /**
     * Basic rule-only classifier.
     * @param history List of recent sensor records for one object
     * @return Inferred threat type (DRONE, MISSILE, DECOY, UNKNOWN)
     */
    public Threat.ThreatType classifyBasic(List<SensorDataRecord> history) {
        if (history == null || history.isEmpty()) return Threat.ThreatType.UNKNOWN;

        SensorDataRecord latest = history.get(history.size() - 1);
        double speed = Math.hypot(latest.vx, latest.vy);
        double altitude = latest.altitude;

        if (speed > 100 && altitude > 500) {
            return Threat.ThreatType.MISSILE;
        } else if (speed > 15 && altitude > 10) {
            return Threat.ThreatType.DRONE;
        } else if (altitude < 10 && speed < 10) {
            return Threat.ThreatType.DECOY;
        } else {
            return Threat.ThreatType.UNKNOWN;
        }
    }
}
