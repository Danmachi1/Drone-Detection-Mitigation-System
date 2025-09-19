package fusion;

import sensors.SensorDataRecord;

import java.util.List;
import java.util.Map;

/**
 * 🔄 FusionPlugin – contract for every fusion engine (Kalman, UKF, Hybrid, etc.).
 *
 *  State vector convention: {@code [x, y, vx, vy, heading, altitude]}.
 *  Elements that do not apply can be returned as {@code Double.NaN}.
 */
public interface FusionPlugin {

    /* ─────────────────────────── Core API ─────────────────────────── */

    /** Push raw sensor samples into the filter. */
    void ingest(List<SensorDataRecord> records);

    /** @return current best fused estimate (after the latest ingest). */
    double[] getFusedEstimate();

    /** @return human-readable type, e.g. “Kalman”, “UKF”, “Hybrid”. */
    String getFusionType();

    /** Reset internal state so the filter behaves as new. */
    void reset();

    /** Optional per-sensor trust weights (0.0 – 1.0). */
    void setSensorTrust(Map<String, Double> sensorTrustMap);

    /* ───────────── Legacy convenience wrappers (default methods) ───────────── */

    /**
     * Convenience helper kept for legacy code: ingests the batch and returns the
     * updated estimate in one call.
     */
    default double[] fuse(List<SensorDataRecord> records) {
        ingest(records);
        return getFusedEstimate();
    }

    /**
     * Alias kept for older files that still call {@code getLastEstimate()}.
     */
    default double[] getLastEstimate() {
        return getFusedEstimate();
    }
    /* ───────────── Legacy convenience wrappers (default methods) ───────────── */

    // … existing fuse(List<SensorDataRecord>) …

    // … existing getLastEstimate() …

    /* ───────────── NEW overload required by SensorFusionEngine ───────────── */

    /**
     * Variant that lets callers supply per-sensor trust weights in the same
     * call.  Implementations may override; the default just sets the weights
     * then delegates to the one-argument {@code fuse(...)}.
     */
    default double[] fuse(List<SensorDataRecord> records,
                          Map<String, Double> trustWeights) {
        if (trustWeights != null)
            setSensorTrust(trustWeights);
        return fuse(records);           // reuse the existing helper
    }

}
