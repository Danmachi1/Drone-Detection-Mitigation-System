package fusion;

import fusion.FusionPlugin;
import fusion.plugins.HybridFusionPlugin;
import fusion.plugins.KalmanFilter2D;
import fusion.plugins.UnscentedKalmanFilter2D;
import fusion.SensorTrustModel;
import sensors.SensorDataRecord;

import java.util.List;

/**
 * 🔀 SensorFusionEngine - Core fusion module for multi-sensor tracking.
 * Supports Kalman, UKF, and Hybrid (AI + rule) with sensor fallback and trust weighting.
 */
public class SensorFusionEngine {

    public enum FusionMode { KALMAN, UKF, HYBRID }

    private FusionMode currentMode = FusionMode.HYBRID;

    private final KalmanFilter2D kalman = new KalmanFilter2D();
    private final UnscentedKalmanFilter2D ukf = new UnscentedKalmanFilter2D();
    private final HybridFusionPlugin hybrid = new HybridFusionPlugin();
    private final SensorTrustModel trustModel = new SensorTrustModel();

    /**
     * Sets the fusion mode (manual override or adaptive).
     */
    public void setFusionMode(FusionMode mode) {
        this.currentMode = mode;
    }

    /**
     * Fuses sensor inputs to generate the best-estimate position.
     * Automatically handles sensor dropouts and applies trust weighting.
     */
    public double[] fuse(List<SensorDataRecord> inputs) {
        if (inputs.isEmpty()) return null;

        trustModel.updateTrust(inputs);

        switch (currentMode) {
            case KALMAN:
                return kalman.fuse(inputs);
            case UKF:
                return ukf.fuse(inputs);
            case HYBRID:
            default:
                return hybrid.fuse(inputs, trustModel.getTrustWeights(inputs));
        }
    }

    /**
     * Returns the last estimated target position (from selected fusion method).
     */
    public double[] getLastEstimate() {
        switch (currentMode) {
            case KALMAN:
                return kalman.getLastEstimate();
            case UKF:
                return ukf.getLastEstimate();
            case HYBRID:
            default:
                return hybrid.getLastEstimate();
        }
    }

    /**
     * Resets the fusion state (for target reacquisition or hard reset).
     */
    public void reset() {
        kalman.reset();
        ukf.reset();
        hybrid.reset();
    }
}
