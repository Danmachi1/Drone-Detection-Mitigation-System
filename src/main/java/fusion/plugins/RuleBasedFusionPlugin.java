package fusion.plugins;

import fusion.FusionPlugin;
import sensors.SensorDataRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 📏 RuleBasedFusionPlugin - Applies simple deterministic rules to estimate position.
 * Useful as a fallback or for redundancy when probabilistic methods fail.
 */
public class RuleBasedFusionPlugin implements FusionPlugin {

    private double[] state = new double[6]; // x, y, vx, vy, heading, alt
    private Map<String, Double> sensorTrustMap = new HashMap<>();

    @Override
    public void ingest(List<SensorDataRecord> records) {
        if (records == null || records.isEmpty()) return;

        double sumX = 0, sumY = 0, sumVx = 0, sumVy = 0, sumHeading = 0, sumAlt = 0;
        double totalWeight = 0;

        for (SensorDataRecord r : records) {
            double trust = 1.0;
            if (sensorTrustMap.containsKey(r.sourceSensor)) {
                trust = sensorTrustMap.get(r.sourceSensor);
            }

            sumX += r.x * trust;
            sumY += r.y * trust;
            sumVx += r.vx * trust;
            sumVy += r.vy * trust;
            sumHeading += r.headingRad * trust;
            sumAlt += r.altitude * trust;
            totalWeight += trust;
        }

        if (totalWeight == 0) return;

        state[0] = sumX / totalWeight;
        state[1] = sumY / totalWeight;
        state[2] = sumVx / totalWeight;
        state[3] = sumVy / totalWeight;
        state[4] = sumHeading / totalWeight;
        state[5] = sumAlt / totalWeight;
    }

    @Override
    public double[] getFusedEstimate() {
        return state.clone();
    }

    @Override
    public String getFusionType() {
        return "RuleBased";
    }

    @Override
    public void reset() {
        state = new double[6];
    }

    @Override
    public void setSensorTrust(Map<String, Double> sensorTrustMap) {
        this.sensorTrustMap = sensorTrustMap;
    }
}
