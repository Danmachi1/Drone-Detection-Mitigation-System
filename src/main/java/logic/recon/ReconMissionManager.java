package logic.recon;

import logic.engage.DefenderDroneController;

import java.util.*;

/**
 * 🛰 ReconMissionManager - Handles the launch and coordination of recon sweeps.
 * Supports dynamic recon patterns and dispatches drones based on the selected mode.
 */
public class ReconMissionManager {

    private static final Map<String, ReconTask> activeSweeps = new HashMap<>();
    private static final DefenderDroneController controller = new DefenderDroneController();

    /**
     * Launches a recon sweep for the specified zone, radius, and bio-inspired mode.
     */
    public static boolean launchSweep(String zoneName, int radius, ReconMode mode) {
        if (zoneName == null || zoneName.isBlank() || mode == null) return false;

        String missionId = "RECON_" + zoneName.toUpperCase().replace(" ", "_");

        if (activeSweeps.containsKey(missionId)) {
            System.out.println("⚠️ Recon mission already running for zone: " + zoneName);
            return false;
        }

        ReconTask task = new ReconTask(missionId, zoneName, radius, mode);
        activeSweeps.put(missionId, task);

        System.out.printf("🚀 Recon sweep started: [%s] radius=%dm mode=%s%n", zoneName, radius, mode);

        dispatchReconDrones(missionId, radius, mode);
        return true;
    }

    private static void dispatchReconDrones(String missionId, int radius, ReconMode mode) {
        Map<String, DefenderDroneController.DroneCommand> drones = controller.getAllDroneCommands();

        int dispatched = 0;
        for (Map.Entry<String, DefenderDroneController.DroneCommand> entry : drones.entrySet()) {
            String droneId = entry.getKey();
            DefenderDroneController.DroneCommand cmd = entry.getValue();

            if (cmd.status == DefenderDroneController.DroneStatus.IDLE) {
                controller.assignMission(droneId, "SCAN_" + missionId, missionId, getReconRole(mode));
                controller.dispatch(droneId);
                dispatched++;
                if (dispatched >= getDroneCountForMode(mode)) break;
            }
        }

        System.out.println("✅ " + dispatched + " drones dispatched for recon mode: " + mode);
    }

    private static logic.engage.InterceptionPlanner.InterceptorRole getReconRole(ReconMode mode) {
        return switch (mode) {
            case BEE_ROLES -> logic.engage.InterceptionPlanner.InterceptorRole.SCOUT;
            case FALCON_VISION -> logic.engage.InterceptionPlanner.InterceptorRole.OBSERVE;
            case BAT_ECHO -> logic.engage.InterceptionPlanner.InterceptorRole.ACOUSTIC_SWEEP;
            case SHARK_SCAN -> logic.engage.InterceptionPlanner.InterceptorRole.SNIFFER;
            case ANT_SWARM -> logic.engage.InterceptionPlanner.InterceptorRole.MAPPER;
        };
    }

    private static int getDroneCountForMode(ReconMode mode) {
        return switch (mode) {
            case ANT_SWARM -> 6;
            case BEE_ROLES -> 4;
            case FALCON_VISION -> 2;
            case BAT_ECHO, SHARK_SCAN -> 3;
        };
    }

    /**
     * Internal class tracking a single recon task.
     */
    public static class ReconTask {
        public final String missionId;
        public final String zoneName;
        public final int radius;
        public final ReconMode mode;

        public ReconTask(String missionId, String zoneName, int radius, ReconMode mode) {
            this.missionId = missionId;
            this.zoneName = zoneName;
            this.radius = radius;
            this.mode = mode;
        }
    }
}
