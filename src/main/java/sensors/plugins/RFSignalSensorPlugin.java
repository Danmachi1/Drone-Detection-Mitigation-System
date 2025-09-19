package sensors.plugins;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.*;

/**
 * 📡 RFSignalSensorPlugin – Detects RF emissions (control links, Wi-Fi, SDR)
 * and tags them with a fingerprint string.  Works in two modes:
 *
 * ▸ **Simulation mode (default)** – emits a random “RF-HASH-xxx” signature
 *   that drifts slowly across the map once per poll().
 *
 * ▸ **Hardware mode** – provides stubs for SDR-hardware integration.  Replace
 *   {@link #connectHardware()} and {@link #readHardwareFrame()} with your SDR
 *   library (SoapySDR, GNU-Radio, etc.).
 */
public class RFSignalSensorPlugin implements SensorPlugin {

    /* ── Runtime flags / bookkeeping ─────────────────────────── */
    private boolean simulationMode = true;
    private long    lastUpdate     = 0;

    /* ── Synthetic emitter track (sim-only) ───────────────────── */
    private final Random rng = new Random();
    private String  rfId  = "RF-HASH-" + Integer.toHexString(rng.nextInt(0xFFFF));
    private double  posX  = -60,   posY  = 120;
    private double  velX  = 4.5,   velY  = -5.2;
    private double lastX = posX;
    private double lastY = posY;

    /* ───────────────── SensorPlugin API ─────────────────────── */

    @Override
    public List<SensorDataRecord> poll() {
        return simulationMode ? generateSimFrame()
                              : readHardwareFrame();   // TODO: SDR integration
    }

    @Override public boolean isSimulationMode()             { return simulationMode; }
    @Override public void    setSimulationMode(boolean sim) {
        if (simulationMode != sim) {
            simulationMode = sim;
            if (!sim) connectHardware();
        }
    }
    @Override public long    getLastUpdateTime()            { return lastUpdate; }

    /* ───────────────────────── SIMULATION PATH ──────────────── */

    private List<SensorDataRecord> generateSimFrame() {
        List<SensorDataRecord> out = new ArrayList<>();

        /* Advance RF source ~1 s with mild noise */
        posX += velX + rng.nextGaussian() * 0.3;
        posY += velY + rng.nextGaussian() * 0.3;

        long ts = System.currentTimeMillis();
        lastUpdate = ts;

        out.add(new SensorDataRecord(
                ts,
                posX, posY,
                velX + rng.nextGaussian() * 0.2,
                velY + rng.nextGaussian() * 0.2,
                0,                                   // altitude unknown
                Math.atan2(velY, velX),
                "RFSim", rfId, 0, false));

        return out;
    }

    /* ───────────────────────── HARDWARE STUBS ───────────────── */

    /** Initialise SDR / RF-front-end – replace with real code. */
    private void connectHardware() {
        System.out.println("⚠️  RFSignalSensorPlugin: hardware init stub.");
        // TODO: Open SDR device, set gain / frequency bands, load classifier
    }

    /**
     * Read one capture frame from SDR, extract emitter positions/fingerprints.
     * Replace with DSP pipeline.  For now returns an empty list.
     */
    private List<SensorDataRecord> readHardwareFrame() {
        lastUpdate = System.currentTimeMillis();
        // TODO: IQ processing → fingerprint, DOA, etc.
        return Collections.emptyList();
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
        drones.put(rfId, new double[]{posX, posY});
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        double x = posX;
        double y = posY;
        for (int i = 1; i <= 5; i++) {
            path.add(new double[]{x + i * velX, y + i * velY});
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
        return "RFSignalSensorPlugin";
    }

}
