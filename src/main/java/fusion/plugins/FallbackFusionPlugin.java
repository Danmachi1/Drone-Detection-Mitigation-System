package fusion.plugins;

import fusion.FusionPlugin;
import sensors.SensorDataRecord;

import java.util.List;
import java.util.Map;

/**
 * 🧩 FallbackFusionPlugin - Passive plugin used when no valid fusion mode is configured.
 * Returns a static default state or null to avoid system crashes.
 */
public class FallbackFusionPlugin implements FusionPlugin {

    private double[] defaultState = new double[] {0, 0, 0, 0, 0, 0};

    @Override
    public void ingest(List<SensorDataRecord> records) {
        // No-op: fallback does not process input
    }

    @Override
    public double[] getFusedEstimate() {
        return defaultState.clone();
    }

    @Override
    public String getFusionType() {
        return "Fallback";
    }

    @Override
    public void reset() {
        defaultState = new double[] {0, 0, 0, 0, 0, 0};
    }

    @Override
    public void setSensorTrust(Map<String, Double> sensorTrustMap) {
        // No-op: fallback does not use trust values
    }
}
