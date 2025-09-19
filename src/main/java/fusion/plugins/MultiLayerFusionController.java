package fusion.plugins;

import fusion.FusionPlugin;
import sensors.SensorDataRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 🧬 MultiLayerFusionController - Applies multiple fusion plugins in layers
 * and combines results for enhanced robustness.
 */
public class MultiLayerFusionController implements FusionPlugin {

    private final KalmanFilter2D kalman = new KalmanFilter2D();
    private final UnscentedKalmanFilter2D ukf = new UnscentedKalmanFilter2D();
    private final RuleBasedFusionPlugin rules = new RuleBasedFusionPlugin();

    private double[] fusedState = new double[6];

    @Override
    public void ingest(List<SensorDataRecord> records) {
        if (records == null || records.isEmpty()) return;

        kalman.ingest(records);
        ukf.ingest(records);
        rules.ingest(records);

        double[] kf = kalman.getFusedEstimate();
        double[] uk = ukf.getFusedEstimate();
        double[] rb = rules.getFusedEstimate();

        // Simple equal-weight blending (can be tuned)
        for (int i = 0; i < fusedState.length; i++) {
            fusedState[i] = (kf[i] + uk[i] + rb[i]) / 3.0;
        }
    }

    @Override
    public double[] getFusedEstimate() {
        return fusedState.clone();
    }

    @Override
    public String getFusionType() {
        return "MultiLayer";
    }

    @Override
    public void reset() {
        kalman.reset();
        ukf.reset();
        rules.reset();
        fusedState = new double[6];
    }

    @Override
    public void setSensorTrust(Map<String, Double> sensorTrustMap) {
        kalman.setSensorTrust(sensorTrustMap);
        ukf.setSensorTrust(sensorTrustMap);
        rules.setSensorTrust(sensorTrustMap);
    }
}
