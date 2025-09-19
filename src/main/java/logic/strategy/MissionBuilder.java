package logic.strategy;

import logic.strategy.EnergyEfficientRouting;
import logic.strategy.RoleAssignmentEngine;
import logic.threat.Threat;
import logic.swarm.DroneAgent;
import logic.swarm.SwarmManager;

import java.util.List;
import java.util.Map;

/**
 * 🚀 MissionBuilder - Constructs and deploys missions based on threats and coverage needs.
 */
public class MissionBuilder {

    private final EnergyEfficientRouting routing = new EnergyEfficientRouting();
    private final RoleAssignmentEngine roleEngine = new RoleAssignmentEngine();
    private final SwarmManager swarmManager;

    public MissionBuilder(SwarmManager swarmManager) {
        this.swarmManager = swarmManager;
    }

    /**
     * Builds and initiates a mission for a list of threats.
     * @param threats list of Threat objects
     */
    public void buildAndDeploy(List<Threat> threats) {
        // Analyze threats and assign roles
        Map<DroneAgent, String> assignments = roleEngine.assignRoles(threats, swarmManager.getAllAgents());

        // For each assignment, plan route and dispatch
        for (Map.Entry<DroneAgent, String> entry : assignments.entrySet()) {
            DroneAgent agent = entry.getKey();
            Threat threat = threats.stream()
                                   .filter(t -> t.getId().equals(entry.getValue()))
                                   .findFirst().orElse(null);
            if (threat == null) continue;

            // Compute route to threat
            double[] start = agent.getPosition2D();
            double[] dest = threat.getPosition2D();
            List<double[]> route = routing.computeRoute(start[0], start[1], dest[0], dest[1]);

            // Dispatch agent along route
            agent.assignMissionRoute(route);
            System.out.println("🚀 Dispatched " + agent.getId() + " to threat " + threat.getId());
        }
    }
}
