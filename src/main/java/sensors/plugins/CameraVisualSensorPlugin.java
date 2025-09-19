package sensors.plugins;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 📷 CameraVisualSensorPlugin – Dual-mode adapter for an optical/thermal camera
 * that detects aerial objects and provides coarse kinematics.
 *
 * • **Simulation mode** – generates one synthetic detection per poll().
 * • **Real-hardware mode** – stubs in two TODO methods you can replace with the
 *   vendor’s SDK (frame grab + object tracker).
 */
public class CameraVisualSensorPlugin implements SensorPlugin {

    /* ─────────────────────────────────────────────────────────── */
    private boolean simulationMode = true;
    private long    lastUpdate     = 0;

    /* Synthetic target state (used only in simulation) */
    private final Random rng = new Random();
    private double posX = -200, posY = 60, velX = 6, velY = 10;

    /* ───────────── SensorPlugin implementation ──────────────── */

    @Override
    public List<SensorDataRecord> poll() {
        return simulationMode ? makeSimFrame() : readHardwareFrame();
    }

    @Override public boolean isSimulationMode()            { return simulationMode; }
    @Override public void    setSimulationMode(boolean sim) {
        if (simulationMode != sim) {
            simulationMode = sim;
            if (!sim) connectHardware();
        }
    }
    @Override public long    getLastUpdateTime()           { return lastUpdate; }

    /* ───────────────── SIMULATION PATH ─────────────────────── */

    private List<SensorDataRecord> makeSimFrame() {
        List<SensorDataRecord> list = new ArrayList<>();

        /* Advance synthetic target */
        posX += velX + rng.nextGaussian() * 0.4;
        posY += velY + rng.nextGaussian() * 0.4;

        long ts = System.currentTimeMillis();
        lastUpdate = ts;

        list.add(new SensorDataRecord(
                ts,
                posX, posY,
                velX + rng.nextGaussian(),
                velY + rng.nextGaussian(),
                /* altitude  */ 60 + rng.nextGaussian() * 3,
                /* heading   */ Math.atan2(velY, velX),
                /* source    */ "CameraSim",
                /* rfSignatureId */ null,
                /* acousticLevel  */ 0,
                /* visuallyDetected */ true));

        return list;
    }

    /* ───────────────── HARDWARE STUBS ───────────────────────── */

    /** Replace with camera initialisation / object-tracker setup. */
    private void connectHardware() {
        // TODO: vendor SDK init
        System.out.println("⚠️  CameraVisualSensorPlugin: hardware init stub.");
    }

    /** Replace with actual frame read + detection logic. */
    private List<SensorDataRecord> readHardwareFrame() {
        lastUpdate = System.currentTimeMillis();
        // TODO: parse detections → SensorDataRecord list
        return new ArrayList<>();
    }
    @Override
    public List<double[]> getThreatPositions() {
        List<double[]> threats = new ArrayList<>();
        threats.add(new double[]{posX, posY});
        return threats;
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        drones.put("Drone-Cam", new double[]{posX, posY});
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        double futureX = posX, futureY = posY;
        for (int i = 0; i < 5; i++) {
            futureX += velX * 0.5;
            futureY += velY * 0.5;
            path.add(new double[]{futureX, futureY});
        }
        return path;
    }
    private boolean active = true;

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getPluginName() {
        return "CameraVisual";
    }

}
