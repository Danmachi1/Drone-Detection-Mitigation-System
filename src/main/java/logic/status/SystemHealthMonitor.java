package logic.status;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;

/**
 * 🩺 SystemHealthMonitor - Provides live stats on CPU, memory, and internal module health.
 * Used by the dashboard to track real-time performance and diagnostics.
 */
public class SystemHealthMonitor {

    private static final Random rand = new Random();
    private static final Map<String, Boolean> mockModules = new HashMap<>();

    static {
        // Simulated module states (normally you would poll real services or flags)
        mockModules.put("Sensor Input Layer", true);
        mockModules.put("Fusion Engine", true);
        mockModules.put("Threat Logic Engine", true);
        mockModules.put("Swarm Controller", true);
        mockModules.put("UI/UX Core", true);
        mockModules.put("Engagement Manager", true);
        mockModules.put("Simulation Engine", true);
    }

    /**
     * Returns an estimated CPU usage percentage.
     */
    public static int getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        try {
            double load = osBean.getSystemLoadAverage();
            int cores = osBean.getAvailableProcessors();
            return (int) Math.min(100.0, (load / cores) * 100);
        } catch (Exception e) {
            return rand.nextInt(30) + 10; // fallback for unsupported systems
        }
    }

    /**
     * Returns current memory usage as a percentage of max heap.
     */
    public static int getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        return (int) ((used * 100.0) / max);
    }

    /**
     * Returns a map of subsystem health states (mocked or real).
     */
    public static Map<String, Boolean> getModuleHealthStates() {
        // This could be dynamically updated in a real implementation
        return new HashMap<>(mockModules);
    }

    /**
     * Allows external modules to update their health status.
     */
    public static void setModuleStatus(String moduleName, boolean isHealthy) {
        mockModules.put(moduleName, isHealthy);
    }

    /**
     * Returns true if all critical systems are healthy.
     */
    public static boolean isSystemHealthy() {
        return mockModules.values().stream().allMatch(Boolean::booleanValue);
    }
}
