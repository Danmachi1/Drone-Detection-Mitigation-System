package sensors;

import java.util.*;

import sensors.plugins.SimulatedRadarPlugin;  // ✅ Corrected full package path

/**
 * 🧠 MultiSensorManager – Central handler that manages all sensor plugins
 * (real or simulated).  Provides unified polling, shutdown, and reload logic.
 */
public class MultiSensorManager {

    private final Map<String, SensorPlugin> activeSensors = new HashMap<>();
    private boolean useSimulation = false;

    /* ───────────────── INITIALISATION ─────────────────────────── */

    /** Load a default sensor set; call at system boot. */
    public void initialize(boolean simulate) {
        this.useSimulation = simulate;

        // Default radar from factory
        SensorPlugin radar = SensorPluginFactory.create("Radar", simulate);
        if (radar != null) activeSensors.put("Radar", radar);

        // Multiple uniquely indexed radar instances
        for (int i = 0; i < 5; i++) {
            SensorPlugin p = new SimulatedRadarPlugin(i); // ✅ using the ID constructor
            activeSensors.put("Radar-Sim" + i, p);
        }
    }

    /* ───────────────── POLLING ─────────────────────────────────── */

    /** Poll every active plugin once; returns a combined list of new records. */
    public List<SensorDataRecord> pollAll() {
        List<SensorDataRecord> all = new ArrayList<>();
        for (SensorPlugin plugin : activeSensors.values()) {
            all.addAll(plugin.poll());
        }

        System.out.println("== Aggregated Sensor Detections ==");
        for (SensorPlugin p : activeSensors.values()) {
            List<double[]> t = p.getThreatPositions();
            System.out.println(p.getPluginName() + " Threats: " + t.size());
        }

        return all;
    }

    /* ───────────────── RELOAD / SHUTDOWN ───────────────────────── */

    /** Reload all sensor plugins (e.g., after config change). */
    public void reloadSensors(boolean simulate) {
        shutdown();
        activeSensors.clear();
        initialize(simulate);
    }

    /** Stop all sensors cleanly. */
    public void shutdown() {
        activeSensors.clear();  // In real-world: close ports
    }

    /* ───────────────── HELPERS ─────────────────────────────────── */

    public boolean isSimMode() {
        return useSimulation;
    }

    public Collection<SensorPlugin> getActivePlugins() {
        return activeSensors.values();
    }

    /* ───────────────── UI SUPPORT METHODS ───────────────────────── */

    public List<double[]> getThreatPositions() {
        List<double[]> positions = new ArrayList<>();
        for (SensorPlugin plugin : activeSensors.values()) {
            positions.addAll(plugin.getThreatPositions());
        }
        return positions;
    }

    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        for (SensorPlugin plugin : activeSensors.values()) {
            drones.putAll(plugin.getDronePositions());
        }
        return drones;
    }

    public List<double[]> getPredictedThreatPaths() {
        List<double[]> predicted = new ArrayList<>();
        for (SensorPlugin plugin : activeSensors.values()) {
            predicted.addAll(plugin.getPredictedPaths());
        }
        return predicted;
    }

    public List<SensorPlugin> getAllPlugins() {
        return new ArrayList<>(activeSensors.values());
    }

    public void enablePlugin(String name) {
        SensorPlugin plugin = SensorPluginFactory.create(name, useSimulation);
        if (plugin != null) {
            activeSensors.put(name, plugin);
            plugin.activate();
        }
    }

    public void disablePlugin(String name) {
        SensorPlugin plugin = activeSensors.remove(name);
        if (plugin != null) {
            plugin.deactivate();
        }
    }
    /** Returns all plugin keys (for unique UI references). */
    public Set<String> getPluginKeys() {
        return activeSensors.keySet();
    }

    /** Returns a plugin by unique key (used in toggles). */
    public SensorPlugin getPluginByKey(String key) {
        return activeSensors.get(key);
    }

}
