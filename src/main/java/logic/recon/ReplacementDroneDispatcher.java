package logic.recon;

import java.util.*;

/**
 * 🤖 ReplacementDroneDispatcher - Automatically dispatches a backup drone when one returns or fails.
 * Ensures continuity in mission coverage.
 */
public class ReplacementDroneDispatcher {

    private final Set<String> standbyPool = new HashSet<>(); // drone IDs ready for dispatch
    private final Set<String> inMission = new HashSet<>();
    private final BatteryAwareReturnLogic batteryLogic;

    public ReplacementDroneDispatcher(BatteryAwareReturnLogic batteryLogic) {
        this.batteryLogic = batteryLogic;
    }

    /**
     * Registers a drone as ready for future dispatch.
     */
    public void registerStandbyDrone(String id) {
        standbyPool.add(id);
    }

    /**
     * Flags drone as dispatched into mission.
     */
    public void assignToMission(String id) {
        standbyPool.remove(id);
        inMission.add(id);
    }

    /**
     * Called when a drone returns to base successfully.
     */
    public void markDroneReturned(String id) {
        inMission.remove(id);
        batteryLogic.resetBattery(id);
        standbyPool.add(id);
    }

    /**
     * Selects a suitable drone to replace a failed/returning one.
     */
    public Optional<String> dispatchReplacement(String failedDroneId) {
        return standbyPool.stream()
            .filter(d -> batteryLogic.getBatteryLevel(d) >= 0.9)
            .findFirst()
            .map(id -> {
                assignToMission(id);
                System.out.println("🟢 Replacement dispatched: " + id + " for " + failedDroneId);
                return id;
            });
    }

    /**
     * Resets all tracking (e.g., new mission).
     */
    public void reset() {
        standbyPool.clear();
        inMission.clear();
    }
}
