package logic.recon;

import logic.threat.ThreatClassifier;
import logic.swarm.SwarmManager;

import java.util.*;

/**
 * 🔄 DroneRoleReassignmentLogic - Dynamically reallocates drone roles based on changing battlefield conditions.
 * Supports recon → intercept → backup fallback and emergency coverage fill-ins.
 */
public class DroneRoleReassignmentLogic {

    private final SwarmManager roleManager;
    private final BatteryAwareReturnLogic batteryLogic;

    public DroneRoleReassignmentLogic(SwarmManager roleManager, BatteryAwareReturnLogic batteryLogic) {
        this.roleManager = roleManager;
        this.batteryLogic = batteryLogic;
    }

    /**
     * Evaluates current drone roles and reallocates as needed.
     */
    public void evaluateAndReassign(Map<String, String> droneRoles, Map<String, Double> threatHeatMap) {
        for (Map.Entry<String, String> entry : droneRoles.entrySet()) {
            String id = entry.getKey();
            String role = entry.getValue();
            double battery = batteryLogic.getBatteryLevel(id);

            if (battery < 0.2) {
                roleManager.updateRole(id, "RETURN");
                continue;
            }

            if (role.equals("RECON")) {
                double threat = getThreatLevelNear(id, threatHeatMap);
                if (threat > 0.7) {
                    roleManager.updateRole(id, "INTERCEPT_BACKUP");
                }
            }

            if (role.equals("IDLE") && battery > 0.9) {
                roleManager.updateRole(id, "RECON");
            }
        }
    }

    /**
     * Calculates averaged threat near drone position.
     */
    private double getThreatLevelNear(String droneId, Map<String, Double> heatMap) {
        return heatMap.getOrDefault(droneId, 0.0); // placeholder logic for location-matched heat
    }
}
