package plugins;

/**
 * 🔌 PluginInterface – Generic life-cycle contract for any pluggable module
 * that is **not** a sensor (sensors use the SensorPlugin interface).
 *
 * Typical implementations: analytics add-ons, custom UIs, exporter agents.
 */
public interface PluginInterface {

    /** Called once after the instance is created (allocate resources). */
    void init() throws Exception;

    /** Start / enable the plugin (non-blocking preferred). */
    void start() throws Exception;

    /** Stop / disable the plugin; free any resources. */
    void stop() throws Exception;

    /** Short human-readable name – e.g. “CSV-Exporter”. */
    String getName();

    /** Version string (default “1.0”). */
    default String getVersion() { return "1.0"; }
}
