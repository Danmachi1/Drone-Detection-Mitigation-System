package logic.strategy;

import logic.swarm.DroneAgent;
import java.util.List;

/**
 * 🐝 SwarmBehaviorModel - Implements swarm algorithms for group movement and formation.
 */
public class SwarmBehaviorModel {

    private double cohesionFactor = 0.5;
    private double separationDistance = 5.0;
    private double alignmentFactor = 0.3;
 // Fields to represent the current tuning parameters
    private double cohesion = 0.5;
    private double alignment = 0.5;
    /**
     * Updates positions of agents based on flocking behavior.
     */
    public void applyFlocking(List<DroneAgent> agents) {
        for (DroneAgent agent : agents) {
            // Cohesion: move toward average position
            double[] avg = averagePosition(agents);
            double[] pos = agent.getPosition2D();
            agent.setVelocity2D(new double[]{
                pos[0] + (avg[0] - pos[0]) * cohesionFactor,
                pos[1] + (avg[1] - pos[1]) * cohesionFactor
            });
            // Separation & alignment omitted for brevity
        }
    }

    private double[] averagePosition(List<DroneAgent> agents) {
        double sumX=0, sumY=0;
        for (DroneAgent a: agents) { sumX+=a.getPosition2D()[0]; sumY+=a.getPosition2D()[1]; }
        return new double[]{sumX/agents.size(), sumY/agents.size()};
    }
 

    // Getters
    public double getCohesion() {
        return cohesion;
    }

    public double getSeparationDistance() {
        return separationDistance;
    }

    public double getAlignment() {
        return alignment;
    }

    // Setters
    public void setCohesion(double cohesion) {
        this.cohesion = cohesion;
    }

    public void setSeparationDistance(double separationDistance) {
        this.separationDistance = separationDistance;
    }

    public void setAlignment(double alignment) {
        this.alignment = alignment;
    }

}
