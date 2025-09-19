package logic.threat;

import logic.swarm.DroneAgent;
import static logic.threat.Threat.ThreatType.*;   // pulls DRONE, MISSILE, DECOY into scope
import logic.swarm.SwarmManager;
import logic.threat.PriorityQueueManager.TrackedThreat;

import java.util.ArrayList;
import java.util.List;

/**
 * 🧭 MissionPlanner – selects idle drones for the highest-priority threats and
 * dispatches them with a simple round-robin policy.
 *
 * Future work: attach EnergyEfficientRouting & RoleAssignmentEngine here.
 */
public class MissionPlanner {

    public enum DroneRole { INTERCEPT, OBSERVE, JAM, DECOY, SHADOW }

    private final SwarmManager swarmManager;
    private final PriorityQueueManager queueManager;

    public MissionPlanner(SwarmManager swarmManager, PriorityQueueManager queueManager) {
        this.swarmManager = swarmManager;
        this.queueManager = queueManager;
    }

    /** Plan missions for the current top-N threats. */
    public void planMissions(int maxAssignments) {

        /* 1 ─ Get top threats and idle drones */
        List<TrackedThreat> topThreats = queueManager.getTopThreats(maxAssignments);
        List<DroneAgent> idle = swarmManager.getAvailableAgents();

        int index = 0;
        for (TrackedThreat tt : topThreats) {
            if (index >= idle.size()) break;

            DroneAgent drone = idle.get(index++);
            DroneRole role   = chooseRole(tt);

            /* 2 ─ Record assignment inside SwarmManager (does not launch route here) */
            swarmManager.assignDroneMission(drone.getId(), tt.getThreat().getId(), role.name());
        }
    }

    /** Very simple role selector – extend later with threat metadata. */
    private DroneRole chooseRole(TrackedThreat tt) {
        return switch (tt.getThreat	().getType()) {
            case MISSILE  -> DroneRole.JAM;
            case DRONE    -> DroneRole.INTERCEPT;
            case DECOY    -> DroneRole.DECOY;
            default       -> DroneRole.OBSERVE;
        };
    }
}
