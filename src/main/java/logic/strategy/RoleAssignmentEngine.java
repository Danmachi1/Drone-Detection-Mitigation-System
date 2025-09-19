package logic.strategy;

import logic.threat.Threat;
import logic.swarm.DroneAgent;

import java.util.*;

/**
 * 🎯 RoleAssignmentEngine - Determines optimal drone-role assignments for mission tasks.
 */
public class RoleAssignmentEngine {

    /**
     * Assigns roles to drones for a list of threats.
     * @param threats list of Threats
     * @param drones list of available DroneAgents
     * @return map of DroneAgent -> threat ID assignment
     */
    public Map<DroneAgent, String> assignRoles(List<Threat> threats, List<DroneAgent> drones) {
        Map<DroneAgent, String> assignments = new HashMap<>();

        // Simple round-robin assignment based on threat list
        int drCount = drones.size();
        for (int i = 0; i < threats.size() && i < drCount; i++) {
            DroneAgent agent = drones.get(i);
            Threat threat = threats.get(i);
            assignments.put(agent, threat.getId());
        }

        return assignments;
    }
}
