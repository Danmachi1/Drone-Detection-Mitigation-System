package logic.threat;

import java.util.*;

/**
 * 📈 ThreatHistoryTracker - Tracks the historical motion and behavior of each threat over time.
 * Used to support trajectory prediction, AI learning, and tactical inference.
 */
public class ThreatHistoryTracker {

    public static class ThreatSnapshot {
        public final long timestamp;
        public final double[] position; // [x, y]
        public final double[] velocity; // [vx, vy]

        public ThreatSnapshot(long time, double[] pos, double[] vel) {
            this.timestamp = time;
            this.position = pos.clone();
            this.velocity = vel.clone();
        }
    }

    // Per-threat history queue
    private final Map<String, Deque<ThreatSnapshot>> history = new HashMap<>();
    private final int maxHistory = 10; // configurable window

    /**
     * Adds a new snapshot of a threat’s state.
     */
    public void addSnapshot(String id, double[] position, double[] velocity) {
        ThreatSnapshot snap = new ThreatSnapshot(System.currentTimeMillis(), position, velocity);
        history.computeIfAbsent(id, k -> new ArrayDeque<>()).add(snap);

        // Trim history to window size
        if (history.get(id).size() > maxHistory) {
            history.get(id).removeFirst();
        }
    }

    /**
     * Returns the full movement history for a threat.
     */
    public List<ThreatSnapshot> getHistory(String id) {
        return history.containsKey(id) ? new ArrayList<>(history.get(id)) : Collections.emptyList();
    }

    /**
     * Returns recent velocity vector average (smoothed motion).
     */
    public double[] getAverageVelocity(String id) {
        Deque<ThreatSnapshot> snaps = history.get(id);
        if (snaps == null || snaps.isEmpty()) return new double[]{0, 0};

        double vx = 0, vy = 0;
        for (ThreatSnapshot s : snaps) {
            vx += s.velocity[0];
            vy += s.velocity[1];
        }
        int n = snaps.size();
        return new double[]{vx / n, vy / n};
    }

    /**
     * Clears all tracked history.
     */
    public void reset() {
        history.clear();
    }
}
