package logic.strategy;

import logic.swarm.DroneAgent;

import utils.logging.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 🎯 KillChainOrchestrator – chooses which interceptor(s) to launch and
 * assigns them a mission based on threat priority, distance, battery, etc.
 */
public class KillChainOrchestrator {

    /* ─────────────────────────── logger ─────────────────────────── */
    private static final Logger LOG = LogManager.getLogger(KillChainOrchestrator.class);

    /* ─────────────────────────── state ───────────────────────────── */
    private final List<DroneAgent> interceptors = new CopyOnWriteArrayList<>();

    /** Attack styles supported by the orchestrator. */
    public enum AttackMode { FAN_BLADE, DIRECT_IMPACT }

    /* ───────────────────────── public API ────────────────────────── */

    public void registerInterceptor(DroneAgent agent) {
        if (agent != null) interceptors.add(agent);
    }

    public void unregisterInterceptor(DroneAgent agent) {
        interceptors.remove(agent);
    }

    /**
     * Assign the highest-ranked idle interceptor to the target.
     *
     * @param targetId  unique string identifying the hostile drone
     * @param mode      attack style to execute
     */
    public void engage(String targetId, AttackMode mode) {
        DroneAgent chosen = pickBestInterceptor();
        if (chosen == null) {
            LOG.warn("No available interceptors for target {}", targetId);
            return;
        }

        chosen.assignKillMission(targetId, mode);
        LOG.info("Interceptor {} assigned to target {} [{}]", chosen.getId(), targetId, mode);
    }

    /* ───────────────────────── internals ─────────────────────────── */

    private DroneAgent pickBestInterceptor() {
        // Simple heuristic: first idle one wins.  Refine later.
        for (DroneAgent a : interceptors) {
            if (a.isIdle()) return a;
        }
        return null;
    }
}
