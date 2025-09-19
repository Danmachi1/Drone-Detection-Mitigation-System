package logic.interception;

import logic.engage.InterceptionPlanner.*;

/**
 * 🛰️ InterceptorDrone - Represents a drone currently engaged or ready for interception missions.
 * Stores position, status, assigned role, and target.
 */
public class InterceptorDrone {

    public enum Status { IDLE, ENGAGING, RETURNING, CHARGING, LOST }

    private final String id;
    private double[] position;
    private double batteryLevel;
    private String targetId;
    private Status status;
    private InterceptorRole role;

    public InterceptorDrone(String id, double[] initialPos) {
        this.id = id;
        this.position = initialPos;
        this.batteryLevel = 100.0;
        this.status = Status.IDLE;
        this.role = InterceptorRole.UNKNOWN;
    }

    public void updatePosition(double[] pos) {
        this.position = pos;
    }

    public void updateBattery(double level) {
        this.batteryLevel = level;
        if (level < 15.0 && status == Status.ENGAGING) {
            status = Status.RETURNING;
        }
    }

    public void assignMission(String targetId, InterceptorRole role) {
        this.targetId = targetId;
        this.role = role;
        this.status = Status.ENGAGING;
    }

    public void completeMission() {
        this.status = Status.IDLE;
        this.targetId = null;
        this.role = InterceptorRole.UNKNOWN;
    }

    // Getters
    public String getId() { return id; }
    public double[] getPosition() { return position; }
    public double getBatteryLevel() { return batteryLevel; }
    public String getTargetId() { return targetId; }
    public Status getStatus() { return status; }
    public InterceptorRole getRole() { return role; }
}
