package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.*;

/**
 * 📡 RealRfSensorPlugin – Stub for an SDR/RTL-SDR or similar RF front-end.
 * Until hardware is integrated, it falls back to *silent* mode (no records).
 *
 * Replace `connectHardware()` & `readHardwareFrame()` with your SDR driver.
 */
public class RealRfSensorPlugin implements SensorPlugin {

    private boolean simulation = true;  // default to sim until connect() called
    private long    lastUpdate = 0;

    /* Synthetic fallback (used only while simulation == true) */
    private final Random rng = new Random();
    private String rfId = "RF-LIVE-" + Integer.toHexString(rng.nextInt(0xFFFF));
    private double x = -40,  y = 55, vx = 5.5, vy = -7;

    @Override
    public List<SensorDataRecord> poll() {
        return simulation ? fallbackSimFrame()
                          : readHardwareFrame();   // TODO: integrate SDR API
    }

    @Override public boolean isSimulationMode()          { return simulation; }
    @Override public void    setSimulationMode(boolean s){
        if (simulation != s) {
            simulation = s;
            if (!s) connectHardware();
        }
    }
    @Override public long    getLastUpdateTime()         { return lastUpdate; }

    /* ── fallback synthetic target ───────────────────────────── */
    private List<SensorDataRecord> fallbackSimFrame() {
        List<SensorDataRecord> out = new ArrayList<>();
        x += vx + rng.nextGaussian()*0.25;
        y += vy + rng.nextGaussian()*0.25;

        lastUpdate = System.currentTimeMillis();
        out.add(new SensorDataRecord(
                lastUpdate, x, y,
                vx, vy,
                0, Math.atan2(vy, vx),
                "RealRF-Sim", rfId, 0, false));
        return out;
    }

    /* ── hardware stubs ─────────────────────────────────────── */
    private void connectHardware() {
        System.out.println("⚠️  RealRfSensorPlugin: SDR init stub – replace with driver.");
        // TODO: open SDR device, set frequency, gain, etc.
    }
    private List<SensorDataRecord> readHardwareFrame() {
        lastUpdate = System.currentTimeMillis();
        // TODO: read IQ buffer, run fingerprint classifier, convert to records
        return Collections.emptyList();
    }
    @Override
    public List<double[]> getThreatPositions() {
        List<double[]> threats = new ArrayList<>();
        threats.add(new double[]{x, y});
        return threats;
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        drones.put(rfId, new double[]{x, y});
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        double cx = x, cy = y;
        for (int i = 0; i < 5; i++) {
            cx += vx;
            cy += vy;
            path.add(new double[]{cx, cy});
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
        return "RealRF-Sim";
    }
 
}
