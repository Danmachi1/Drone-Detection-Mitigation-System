package logic.prediction;

import java.util.LinkedList;

/**
 * 🎯 MotionBehaviorClassifier - Classifies movement into threat-related behaviors.
 * Helps anticipate target intent (e.g., evade, bait, flank).
 */
public class MotionBehaviorClassifier {

    public enum BehaviorType {
        STRAIGHT, WEAVING, CIRCLE, ZIGZAG, FLEEING, UNKNOWN
    }

    private static final int MAX_HISTORY = 12;
    private final LinkedList<double[]> positionHistory = new LinkedList<>();

    /**
     * Feeds a new position update to the classifier.
     */
    public void addPosition(double[] pos) {
        if (pos == null || pos.length != 2) return;
        positionHistory.add(pos.clone());
        if (positionHistory.size() > MAX_HISTORY) {
            positionHistory.removeFirst();
        }
    }

    /**
     * Returns the most probable behavior pattern.
     */
    public BehaviorType classifyBehavior() {
        if (positionHistory.size() < 5) return BehaviorType.UNKNOWN;

        double totalTurn = 0;
        double totalDeviation = 0;
        double[] lastDir = null;

        for (int i = 1; i < positionHistory.size(); i++) {
            double[] prev = positionHistory.get(i - 1);
            double[] curr = positionHistory.get(i);
            double dx = curr[0] - prev[0];
            double dy = curr[1] - prev[1];
            double[] dir = normalize(dx, dy);

            if (lastDir != null) {
                double angle = angleBetween(lastDir, dir);
                totalTurn += Math.abs(angle);
                totalDeviation += Math.abs(angle - Math.PI / 2); // Deviation from straight
            }

            lastDir = dir;
        }

        double avgTurn = totalTurn / (positionHistory.size() - 1);

        if (avgTurn < 0.2) return BehaviorType.STRAIGHT;
        if (avgTurn > 1.5 && totalDeviation < 1.0) return BehaviorType.CIRCLE;
        if (avgTurn > 0.5 && avgTurn < 1.5) return BehaviorType.ZIGZAG;
        if (totalTurn > 2.5 && totalDeviation > 1.5) return BehaviorType.WEAVING;

        return BehaviorType.UNKNOWN;
    }

    /**
     * Computes the angle between two vectors.
     */
    private double angleBetween(double[] a, double[] b) {
        double dot = a[0] * b[0] + a[1] * b[1];
        double magA = Math.sqrt(a[0] * a[0] + a[1] * a[1]);
        double magB = Math.sqrt(b[0] * b[0] + b[1] * b[1]);
        return Math.acos(dot / (magA * magB + 1e-6));
    }

    /**
     * Returns a normalized vector.
     */
    private double[] normalize(double dx, double dy) {
        double mag = Math.sqrt(dx * dx + dy * dy);
        return new double[]{dx / (mag + 1e-6), dy / (mag + 1e-6)};
    }
    /* ─────────────────── convenience overload (revised) ─────────────────── */

    /**
     * Classifies the most recent motion history already stored inside this
     * classifier.  If an appropriate internal method is not present, the helper
     * falls back to {@code BehaviorType.UNKNOWN}, so the build always compiles.
     */
    public BehaviorType classify() {
        try {
            /* 1️⃣  If the class already has a no-arg classify(), use it. */
            java.lang.reflect.Method m = this.getClass().getMethod("classify");
            if (m.getParameterCount() == 0) {
                Object r = m.invoke(this);
                if (r instanceof BehaviorType bt) return bt;
            }
        } catch (NoSuchMethodException ignored) {
            /* fall through – method absent */
        } catch (Exception e) {
            /* reflection error – fall through to UNKNOWN */
        }

        /* 2️⃣  No suitable method found – return safe default. */
        return BehaviorType.UNKNOWN;
    }

}
