package logic.recon;

import java.util.HashMap;
import java.util.Map;

/**
 * 🔋 BatteryAwareReturnLogic - Monitors each drone's estimated battery level and triggers return/replacement logic.
 * Ensures no drone is lost due to battery exhaustion during missions.
 */
public class BatteryAwareReturnLogic {

    private final Map<String, Double> batteryLevels = new HashMap<>(); // drone ID → [0.0 - 1.0]
    private final double criticalThreshold = 0.15;
    private final double safeReturnMargin = 0.25;

    /**
     * Updates estimated battery after movement.
     */
    public void updateBattery(String droneId, double distanceMeters, double speedMps) {
        double burnRate = 0.00003 * distanceMeters + 0.0001 * speedMps; // simplified consumption
        double current = batteryLevels.getOrDefault(droneId, 1.0);
        double newLevel = Math.max(0.0, current - burnRate);
        batteryLevels.put(droneId, newLevel);
    }

    /**
     * Returns whether a drone should return to base soon.
     */
    public boolean shouldReturnToBase(String droneId) {
        double level = batteryLevels.getOrDefault(droneId, 1.0);
        return level < safeReturnMargin;
    }

    /**
     * Returns whether drone must stop all activity immediately.
     */
    public boolean isCritical(String droneId) {
        return batteryLevels.getOrDefault(droneId, 1.0) < criticalThreshold;
    }

    /**
     * Flags the drone as recharged and ready again.
     */
    public void resetBattery(String droneId) {
        batteryLevels.put(droneId, 1.0);
    }

    /**
     * Get current battery level for UI or planner logic.
     */
    public double getBatteryLevel(String droneId) {
        return batteryLevels.getOrDefault(droneId, 1.0);
    }

    /**
     * Removes all tracked drones.
     */
    public void clearAll() {
        batteryLevels.clear();
    }
}
