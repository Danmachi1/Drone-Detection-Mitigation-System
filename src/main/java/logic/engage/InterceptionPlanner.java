package logic.engage;

import java.util.*;
import logic.engage.DefenderDroneController.DroneCommand;
import logic.engage.DefenderDroneController.DroneStatus;

/**
 * 🎯 InterceptionPlanner - Computes which interceptor drone should engage which threat,
 * based on range, velocity, and role strategy.
 */
public class InterceptionPlanner {

	public enum InterceptorRole {
	    KAMIKAZE, JAMMER, SNARE, DECOY, SCOUT, BEE_SCOUT, UNKNOWN, OBSERVE,ACOUSTIC_SWEEP,SNIFFER,MAPPER
	}


    private final DefenderDroneController controller;

    public InterceptionPlanner(DefenderDroneController controller) {
        this.controller = controller;
    }

    /**
     * Assigns the best available drone to intercept a threat.
     */
    public void assignInterception(String targetId, InterceptorRole requiredRole) {
        Map<String, DroneCommand> drones = controller.getAllDroneCommands();

        String bestDrone = null;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, DroneCommand> entry : drones.entrySet()) {
            String droneId = entry.getKey();
            DroneCommand cmd = entry.getValue();

            if (cmd.status != DroneStatus.IDLE) continue;
            if (cmd.role != null && cmd.role != InterceptorRole.UNKNOWN && cmd.role != requiredRole) continue;

            double dist = distance(cmd.position, getTargetPosition(targetId));
            if (dist < minDistance) {
                minDistance = dist;
                bestDrone = droneId;
            }
        }

        if (bestDrone != null) {
            controller.assignMission(bestDrone, targetId, "intercept_" + targetId, requiredRole);
            controller.dispatch(bestDrone);
            System.out.println("✅ Interception assigned to drone " + bestDrone + " for target " + targetId + " with role " + requiredRole);
        } else {
            System.out.println("⚠️ No suitable interceptor available for role " + requiredRole);
        }
    }

    /**
     * Returns dummy target position (for now).
     * Replace with real tracking system in production.
     */
    private double[] getTargetPosition(String targetId) {
        // Dummy logic – replace with actual position tracking
        return new double[] { 0.0, 0.0, 0.0 };
    }

    /**
     * Computes 3D Euclidean distance.
     */
    private double distance(double[] a, double[] b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        double dx = b[0] - a[0];
        double dy = b[1] - a[1];
        double dz = b[2] - a[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    /**
     * Selects the best available interceptor by evaluating position and role.
     * @param droneIds List of drone IDs in same order as positions
     * @param dronePositions List of drone positions [x, y, z]
     * @param target Position of the threat/target
     * @param requiredRole Required InterceptorRole
     * @return Index of selected drone or -1 if no suitable drone found
     */
    public int selectInterceptor(List<String> droneIds, List<double[]> dronePositions, double[] target, InterceptorRole requiredRole) {
        if (droneIds == null || dronePositions == null || target == null) return -1;

        Map<String, DroneCommand> allDrones = controller.getAllDroneCommands();
        int bestIndex = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < droneIds.size(); i++) {
            String id = droneIds.get(i);
            double[] pos = dronePositions.get(i);
            DroneCommand cmd = allDrones.get(id);

            if (cmd == null || cmd.status != DroneStatus.IDLE) continue;
            if (cmd.role != null && cmd.role != InterceptorRole.UNKNOWN && cmd.role != requiredRole) continue;

            double dist = distance(pos, target);
            if (dist < minDistance) {
                minDistance = dist;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

}
