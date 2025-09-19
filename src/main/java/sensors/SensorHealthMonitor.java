package sensors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 🩺 SensorHealthMonitor – Tracks heartbeat / freshness of every
 * {@link SensorPlugin} the system is running.  Designed to be queried
 * by dashboards and fusion-fallback logic.
 *
 * Rule-of-thumb thresholds (can be tuned per sensor later):
 *   • OK         : last update <   2 s
 *   • WARNING    : 2 s ≤ Δt < 5 s
 *   • CRITICAL   : Δt ≥ 5 s  → triggers fallback
 */
public class SensorHealthMonitor {

    public enum Status { OK, WARNING, CRITICAL }

    /** Internal record (plugin ref + last known status). */
    private static class Entry {
        final SensorPlugin plugin;
        Status status = Status.OK;
        Entry(SensorPlugin p) { plugin = p; }
    }

    private final Map<String, Entry> registry = new HashMap<>();
    private long warnThresholdMs    = 2_000;   // 2 s
    private long criticalThresholdMs = 5_000;  // 5 s

    /* ── Registration ─────────────────────────────────────────── */

    public void register(String id, SensorPlugin plugin) {
        registry.put(id, new Entry(plugin));
    }

    public void unregister(String id) {
        registry.remove(id);
    }

    /* ── Health update loop (call each control-tick) ──────────── */

    public void update() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Entry> e : registry.entrySet()) {
            long delta = now - e.getValue().plugin.getLastUpdateTime();
            Status s   = (delta < warnThresholdMs)         ? Status.OK
                       : (delta < criticalThresholdMs)     ? Status.WARNING
                                                         : Status.CRITICAL;
            e.getValue().status = s;
        }
    }

    /* ── Queries ──────────────────────────────────────────────── */

    public Status getStatus(String id) {
        Entry e = registry.get(id);
        return e != null ? e.status : Status.CRITICAL;
    }

    public Map<String, Status> getAllStatuses() {
        Map<String, Status> map = new HashMap<>();
        registry.forEach((k, v) -> map.put(k, v.status));
        return Collections.unmodifiableMap(map);
    }

    /* ── Threshold tuning (optional) ──────────────────────────── */

    public void setWarnThreshold(long ms)    { warnThresholdMs    = ms; }
    public void setCriticalThreshold(long ms){ criticalThresholdMs = ms; }
}
