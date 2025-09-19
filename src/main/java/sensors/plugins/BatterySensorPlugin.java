package sensors.plugins;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 🔋 BatterySensorPlugin – Emits simulated battery-voltage records once per
 * poll().  Real hardware would read an ADC line; this version just drifts
 * downward with small Gaussian noise.
 *
 * The record encodes battery voltage in the altitude field for now (so we
 * don’t add more fields to SensorDataRecord).  Downstream logic can map it
 * elsewhere later.
 */
public class BatterySensorPlugin implements SensorPlugin {

    private final Random rng = new Random();
    private boolean simulation = true;
    private long    lastUpdate = 0;

    /* Synthetic battery model */
    private double voltage = 16.8;   // fresh 4-cell Li-ion pack
    private final double dischargeRate = 0.005;  // V per poll

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> list = new ArrayList<>();
        if (!simulation) return list;

        /* Simple linear discharge + noise */
        voltage = Math.max(14.0, voltage - dischargeRate + rng.nextGaussian() * 0.001);

        long ts = System.currentTimeMillis();
        lastUpdate = ts;

        SensorDataRecord rec = new SensorDataRecord(
                ts,
                /* x,y */ 0, 0,
                /* vx,vy */ 0, 0,
                /* altitude := battery voltage proxy */ voltage,
                /* heading */ 0,
                "SimBattery");

        list.add(rec);
        return list;
    }

    /* ── SensorPlugin boiler-plate ────────────────────────────── */
    @Override public boolean isSimulationMode()        { return simulation; }
    @Override public void    setSimulationMode(boolean s) { simulation = s; }
    @Override public long    getLastUpdateTime()       { return lastUpdate; }
    @Override
    public List<double[]> getThreatPositions() {
        // Battery sensor does not detect threats
        return new ArrayList<>();
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        // If available, simulate drone position with constant value
        Map<String, double[]> dronePositions = new HashMap<>();
        dronePositions.put("Drone-1", new double[]{0.0, 0.0});
        return dronePositions;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        // Battery sensor does not provide path prediction
        return new ArrayList<>();
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
        return "Battery";
    }

}
