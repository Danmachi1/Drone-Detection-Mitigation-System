package logic.prediction;

import java.util.LinkedList;

/**
 * 📊 TrajectoryClassifier - Analyzes historical motion and classifies trajectory type.
 * Detects threat-level patterns like direct approach or evasive maneuvering.
 */
public class TrajectoryClassifier {

    public enum ThreatLevel {
        THREAT_DIRECT, THREAT_EVASIVE, SURVEILLANCE, PASSIVE, UNKNOWN
    }

    private final LinkedList<double[]> history = new LinkedList<>();
    private static final int MAX_HISTORY = 12;

    /**
     * Adds a new position to the motion history.
     */
    public void addPosition(double[] pos) {
        if (pos == null || pos.length != 2) return;
        history.add(pos.clone());
        if (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
    }

    /**
     * Classifies motion threat level based on curvature and consistency.
     */
    public ThreatLevel classify() {
        if (history.size() < 5) return ThreatLevel.UNKNOWN;

        double totalAngleChange = 0.0;
        double[] lastDir = null;

        for (int i = 1; i < history.size(); i++) {
            double[] prev = history.get(i - 1);
            double[] curr = history.get(i);
            double dx = curr[0] - prev[0];
            double dy = curr[1] - prev[1];
            double[] dir = normalize(dx, dy);

            if (lastDir != null) {
                double angle = angleBetween(lastDir, dir);
                totalAngleChange += Math.abs(angle);
            }

            lastDir = dir;
        }

        double avgAngle = totalAngleChange / (history.size() - 1);

        if (avgAngle < 0.15) return ThreatLevel.THREAT_DIRECT;
        if (avgAngle > 0.6) return ThreatLevel.THREAT_EVASIVE;
        if (avgAngle > 0.3) return ThreatLevel.SURVEILLANCE;

        return ThreatLevel.PASSIVE;
    }

    private double angleBetween(double[] a, double[] b) {
        double dot = a[0] * b[0] + a[1] * b[1];
        double magA = Math.sqrt(a[0] * a[0] + a[1] * a[1]);
        double magB = Math.sqrt(b[0] * b[0] + b[1] * b[1]);
        return Math.acos(dot / (magA * magB + 1e-6));
    }

    private double[] normalize(double dx, double dy) {
        double mag = Math.sqrt(dx * dx + dy * dy);
        return new double[]{dx / (mag + 1e-6), dy / (mag + 1e-6)};
    }

    /**
     * Clears historical data.
     */
    public void reset() {
        history.clear();
    }
}
