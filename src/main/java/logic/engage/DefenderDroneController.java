package logic.engage;

import java.util.*;
import java.lang.reflect.*;
import logic.engage.InterceptionPlanner;
		


/**
 * 🚁 DefenderDroneController - Assigns missions to drones and manages real-time swarm behavior.
 * Includes fallback logic, return-to-base, and swarm coordination modes.
 */
public class DefenderDroneController {

    public enum DroneStatus { IDLE, ENGAGING, RETURNING, LOST, CHARGING, PAUSED }

    public static class DroneCommand {
        public String missionId;
        public String assignedTargetId;
        public logic.engage.InterceptionPlanner.InterceptorRole role;
        public DroneStatus status;
        public double batteryLevel;
        public double[] position;
    }

    private final Map<String, DroneCommand> droneMap = new HashMap<>();

    /**
     * Registers a new drone into the system.
     */
    public void registerDrone(String droneId, double[] initialPos) {
        DroneCommand cmd = new DroneCommand();
        cmd.position = initialPos;
        cmd.status = DroneStatus.IDLE;
        cmd.batteryLevel = 100.0;
        droneMap.put(droneId, cmd);
    }

    /**
     * Assigns a mission to a drone with a specific role and target.
     */
    public void assignMission(String droneId, String targetId, String missionId,
    		logic.engage.InterceptionPlanner.InterceptorRole role) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd == null) return;
        cmd.assignedTargetId = targetId;
        cmd.missionId = missionId;
        cmd.role = role;
        cmd.status = DroneStatus.ENGAGING;
        System.out.println("✅ Assigned " + droneId + " to " + role + " target " + targetId);
    }

    /**
     * Updates battery and triggers fallback if needed.
     */
    public void updateBattery(String droneId, double batteryLevel) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd == null) return;
        cmd.batteryLevel = batteryLevel;

        if (batteryLevel < 15.0 && cmd.status == DroneStatus.ENGAGING) {
            cmd.status = DroneStatus.RETURNING;
            System.out.println("🔋 " + droneId + " low battery – returning to base.");
        }
    }

    /**
     * Resets drone to IDLE after mission ends or is aborted.
     */
    public void resetDrone(String droneId) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd == null) return;
        cmd.status = DroneStatus.IDLE;
        cmd.assignedTargetId = null;
        cmd.missionId = null;
        cmd.role =logic.engage.InterceptionPlanner.InterceptorRole.UNKNOWN;
    }

    /**
     * Returns current drone status for monitoring.
     */
    public DroneStatus getDroneStatus(String droneId) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd == null) return DroneStatus.LOST;
        return cmd.status;
    }

    /**
     * Updates drone's known position.
     */
    public void updatePosition(String droneId, double[] pos) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd != null) {
            cmd.position = pos;
        }
    }

    /**
     * Returns all currently registered drones and their commands.
     */
    /**
     * Returns current positions of all drones.
     */
    public Map<String, double[]> getAllDrones() {
        Map<String, double[]> result = new HashMap<>();
        for (Map.Entry<String, DroneCommand> entry : droneMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().position);
        }
        return result;
    }

    /* ──────────────────────── NEW helper – reflection-safe ──────────────────────── */
    /**
     * Attempts to abort or cancel the mission identified by {@code missionId}.
     *
     * ❶ If the controller has a {@code Map<String,?> activeMissions} field we:
     *    • look up the mission<br>
     *    • if found, mark a {@code status}/`setStatus()` field = "CANCELLED"<br>
     *    • remove the entry from the map.
     *
     * ❷ If the controller exposes any field named {@code droneAPI} (or
     *    {@code fleetAPI}, {@code link}, …) that has an {@code abortMission(String)}
     *    method, we invoke it best-effort.
     *
     * The method never throws; it returns {@code true} only if *something*
     * was cancelled/aborted, otherwise {@code false}.
     */
    public boolean cancelMission(String missionId) {
        if (missionId == null || missionId.isBlank()) return false;
        boolean acted = false;

        /* ---- try to remove from an activeMissions Map<String, ?> ---- */
        try {
            Field mapField = findField(this.getClass(),
                                       List.of("activeMissions", "missions", "missionRegistry"));
            if (mapField != null && Map.class.isAssignableFrom(mapField.getType())) {
                mapField.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<String, ?> missions = (Map<String, ?>) mapField.get(this);
                Object mission = (missions != null) ? missions.remove(missionId) : null;
                if (mission != null) {
                    // mark status = CANCELLED if such a field or setter exists
                    setMissionCancelled(mission);
                    acted = true;
                }
            }
        } catch (Exception ignored) { }

        /* ---- try to invoke an abortMission(String) on a drone API-like field ---- */
        try {
            Field apiField = findField(this.getClass(),
                                       List.of("droneAPI", "fleetAPI", "link", "controller"));
            if (apiField != null) {
                apiField.setAccessible(true);
                Object api = apiField.get(this);
                if (api != null) {
                    Method abort = api.getClass().getMethod("abortMission", String.class);
                    abort.invoke(api, missionId);
                    acted = true;
                }
            }
        } catch (NoSuchMethodException ignored) {     // abortMission not present
        } catch (Exception e) {                       // reflection/setAccessible/etc.
            System.err.println("⚠️  cancelMission: abortMission failed – " + e.getMessage());
        }

        return acted;
    }

    /* ───────────────────────────── helpers ───────────────────────────── */
    /** Finds the first declared or inherited field whose name matches one of the hints. */
    private static Field findField(Class<?> cls, List<String> hints) {
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                if (hints.stream().anyMatch(h -> h.equalsIgnoreCase(f.getName())))
                    return f;
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    /** Best-effort: mission.status = "CANCELLED" or mission.setStatus(...). */
    private static void setMissionCancelled(Object mission) {
        if (mission == null) return;
        try {                                   // try setter first
            Method setter = mission.getClass().getMethod("setStatus", String.class);
            setter.invoke(mission, "CANCELLED");
            return;
        } catch (Exception ignored) { }
        try {                                   // fall back to direct field
            Field status = mission.getClass().getDeclaredField("status");
            status.setAccessible(true);
            status.set(mission, "CANCELLED");
        } catch (Exception ignored) { }
    }
    public void returnToBase(String droneId) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd != null && cmd.status == DroneStatus.ENGAGING) {
            cmd.status = DroneStatus.RETURNING;
            System.out.println("🔄 Drone " + droneId + " instructed to return to base.");
        }
        
    }

    public void abortMission(String droneId) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd != null && cmd.status != DroneStatus.IDLE) {
            System.out.println("🛑 Aborting mission for drone " + droneId);
            resetDrone(droneId);
        }
    }

    public void dispatch(String droneId) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd == null) return;

        if (cmd.status == DroneStatus.IDLE && cmd.assignedTargetId != null) {
            cmd.status = DroneStatus.ENGAGING;
            System.out.println("🚁 Dispatching drone " + droneId + " toward target " + cmd.assignedTargetId);
        }
    }
    public Map<String, DroneCommand> getAllDroneCommands() {
        return droneMap;
    }
    public boolean commandDrone(String droneId, String action) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd == null) return false;

        switch (action.toLowerCase()) {
            case "takeoff" -> {
                if (cmd.status == DroneStatus.IDLE) {
                    cmd.status = DroneStatus.ENGAGING;
                    System.out.println("🛫 Drone " + droneId + " taking off.");
                    return true;
                }
            }
            case "pause" -> {
                if (cmd.status == DroneStatus.ENGAGING) {
                    cmd.status = DroneStatus.PAUSED;
                    System.out.println("⏸ Drone " + droneId + " paused.");
                    return true;
                }
            }
            case "resume" -> {
                if (cmd.status == DroneStatus.PAUSED) {
                    cmd.status = DroneStatus.ENGAGING;
                    System.out.println("▶️ Drone " + droneId + " resumed.");
                    return true;
                }
            }
            case "land" -> {
                cmd.status = DroneStatus.RETURNING;
                System.out.println("🛬 Drone " + droneId + " emergency landing.");
                return true;
            }
        }
        return false;
    }

    public Map<String, Double> getDroneBatteryLevels() {
        Map<String, Double> levels = new HashMap<>();
        for (Map.Entry<String, DroneCommand> entry : droneMap.entrySet()) {
            levels.put(entry.getKey(), entry.getValue().batteryLevel);
        }
        return levels;
    }
    public boolean sendReturnCommand(String droneId) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd != null && cmd.status != DroneStatus.RETURNING && cmd.status != DroneStatus.LOST) {
            cmd.status = DroneStatus.RETURNING;
            System.out.println("↩️ Drone " + droneId + " instructed to return.");
            return true;
        }
        return false;
    }
    private static final DefenderDroneController instance = new DefenderDroneController();

    public static DefenderDroneController getInstance() {
        return instance;
    }
    public boolean assignRole(String droneId, String roleName) {
        DroneCommand cmd = droneMap.get(droneId);
        if (cmd == null) return false;

        try {
            InterceptionPlanner.InterceptorRole role =
                InterceptionPlanner.InterceptorRole.valueOf(roleName.toUpperCase());
            cmd.role = role;
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Invalid role name: " + roleName);
            return false;
        }
    }
    public Map<String, String> getDroneRoles() {
        Map<String, String> roles = new HashMap<>();
        for (Map.Entry<String, DroneCommand> entry : droneMap.entrySet()) {
            String id = entry.getKey();
            logic.engage.InterceptionPlanner.InterceptorRole role = entry.getValue().role;
            roles.put(id, (role != null) ? role.name() : "UNKNOWN");
        }
        return roles;
    }


}
