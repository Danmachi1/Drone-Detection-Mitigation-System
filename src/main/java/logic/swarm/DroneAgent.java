package logic.swarm;

import logic.threat.Threat; 
import main.config.Config;
import java.util.ArrayList; 
import fusion.FusionCoordinator;
import sensors.SensorDataRecord;
import logic.strategy.KillChainOrchestrator.AttackMode;    // brings the enum type into scope
import static logic.strategy.KillChainOrchestrator.AttackMode.*;  // pulls in FAN_BLADE, DIRECT_IMPACT constants

import utils.logging.LogManager;                // ← our project-local façade
import org.apache.logging.log4j.Logger;         // ← actual type returned

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🛰️ DroneAgent – lightweight per-drone thread that:
 *   1. pulls the latest sensor batch,
 *   2. asks {@link FusionCoordinator} for the fused state,
 *   3. applies navigation / mission commands.
 *
 * Runs at a fixed rate defined by <code>agent.hz</code> in <code>config.json</code>.
 */
public class DroneAgent implements Runnable {

    /* ───────────────────────────── logger ─────────────────────────── */
    private static final Logger LOG = LogManager.getLogger(DroneAgent.class);

    /* ───────────────────────────── state ───────────────────────────── */
    private final String            droneId;
    private final FusionCoordinator fusion;
    private final AtomicBoolean     running   = new AtomicBoolean(true);

    /** Simple enum to demo the previously broken <code>switch</code> block. */
    public enum CommandType { IDLE, FAN_BLADE, RETURN_HOME }

    private volatile CommandType currentCommand = CommandType.IDLE;

    public DroneAgent(String droneId, FusionCoordinator fusion) {
        this.droneId = droneId;
        this.fusion  = fusion;
    }

    /* ───────────────────── external control hooks ─────────────────── */

    public void stop() { running.set(false); }

    public void setCommand(CommandType cmd) {
        if (cmd != null) currentCommand = cmd;
    }

    /* ─────────────────────────── main loop ────────────────────────── */

    @Override
    public void run() {
        final double hz          = Config.getDouble("agent.hz", 50);
        final long   periodNanos = (long) (1_000_000_000.0 / hz);
        long         nextTick    = System.nanoTime();

        LOG.info("DroneAgent {} started at {} Hz", droneId, hz);

        while (running.get()) {
            try {
                /* 1️⃣  Sensors → Fusion */
                List<SensorDataRecord> batch = getLatestSensorData();
                double[] fused = fusion.fuse(batch);

                /* 2️⃣  Handle mission command */
                handleCommand(fused);

                /* 3️⃣  Sleep until next tick */
                nextTick += periodNanos;
                long sleep = nextTick - System.nanoTime();
                if (sleep > 0) Thread.sleep(sleep / 1_000_000, (int) (sleep % 1_000_000));

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                LOG.warn("DroneAgent {} interrupted – shutting down.", droneId);
                break;

            } catch (Exception ex) {
                LOG.error("Uncaught exception in DroneAgent {}", droneId, ex);
            }
        }

        LOG.info("DroneAgent {} terminated.", droneId);
    }

    /* ───────────────────────── internals ──────────────────────────── */

    /** Placeholder → connect to your sensor layer. */
    private List<SensorDataRecord> getLatestSensorData() {
        return List.of();                // real implementation injected later
    }

    /** Dispatches behaviour based on the current mission command. */
    private void handleCommand(double[] fusedState) {
        switch (currentCommand) {
            case FAN_BLADE -> engageFanBladeAttack(fusedState);

            case RETURN_HOME -> returnToBase();

            case IDLE -> { /* no-op */ }
        }
    }

    /* ---- Stub actions – flesh out when the low-level drone API is ready ---- */

    private void engageFanBladeAttack(double[] state) {
        LOG.debug("Drone {} engaging fan-blade attack at location ({}, {})",
                  droneId, state != null ? state[0] : "?", state != null ? state[1] : "?");
        // TODO: send command via nav link
    }

    private void returnToBase() {
        LOG.debug("Drone {} returning to base", droneId);
        // TODO: RTH command
    }
    /* ─────────────────────── kill-chain support ─────────────────────── */

    /**
     * Called by {@link logic.kill.KillChainOrchestrator} to turn this drone
     * into an interceptor for {@code targetId}.
     */
    public void assignKillMission(String targetId, AttackMode mode) {
        this.currentTargetId = targetId;          // new private field – see below
        switch (mode) {
            case FAN_BLADE      -> setCommand(CommandType.FAN_BLADE);
            case DIRECT_IMPACT  -> setCommand(CommandType.RETURN_HOME); // placeholder
        }
        LOG.info("Drone {} accepted kill mission for {} ({})",
                 droneId, targetId, mode);
    }

    /** @return unique ID used by orchestrators / UI. */
    public String getId() {
        return droneId;
    }

    /** @return true if agent currently has no mission assigned. */
    public boolean isIdle() {
        return currentTargetId == null;
    }

    /* ─────────────────────────── fields ───────────────────────────── */
    private volatile String currentTargetId = null;
    /* ──────────────────── mission-route support (new) ──────────────────── */


    // …

    /** Latest route assigned by MissionBuilder (immutable snapshot). */
    private volatile java.util.List<double[]> currentRoute = java.util.List.of();

