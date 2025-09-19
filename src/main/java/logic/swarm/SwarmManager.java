package logic.swarm;

import logic.threat.Threat;
import utils.alerts.AlertManager;
import logic.swarm.DroneRole;
import logic.mission.MissionProfile;
import java.util.*;
import java.util.List;   
import java.util.ArrayList; 
import java.util.Collections;   // for emptyList()
//// add near other imports


/**
 * 🧠 SwarmManager - Oversees and coordinates swarm behavior,
 * including role distribution, fallback voting, and formation control.
 */
public class SwarmManager {

    private final List<DroneAgent> drones = new ArrayList<>();
    private boolean reconMode = false;
    private final Map<String, DroneAgent> swarmMap = new HashMap<>();


    /**
     * Registers a drone into the swarm.
     */
    public void registerDrone(DroneAgent drone) {
    	 drones.add(drone);
    	    swarmMap.put(drone.getId(), drone);
    }

    /**
     * Assigns roles dynamically based on current swarm health and task.
     */
    public void rebalanceRoles() {
        Map<DroneAgent.Role, Integer> roleCount = new EnumMap<>(DroneAgent.Role.class);
        for (DroneAgent.Role r : DroneAgent.Role.values()) {
            roleCount.put(r, 0);
        }

        for (DroneAgent d : drones) {
            if (d.isDamaged()) continue;
            roleCount.put(d.getRole(), roleCount.get(d.getRole()) + 1);
        }

        // Ensure at least 2 scouts, 1 repair unit, 1 striker
        for (DroneAgent d : drones) {
            if (d.isDamaged()) continue;

            if (roleCount.get(DroneAgent.Role.BEE_SCOUT) < 2) {
                d.setRole(DroneAgent.Role.BEE_SCOUT);
                roleCount.put(DroneAgent.Role.BEE_SCOUT, roleCount.get(DroneAgent.Role.BEE_SCOUT) + 1);
            } else if (roleCount.get(DroneAgent.Role.REPAIR_UNIT) < 1) {
                d.setRole(DroneAgent.Role.REPAIR_UNIT);
                roleCount.put(DroneAgent.Role.REPAIR_UNIT, roleCount.get(DroneAgent.Role.REPAIR_UNIT) + 1);
            }
        }
    }

    /**	
     * Assigns drones to intercept a threat.
     */
    public void assignInterceptors(Threat threat) {
        for (DroneAgent d : drones) {
            if (!d.isAvailable()) continue;

            if (d.getRole() == DroneAgent.Role.FALCON_STRIKE || d.getRole() == DroneAgent.Role.WOLF_FLANK) {
                d.executeFanBladeStrike(threat);
                d.setBusy(true);
                AlertManager.push("🚀 Drone " + d.getId() + " launched on mission " + d.getRole());

                break; // Assign one drone per call
            }
        }
    }

    /**
     * Initiates voting if swarm leader is lost.
     */
    public DroneAgent initiateVotingFallback() {
        for (DroneAgent d : drones) {
            if (d.isAvailable() && !d.isDamaged()) {
                // Elect first available healthy unit as leader
                return d;
            }
        }
        return null;
    }

    /**
     * Enables recon-only mode (non-aggressive swarm behavior).
     */
    public void enableReconMode(boolean enable) {
        this.reconMode = enable;
    }

    public boolean isReconModeEnabled() {
        return reconMode;
    }

    public List<DroneAgent> getSwarm() {
        return drones;
    }

    /**
     * Initializes swarm formation (e.g., circular grid, bee pattern).
     */
    public void initializeFormation() {
        // Example of bee swarm formation trigger
        System.out.println("🛠️ Initializing bee-pattern swarm formation...");
        // Could include path generation + spacing logic
    }

    /**
     * Attempts to recharge all returning drones.
     */
    public void rechargeSwarm() {
        for (DroneAgent d : drones) {
            if (d.isReturning()) {
                d.recharge();
            }
        }
    }
    /** Returns *all* registered drones (busy or idle). */
    public List<DroneAgent> getAllAgents() {
        return new ArrayList<>(drones);
    }

    /** Records a mission assignment – later this could forward to a route planner. */
    public void assignDroneMission(String droneId, String targetId, String role) {
        drones.stream()
              .filter(d -> d.getId().equals(droneId))
              .findFirst()
              .ifPresent(d -> {
                  System.out.println("📌 SwarmManager: " + droneId +
                          " assigned to " + role + " mission on " + targetId);
                  // Future: invoke route/kill-chain logic here
              });
    }
    /** UI-friendly overload that takes a MissionProfile */
    public void assignDroneMission(String droneId, MissionProfile profile) {
        drones.stream()
              .filter(d -> d.getId().equals(droneId))
              .findFirst()
              .ifPresent(d -> {
                  d.setBusy(true);

                  /* 🔹 1.  Build a waypoint list for the chosen zone.
                     Replace this stub with your real planner later. */
                  List<double[]> waypoints = buildStubRoute(profile.getZone());
                  d.assignMissionRoute(waypoints);          // ✅ use the list

                  System.out.println("📌 " + droneId + " launched on mission "
                          + profile.getId() + " (" + profile.getVisionMode() + ")");
              });
    }

    /* ------------------------------------------------------------------ */
    /* Very-simple placeholder – returns a straight-line 2-waypoint route */
    private List<double[]> buildStubRoute(String zoneName) {
        // For now just send the drone 200 m north of origin, then back.
        return List.of(
            new double[]{0,   0,   50},   // x, y, alt
            new double[]{0, 200,  50},
            new double[]{0,   0,   50}
        );
    }


    /* ───────────────────────────── role-management helper ───────────────────────────── */

