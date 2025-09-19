package fusion;

import main.config.Config;
import fusion.plugins.HybridFusionPlugin;
import fusion.plugins.KalmanFilter2D;
import fusion.plugins.UnscentedKalmanFilter2D;
import sensors.SensorDataRecord;

import java.util.List;

/**
 * 🔁 FusionCoordinator – selects, owns, and (optionally) hot-reloads a single
 * {@link FusionPlugin} instance based on <code>config.json</code>.
 */
public class FusionCoordinator {

    /** The currently active fusion engine. */
    private FusionPlugin plugin;

    /* ─────────────────────────── Construction ─────────────────────────── */

    public FusionCoordinator() {
        initializeFromConfig();
    }

    /**
     * Instantiates the plugin requested by <code>fusion.mode</code>.
     * Accepted values: <b>kalman</b>, <b>ukf</b>, <b>hybrid</b> (default).
     */
    private void initializeFromConfig() {
        String mode = Config.getString("fusion.mode", "hybrid").toLowerCase();
        switch (mode) {	
            case "kalman" -> {
                plugin = new KalmanFilter2D();
                System.out.println("🧮  FusionCoordinator: Using KalmanFilter2D");
            }
            case "ukf" -> {
                plugin = new UnscentedKalmanFilter2D();
                System.out.println("🧡  FusionCoordinator: Using UnscentedKalmanFilter2D");
            }
            default -> {
                plugin = new HybridFusionPlugin();            // “hybrid” or any unknown value
                System.out.println("🤖  FusionCoordinator: Using HybridFusionPlugin");
            }
        }
    }
    

    /* ─────────────────────────── Runtime helpers ─────────────────────────── */

    /** Manually override the current plugin (useful for tests or live tuning). */
    public void setPlugin(FusionPlugin customPlugin) {
        this.plugin = customPlugin;
        System.out.println("✅ FusionCoordinator: Custom fusion plugin injected (" +
                customPlugin.getFusionType() + ")");
    }

    /** Reload plugin selection from the config file at runtime. */
    public void reloadFromConfig() {
        System.out.println("🔄 FusionCoordinator: Reloading fusion plugin from config …");
        initializeFromConfig();
    }

    /* ─────────────────────────── Delegation façade ─────────────────────────── */

    /** Ingests the batch and returns the fused state vector. */
    public double[] fuse(List<SensorDataRecord> inputs) {
        if (plugin == null) {
            System.err.println("❌ FusionCoordinator: Cannot fuse – no plugin initialised.");
            return null;
        }
        return plugin.fuse(inputs);
    }

    /** @return last fused estimate without ingesting new data (or <code>null</code>). */
    public double[] getLastEstimate() {
        if (plugin == null) {
            System.err.println("❌ FusionCoordinator: Cannot get estimate – plugin not initialised.");
            return null;
        }
        return plugin.getLastEstimate();
    }
    
}