    /**
     * Accepts a pre-planned waypoint route from {@code MissionBuilder}.
     *
     * @param waypoints list of {@code double[]{x,y}} pairs in world units.
     */
    public void assignMissionRoute(java.util.List<double[]> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            LOG.warn("Drone {} received an empty mission route", droneId);
            this.currentRoute = java.util.List.of();
            return;
        }
        // Make our own defensive copy
        this.currentRoute = new ArrayList<>(waypoints);
        LOG.info("Drone {} assigned mission route with {} waypoints",
                 droneId, this.currentRoute.size());

        /* TODO: integrate with nav stack – setCommand(CommandType.NAV_ROUTE) etc. */
    }	

    /** @return immutable snapshot of the current waypoint list. */
    public java.util.List<double[]> getCurrentRoute() {
        return java.util.List.copyOf(currentRoute);
    }
    /* ─────────────────────── position helper (new) ─────────────────────── */

    /**
     * @return the drone’s most recent fused X/Y position in world units.
     *         If no estimate is available yet, returns {@code [NaN, NaN]}.
     */
    public double[] getPosition2D() {
        double[] est = fusion.getLastEstimate();          // already thread-safe
        if (est == null || est.length < 2) {
            return new double[]{Double.NaN, Double.NaN};
        }
        return new double[]{est[0], est[1]};
    }
    /* ───────────────────────────── role support ───────────────────────────── */

    /** Swarm-level logical role – matches constants used by SwarmManager. */
    public enum Role {
        BEE_SCOUT,
        BEE_ATTACKER,
        BEE_RELAY,
        BEE_DEFENDER,
        REPAIR_UNIT,     // ← newly added
        FALCON_STRIKE,   // ← newly added
        WOLF_FLANK       // ← newly added
    }
    /* ───────────────────────── busy-state tracking ───────────────────────── */

    private volatile boolean busy = false;

    /** Generic setter used by SwarmManager when assigning / clearing tasks. */
    public void setBusy(boolean busy) { this.busy = busy; }

    /** Optional getter if other modules need it later. */
    public boolean isBusy() { return busy; }

    /* ───────────────────── Fan-blade strike execution (new) ────────────────── */

     // add near top with other imports

    /**
     * Executes a fan-blade strike against the given hostile drone.
     * Currently a stub; real implementation should hand off to nav / weapons.
     */
    public void executeFanBladeStrike(Threat target) {
        if (target == null) {
            LOG.warn("Drone {} received null target for fan-blade strike", droneId);
            return;
        }
        this.currentTargetId = target.getId();        // or however you track IDs
        setCommand(CommandType.FAN_BLADE);
        setBusy(true);

        LOG.info("Drone {} commencing fan-blade strike on threat {}", droneId, target.getId());
    }
    /** Current role (thread-safe). */
    private volatile Role currentRole = Role.BEE_SCOUT;

    /** Change role programmatically (UI, SwarmManager, etc.). */
    public void setRole(Role role) { if (role != null) this.currentRole = role; }

    /** Convenience getter for external queries. */
    public Role getRole() { return currentRole; }

    /* ─────────────────────────── health & status ─────────────────────────── */

    private volatile boolean damaged = false;

    /** Mark the drone as damaged (e.g. low battery, sensor failure). */
    public void markDamaged()  { this.damaged = true;  }

    /** Mark the drone as repaired / healthy. */
    public void clearDamage()  { this.damaged = false; }

    /** @return <code>true</code> if the drone has a damage flag. */
    public boolean isDamaged() { return damaged; }

    /**
     * A drone is considered <i>available</i> when it is:
     *   • not damaged<br>
     *   • not already assigned a target<br>
     *   • not currently returning to base
     */
    public boolean isAvailable() {
        return !damaged &&
               currentTargetId == null &&
               currentCommand == CommandType.IDLE;
    }

    /** @return <code>true</code> if the drone is executing RTH / recharge. */
    public boolean isReturning() {
        return currentCommand == CommandType.RETURN_HOME;
    }

    /* ─────────────────────────── recharge helper ─────────────────────────── */

    /** Sends the drone home for recharge (simple placeholder implementation). */
    public void recharge() {
        LOG.info("Drone {} heading home to recharge", droneId);
        setCommand(CommandType.RETURN_HOME);
    }

    /* ──────────────────────── velocity helpers (new) ──────────────────────── */

    /** Latest {vx, vy} published by this agent (thread-safe snapshot). */
    private volatile double[] velocity2D = new double[]{Double.NaN, Double.NaN};

    /** Sets the current 2-D velocity (called by SwarmBehavioralModel, tests, etc.). */
    public void setVelocity2D(double[] v) {
        if (v != null && v.length >= 2) {
            this.velocity2D = new double[]{v[0], v[1]}; // defensive copy
        }
    }

    /** @return immutable snapshot of the last known 2-D velocity. */
    public double[] getVelocity2D() {
        return new double[]{velocity2D[0], velocity2D[1]};
    }
    private double batteryLevel = 100.0;  // default full charge (you can simulate depletion elsewhere)

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double level) {
        this.batteryLevel = Math.max(0.0, Math.min(100.0, level));  // clamp between 0–100%
    }
    public void updateBattery() {
        batteryLevel -= 0.05; // 0.05% per update tick
        if (batteryLevel < 0) batteryLevel = 0;
    }

}
