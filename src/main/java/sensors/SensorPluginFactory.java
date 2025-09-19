package sensors;

import sensors.plugins.*;

/**
 * 🏭 SensorPluginFactory - Creates real or simulated sensor plugin instances
 * based on requested type (Radar, RF, Acoustic, etc.).
 */
public class SensorPluginFactory {

    /**
     * Factory method to return a real or simulated sensor plugin.
     *
     * @param type Sensor type (e.g. "Radar", "Visual", "RF")
     * @param simulate True for simulation mode
     * @return A sensor plugin or null if unsupported
     */
    public static SensorPlugin create(String type, boolean simulate) {
        return create(type, simulate, 0);
    }

    /**
     * Overloaded factory method with instance index (used for uniqueness in simulation).
     *
     * @param type Sensor type
     * @param simulate true if simulating
     * @param instanceIndex unique index for simulated sensors
     * @return sensor plugin instance
     */
    public static SensorPlugin create(String type, boolean simulate, int instanceIndex) {
        return switch (type) {
            case "Radar"    -> simulate ? new SimulatedRadarPlugin(instanceIndex)    : new RealRadarSensorPlugin();
            case "RF"       -> simulate ? new SimulatedRFPlugin()       : new RealRFPlugin();
            case "Visual"   -> simulate ? new SimulatedVisualPlugin()   : new RealVisualPlugin();
            case "Acoustic" -> simulate ? new SimulatedAcousticPlugin() : new RealAcousticPlugin();
            case "Thermal"  -> simulate ? new SimulatedThermalPlugin()  : new RealThermalPlugin();
            case "Sonar"    -> simulate ? new SimulatedSonarPlugin()    : new RealSonarPlugin();
            case "SimulatedRadarPlugin" -> new SimulatedRadarPlugin(instanceIndex);

            default -> {
                System.err.println("⚠️ Unsupported sensor type: " + type);
                yield null;
            }
        };
    }
}
