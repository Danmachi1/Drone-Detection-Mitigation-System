package logic.zones;

import logic.swarm.DroneAgent;
import logic.swarm.SwarmManager;

import java.util.HashSet;
import java.util.Set;

/**
 * 🚫 NoFlyEnforcer – Continuously scans swarm positions and flags drones that
 * enter a configured no-fly zone.  Optionally orders an immediate retreat.
 *
 * Usage:
 *   NoFlyEnforcer enforcer = new NoFlyEnforcer(swarmManager);
 *   enforcer.update();   // call once per simulation / control-loop tick
 */
public class NoFlyEnforcer {

    /** Swarm interface (already reviewed in logic.swarm). */
    private final SwarmManager swarmManager;

    /** Currently flagged violators (drone IDs). */
    private final Set<String> flaggedDrones = new HashSet<>();

    public NoFlyEnforcer(SwarmManager swarmManager) {
        this.swarmManager = swarmManager;
    }

    /** Perform one pass over all drones; update violation set and send orders. */
    public void update() {
        final Set<String> stillInside = new HashSet<>();

        for (DroneAgent agent : swarmManager.getSwarm()) {
            double[] pos = agent.getPosition2D();

            if (ZoneManager.isInsideNoFly(pos)) {
                stillInside.add(agent.getId());

                /* NEW : instruct drone to retreat if not already doing so */
                if (!flaggedDrones.contains(agent.getId())) {
                    double[] safe = getSafeRetreatPoint(pos);
                    swarmManager.assignDroneMission(
                            agent.getId(), /*targetId*/ "RTB",
                            /*role*/ "RETURN"
                    );
                    System.out.println("⚠️  " + agent.getId() +
                            " entered NFZ – issuing retreat to " + safe[0] + "," + safe[1]);
                }
            }
        }

        /* Update internal set (remove drones that have left) */
        flaggedDrones.clear();
        flaggedDrones.addAll(stillInside);
    }

    /** Very simple retreat vector (50 m south-west) – expand later with routing. */
    private double[] getSafeRetreatPoint(double[] current) {
        return new double[]{current[0] - 50, current[1] - 50};
    }

    /** Expose violator list to UI / logs. */
    public Set<String> getFlaggedViolators() {
        return new HashSet<>(flaggedDrones);
    }
}
