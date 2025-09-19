package sensors.plugins;

import java.util.Map;
import java.util.HashMap;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 🌐 EcholocationSensorPlugin – Dual-mode adapter that simulates (or stubs)
 * an ultrasonic / UWB distance-ranging sensor array.  Provides coarse range
 * and bearing to the strongest reflector (usually a drone body).
 *
 * • **Simulation mode** – emits a single synthetic reflector that moves on a
 *   gentle curve each poll().
 * • **Real mode** – two TODO methods await vendor SDK integration.
 */
public class EcholocationSensorPlugin implements SensorPlugin {

    /* ── Runtime flags & bookkeeping ─────────────────────────── */
    private boolean simulationMode = true;
    private long    lastUpdate     = 0;

    /* ── Synthetic target state (sim-only) ───────────────────── */
    private final Random rng = new Random();
    private double posX = 120, posY = -40, velX = -5, velY = 3;

    /* ───────────────── SensorPlugin API ─────────────────────── */

    @Override
    public List<SensorDataRecord> poll() {
        return simulationMode ? genSimFrame() : readHardwareFrame();
    }

    @Override public boolean isSimulationMode()                { return simulationMode; }
    @Override public void    setSimulationMode(boolean sim) {
        if (simulationMode != sim) {
            simulationMode = sim;
            if (!sim) connectHardware();
        }
    }
    @Override public long    getLastUpdateTime()               { return lastUpdate; }

    /* ──────────────── SIMULATION FRAME ──────────────────────── */

    private List<SensorDataRecord> genSimFrame() {
        List<SensorDataRecord> out = new ArrayList<>();

        posX += velX + rng.nextGaussian() * 0.2;
        posY += velY + rng.nextGaussian() * 0.2;

        long ts  = System.currentTimeMillis();
        lastUpdate = ts;

        double range  = Math.hypot(posX, posY);
        double bearing = Math.atan2(posY, posX);

        out.add(new SensorDataRecord(
                ts,
                posX, posY,
                velX, velY,
                /* altitude (unused) */ 0,
                bearing,
                "EchoSim"));

        return out;
    }

    /* ──────────────── HARDWARE STUBS ────────────────────────── */

    /** TODO: replace with UWB / sonar hardware initialisation. */
    private void connectHardware() {
        System.out.println("⚠️  EcholocationSensorPlugin: hardware init stub.");
    }

    /** TODO: read one frame from device and convert to records. */
    private List<SensorDataRecord> readHardwareFrame() {
        lastUpdate = System.currentTimeMillis();
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
        drones.put("Echo-Target", new double[]{posX, posY});
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
        return "Echolocation";
    }

}
