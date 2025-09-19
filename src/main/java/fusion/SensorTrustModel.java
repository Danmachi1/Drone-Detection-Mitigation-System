package fusion;

import sensors.SensorDataRecord;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 📊 SensorTrustModel – keeps a per-sensor EWMA “trust weight”.
 * Works even if {@link SensorDataRecord} does **not** expose getters
 * for sensor ID / reliability; we discover them at runtime.
 */
public class SensorTrustModel {

    /* ─────────────────────────── Tunables ─────────────────────────── */
    private static final double ALPHA          = 0.10;   // EWMA smoothing
    private static final double DEFAULT_TRUST  = 0.85;   // unseen sensors
    private static final double DEFAULT_RELIAB = 1.00;   // no getter found

    /* ─────────────────────────── State ─────────────────────────────── */
    private final Map<String, Double> trustWeights = new HashMap<>();

    /* ─────────────────────────── Public API ────────────────────────── */

    /** Update using a single record (any object that represents a sample). */
    public void updateTrust(SensorDataRecord record) {
        String  id   = extractSensorId(record);
        double  rel  = extractReliability(record);
        updateTrust(id, rel);
    }

    /** Batch update – used by SensorFusionEngine. */
    public void updateTrust(List<SensorDataRecord> records) {
        if (records == null || records.isEmpty()) return;
        records.forEach(this::updateTrust);
    }

    /** Get immutable slice of weights **only** for sensors appearing in list. */
    public Map<String, Double> getTrustWeights(List<SensorDataRecord> records) {
        if (records == null || records.isEmpty()) return Collections.emptyMap();

        Map<String, Double> slice = new HashMap<>();
        for (SensorDataRecord rec : records) {
            String id = extractSensorId(rec);
            slice.put(id, getTrustWeight(id));
        }
        return Collections.unmodifiableMap(slice);
    }

    /* ─────────────────────────── Internals ─────────────────────────── */

    private void updateTrust(String sensorId, double reliability) {
        reliability = clamp01(reliability);
        double old  = trustWeights.getOrDefault(sensorId, DEFAULT_TRUST);
        double ewma = (1.0 - ALPHA) * old + ALPHA * reliability;
        trustWeights.put(sensorId, ewma);
    }

    private double getTrustWeight(String sensorId) {
        return trustWeights.getOrDefault(sensorId, DEFAULT_TRUST);
    }

    /* ────────────────── Reflection-based extractors ────────────────── */

    /** Finds <code>getSensorId()</code> or <code>getId()</code>; else <code>hashCode()</code>. */
    private static String extractSensorId(SensorDataRecord rec) {
        if (rec == null) return "UNKNOWN-" + System.nanoTime();
        try {                               // 1️⃣ look for getSensorId()
            Method m = rec.getClass().getMethod("getSensorId");
            Object v = m.invoke(rec);
            if (v != null) return v.toString();
        } catch (Exception ignored) { }
        try {                               // 2️⃣ fall back to getId()
            Method m = rec.getClass().getMethod("getId");
            Object v = m.invoke(rec);
            if (v != null) return v.toString();
        } catch (Exception ignored) { }
        return "UNKNOWN-" + rec.hashCode();
    }

    /** Finds <code>getReliability()</code>; else defaults to {@value #DEFAULT_RELIAB}. */
    private static double extractReliability(SensorDataRecord rec) {
        if (rec == null) return DEFAULT_RELIAB;
        try {
            Method m = rec.getClass().getMethod("getReliability");
            Object v = m.invoke(rec);
            if (v instanceof Number num) return num.doubleValue();
        } catch (Exception ignored) { }
        return DEFAULT_RELIAB;
    }

    /* ─────────────────────────── Helpers ───────────────────────────── */
    private static double clamp01(double v) {
        return (v < 0.0) ? 0.0 : (v > 1.0) ? 1.0 : v;
    }
}