    /** Internal live map ⇢ <droneId , currentRole>.  Thread-safe for UI refreshes. */
    private final java.util.concurrent.ConcurrentHashMap<String,String> droneRoles =
            new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Updates the role of an active drone and notifies listeners / APIs if present.
     *
     * @param droneId  unique identifier used throughout the system
     * @param newRole  e.g. "BEE_SCOUT", "BEE_ATTACKER", "BEE_RELAY", …
     * @return {@code true} if the role was changed (or newly set), {@code false} if the
     *         drone is unknown or the role is already the same.
     */
    public boolean updateRole(String droneId, String newRole) {
        if (droneId == null || droneId.isBlank() ||
            newRole == null || newRole.isBlank()) {
            return false;
        }

        String prev = droneRoles.put(droneId, newRole);
        boolean changed = !newRole.equals(prev);

        // 🔔 Optional: inform any registered listeners or low-level API about the change
        try {
            java.lang.reflect.Field apiField = this.getClass().getDeclaredField("droneAPI");
            apiField.setAccessible(true);
            Object api = apiField.get(this);
            if (api != null) {
                java.lang.reflect.Method m = api.getClass()
                        .getMethod("assignRole", String.class, String.class);
                m.invoke(api, droneId, newRole);
            }
        } catch (NoSuchFieldException | NoSuchMethodException ignored) {
            /* SwarmManager has no droneAPI / assignRole – perfectly fine in sim mode. */
        } catch (Exception e) {
            System.err.println("⚠️  SwarmManager.updateRole: notify failed – " + e.getMessage());
        }

        return changed;
    }
    /* ─────────── list of idle, healthy agents (needed by MissionPlanner) ─────────── */


    /* ───── list of idle, healthy agents (needed by MissionPlanner) ───── */


    public List<DroneAgent> getAvailableAgents() {

        /* 1️⃣  Try field named 'drones' (Map<String,DroneAgent> or Collection). */
        try {
            java.lang.reflect.Field f = this.getClass().getDeclaredField("drones");
            f.setAccessible(true);
            Object obj = f.get(this);

            if (obj instanceof java.util.Map<?,?> map) {
                return map.values().stream()
                          .filter(da -> da instanceof DroneAgent d && d.isAvailable())
                          .map(da -> (DroneAgent) da)
                          .toList();
            }
            if (obj instanceof java.util.Collection<?> col) {
                return col.stream()
                          .filter(da -> da instanceof DroneAgent d && d.isAvailable())
                          .map(da -> (DroneAgent) da)
                          .toList();
            }
        } catch (NoSuchFieldException ignored) { /* fall through */ }
          catch (Exception            ignored) { /* fall through */ }

        /* 2️⃣  Fallback – scan every field for a Collection<DroneAgent>. */
        List<DroneAgent> result = new ArrayList<>();
        for (java.lang.reflect.Field f : this.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object o = f.get(this);
                if (o instanceof java.util.Collection<?> col) {
                    for (Object e : col) {
                        if (e instanceof DroneAgent d && d.isAvailable()) result.add(d);
                    }
                } else if (o instanceof java.util.Map<?,?> map) {
                    for (Object e : map.values()) {
                        if (e instanceof DroneAgent d && d.isAvailable()) result.add(d);
                    }
                }
            } catch (Exception ignored) { /* continue */ }
        }
        return result;
    }
    public void resetDrone(String droneId) {
        DroneAgent agent = swarmMap.get(droneId);
        if (agent != null) {
            agent.setCommand(DroneAgent.CommandType.IDLE);  // cancel current command
            agent.setBusy(false);                           // mark as available
            agent.setRole(DroneAgent.Role.BEE_SCOUT);       // reset to default role
            agent.assignMissionRoute(List.of());            // clear route
            agent.clearDamage();                            // mark as healthy
            agent.setBatteryLevel(100);                     // recharge to full
            System.out.println("🔄 Drone " + droneId + " has been reset.");
            AlertManager.push("🔄 Drone " + droneId + " was reset to default state.");

        } else {
            System.err.println("⚠️ Unknown drone ID: " + droneId);
        }
    }

    public void cancelMission(String droneId) {
        DroneAgent agent = swarmMap.get(droneId);
        if (agent != null) {
            agent.setCommand(DroneAgent.CommandType.RETURN_HOME);  // send home
            agent.setBusy(false);
            System.out.println("🛑 Drone " + droneId + " mission aborted.");
            AlertManager.push("🛑 Drone " + droneId + " mission CANCELLED (RTB).");

        } else {
            System.err.println("⚠️ Unknown drone ID: " + droneId);
        }
    }
    public DroneAgent getById(String droneId) {
        return swarmMap.get(droneId);
    }
    /** Immediate abort – drone loiters then RTB */
    public void abortMission(String droneId) {
        DroneAgent agent = swarmMap.get(droneId);
        if (agent != null) {
            agent.setCommand(DroneAgent.CommandType.RETURN_HOME);  // if you have a specific enum
            agent.setBusy(false);
            System.out.println("⚠️  Drone " + droneId + " ABORT triggered.");
            AlertManager.push("⚠️ Drone " + droneId + " mission ABORTED.");

        } else {
            System.err.println("⚠️ Unknown drone ID: " + droneId);
        }
    }
    /** All drones, regardless of status (UI helper). */
    public List<DroneAgent> getAllDrones() {
        return new ArrayList<>(drones);
    }

    public void returnToBase(String droneId) {
        DroneAgent agent = swarmMap.get(droneId);
        if (agent != null) {
            agent.setCommand(DroneAgent.CommandType.RETURN_HOME);
            System.out.println("↩️ Drone " + droneId + " instructed to return to base.");
            AlertManager.push("🛑 Drone " + droneId + " instructed to return to base.");

        } else {
            System.err.println("⚠️ Unknown drone ID: " + droneId);
        }
    }


}
