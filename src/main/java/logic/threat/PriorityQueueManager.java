package logic.threat;

import logic.prediction.MotionBehaviorClassifier.BehaviorType;
import logic.threat.ThreatReasoningEngine.ThreatResult;
import logic.threat.Threat.ThreatType;
import logic.threat.ThreatReasoningEngine.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 📊 PriorityQueueManager – Maintains a live queue of high-priority threats
 * based on threat score.  Used for swarm targeting, visual display, and
 * mission queue logic.
 */
public class PriorityQueueManager {

    /** id → TrackedThreat snapshot */
    private final Map<String, TrackedThreat> threatMap = new ConcurrentHashMap<>();
    private final ThreatReasoningEngine reasoningEngine = new ThreatReasoningEngine();

    /**
     * Immutable snapshot of a single known threat and its reasoning result.
     */
    public static class TrackedThreat {
        public final String        id;
        public final double[]      position;
        public final double[]      velocity;
        public final BehaviorType  behavior;
        public final ThreatResult  threatResult;
        public final long          timestamp;

        public TrackedThreat(String id,
                             double[] pos,
                             double[] vel,
                             BehaviorType behavior,
                             ThreatResult result) {
            this.id           = id;
            this.position     = pos;
            this.velocity     = vel;
            this.behavior     = behavior;
            this.threatResult = result;
            this.timestamp    = System.currentTimeMillis();
        }

        /* ─────────── single accessor needed by MissionPlanner ─────────── */

        /**
         * Builds a lightweight, re		ad-only {@link Threat} view for
         * downstream modules that expect a Threat instance.
         */
        public logic.threat.Threat getThreat() {
        	return new logic.threat.Threat(id, ThreatType.DRONE) {

                /* Basic identity & kinematics – cloned f	or safety */
                @Override public String   getId()        { return id; }
                @Override public double[] getPosition()  { return position.clone(); }
                @Override public double[] getVelocity()  { return velocity.clone(); }

                /* Safe defaults – extend if your Threat interface defines more */
                @Override public boolean    isArmed()     { return false; }
                @Override public ThreatType getType()     { return ThreatType.DRONE; }
                @Override public double     getConfidence(){ return threatResult.threatScore; }

                /* Equality based on unique ID */
                @Override public int hashCode()           { return id.hashCode(); }
                @Override public boolean equals(Object o) {
                    return (o instanceof logic.threat.Threat t) && id.equals(t.getId());
                }
            };
        }
        
    }

    /* ───────────────────────── public API ───────────────────────── */

    /** Feeds a new observation for a target into the system. */
    public void updateThreat(String id,
                             double[] pos,
                             double[] vel,
                             BehaviorType behavior,
                             double fusionConfidence) {

        if (id == null || pos == null || vel == null || behavior == null) return;

        ThreatResult result = reasoningEngine
                .computeThreat(pos, vel, behavior, fusionConfidence);
        TrackedThreat tt = new TrackedThreat(id, pos, vel, behavior, result);
        threatMap.put(id, tt);
    }

    /** @return a list of threats sorted by descending score (max = <code>max</code>) */
    public List<TrackedThreat> getTopThreats(int max) {
        long now = System.currentTimeMillis();

        return threatMap.values().stream()
                .filter(t -> now - t.timestamp < 5_000                       // freshness
                          && t.threatResult.threatScore > 0.1)               // relevance
                .sorted((a, b) -> Double.compare(
                        b.threatResult.threatScore, a.threatResult.threatScore))
                .limit(max)
                .toList();
    }

    /** Highest-scoring threat if one exists. */
    public Optional<TrackedThreat> getTopThreat() {
        return getTopThreats(1).stream().findFirst();
    }

    /** Removes everything (mainly for tests / sim resets). */
    public void clear() {
        threatMap.clear();
    }

    /** Removes threats older than the given timeout (ms). */
    public void cleanup(long maxAgeMs) {
        long now = System.currentTimeMillis();
        threatMap.entrySet()
                 .removeIf(e -> now - e.getValue().timestamp > maxAgeMs);
    }
}
