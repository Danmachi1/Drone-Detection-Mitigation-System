package fusion;

import sensors.SensorDataRecord;
import fusion.plugins.FallbackFusionPlugin;
import fusion.plugins.KalmanFilter2D;
import fusion.plugins.UnscentedKalmanFilter2D;
import fusion.plugins.HybridFusionPlugin;
import fusion.plugins.ExtendedKalmanFilter2D;
import fusion.plugins.RuleBasedFusionPlugin;
import fusion.plugins.MultiLayerFusionController;

import java.util.List;

/**
 * 🧠 FusionManager - High-level wrapper coordinating fusion logic.
 * Supports switching, querying, and running various fusion strategies.
 */
public class FusionManager {

    private FusionPlugin plugin;
    private String mode;

    public FusionManager(String mode) {
        this.mode = mode.toLowerCase();
        initializePlugin();
    }

    private void initializePlugin() {
        switch (mode) {
            case "kalman":
                plugin = new KalmanFilter2D();
                break;
            case "ukf":
                plugin = new UnscentedKalmanFilter2D();
                break;
            case "ekf":
                plugin = new ExtendedKalmanFilter2D();
                break;
            case "hybrid":
                plugin = new HybridFusionPlugin();
                break;
            case "rules":
                plugin = new RuleBasedFusionPlugin();
                break;
            case "multi":
                plugin = new MultiLayerFusionController();
                break;
            default:
                System.err.println("⚠️ Unknown mode '" + mode + "' — using fallback.");
                plugin = new FallbackFusionPlugin();
                break;
        }
    }

    public double[] fuse(List<SensorDataRecord> data) {
        if (plugin == null) {
            System.err.println("❌ Fusion plugin is not initialized.");
            return null;
        }
        return plugin.fuse(data);
    }

    public double[] getLastEstimate() {
        if (plugin == null) {
            return null;
        }
        return plugin.getLastEstimate();
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String newMode) {
        this.mode = newMode.toLowerCase();
        initializePlugin();
    }

    public FusionPlugin getPlugin() {
        return plugin;
    }
}
