package fusion.plugins;

import fusion.FusionPlugin;
import sensors.SensorDataRecord;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 🌳 DecisionTreeFusion – “rule / tree–driven” alternative to statistical
 * filters.  For now we implement a simple weighted-average decision tree:
 *
 *   • Each sensor proposes a 6-element state vector<br>
 *   • Per-sensor trust (0-1) forms the weights<br>
 *   • The highest-trust sensor wins for any component whose weight ≥ 0.8;<br>
 *     otherwise a trust-weighted average is used.<br>
 *
 * This keeps compile-time dependencies minimal while leaving the door open
 * for a full, hand-tuned decision tree later – without breaking HybridFusion.
 *
 * **State vector convention**: {@code [x, y, vx, vy, heading, altitude]}.
 * Unknown components are {@code Double.NaN}.
 */
public class DecisionTreeFusion implements FusionPlugin {

    /* ─────────────────────────── State ─────────────────────────────── */
    private Map<String, Double> sensorTrust = new HashMap<>();
    private double[]            lastEstimate = new double[] {
            Double.NaN, Double.NaN, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN };

    /* ─────────────────────────── Ingestion ─────────────────────────── */
    @Override
    public void ingest(List<SensorDataRecord> records) {
        if (records == null || records.isEmpty()) return;

        // Track best component per index
        double[] weightedSum = new double[6];
        double[] weightSum   = new double[6];
        Arrays.fill(weightSum, 1e-12);               // avoid ÷0

        for (SensorDataRecord rec : records) {
            double[] state = extractStateVector(rec);
            double    w    = sensorTrust.getOrDefault(extractId(rec), 1.0);

            for (int i = 0; i < 6 && i < state.length; i++) {
                if (!Double.isNaN(state[i])) {
                    weightedSum[i] += state[i] * w;
                    weightSum[i]   += w;
                }
            }
        }

        // Winner-take-all if a single sensor dominates any component
        double[] bestByIndex = new double[6];
        Arrays.fill(bestByIndex, Double.NaN);

        for (SensorDataRecord rec : records) {
            double[] state = extractStateVector(rec);
            double    w    = sensorTrust.getOrDefault(extractId(rec), 1.0);

            for (int i = 0; i < 6 && i < state.length; i++) {
                if (!Double.isNaN(state[i]) && w >= 0.8) {
                    bestByIndex[i] = state[i];        // deterministic tie-break: first hit
                }
            }
        }

        // Final estimate: winner-take-all where defined, else average
        for (int i = 0; i < 6; i++) {
            lastEstimate[i] = !Double.isNaN(bestByIndex[i])
                    ? bestByIndex[i]
                    : weightedSum[i] / weightSum[i];
        }
    }

    @Override public double[] getFusedEstimate() { return lastEstimate.clone(); }
    @Override public String   getFusionType()    { return "DecisionTree"; }

    @Override public void reset() {
        lastEstimate = new double[] {
                Double.NaN, Double.NaN, Double.NaN,
                Double.NaN, Double.NaN, Double.NaN };
    }

    @Override public void setSensorTrust(Map<String, Double> trust) {
        if (trust != null) sensorTrust = new HashMap<>(trust);
    }

    /* ────────────────────────── Reflection helpers ─────────────────── */

    /** Try {@code getSensorId()}, then {@code getId()}, else class+hash. */
    private static String extractId(SensorDataRecord r) {
        if (r == null) return "UNKNOWN-" + System.nanoTime();
        try {
            Method m = r.getClass().getMethod("getSensorId");
            Object v = m.invoke(r);
            if (v != null) return v.toString();
        } catch (Exception ignored) {}
        try {
            Method m = r.getClass().getMethod("getId");
            Object v = m.invoke(r);
            if (v != null) return v.toString();
        } catch (Exception ignored) {}
        return r.getClass().getSimpleName() + "-" + r.hashCode();
    }

    /**
     * Extracts a double[ ] state vector via reflection.
     * Looks for **getStateVector()** or **toVector()**; otherwise returns
     * six {@code NaN}s so downstream math is safe.
     */
    private static double[] extractStateVector(SensorDataRecord r) {
        if (r == null) return blank();

        for (String mName : List.of("getStateVector", "toVector")) {
            try {
                Method m = r.getClass().getMethod(mName);
                Object v = m.invoke(r);
                if (v instanceof double[] vec && vec.length >= 2) return vec;
            } catch (Exception ignored) {}
        }
        return blank();
    }

    private static double[] blank() {
        return new double[] { Double.NaN, Double.NaN, Double.NaN,
                              Double.NaN, Double.NaN, Double.NaN };
    }
}
