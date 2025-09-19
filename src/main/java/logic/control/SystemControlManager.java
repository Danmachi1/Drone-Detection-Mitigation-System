package logic.control;

import java.util.HashMap;
import java.util.Map;

/**
 * 🛠 SystemControlManager - Central controller for system-wide configuration,
 * sensor toggling, AI mode management, logging settings, and override lock.
 */
public class SystemControlManager {

    public enum AiMode {
        AI, RULE_BASED, HYBRID
    }

    private static AiMode currentMode = AiMode.HYBRID;
    private static final Map<String, Boolean> sensorStates = new HashMap<>();
    private static boolean overrideLocked = false;
    private static boolean systemActive = true;
    private static String loggingMode = "json";

    static {
        // Default sensor state setup
        sensorStates.put("radar", true);
        sensorStates.put("rf", true);
        sensorStates.put("visual", true);
        sensorStates.put("acoustic", true);
    }

    public static void setAiMode(String mode) {
        try {
            currentMode = AiMode.valueOf(mode.toUpperCase());
            System.out.println("🧠 AI mode set to: " + currentMode);
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Invalid AI mode: " + mode);
        }
    }

    public static AiMode getAiMode() {
        return currentMode;
    }

    public static void toggleSensor(String sensorType, boolean enabled) {
        if (sensorStates.containsKey(sensorType)) {
            sensorStates.put(sensorType, enabled);
            System.out.printf("🔧 Sensor '%s' %s%n", sensorType, enabled ? "enabled" : "disabled");
        } else {
            System.err.println("⚠️ Unknown sensor type: " + sensorType);
        }
    }

    public static boolean isSensorEnabled(String sensorType) {
        return sensorStates.getOrDefault(sensorType, false);
    }

    public static void setOverrideLock(boolean locked) {
        overrideLocked = locked;
        System.out.println("🔐 Override lock " + (locked ? "enabled" : "disabled"));
    }

    public static boolean isOverrideLocked() {
        return overrideLocked;
    }

    public static void setSystemActive(boolean active) {
        systemActive = active;
        System.out.println(active ? "🟢 System activated" : "🔴 System set to passive");
    }

    public static boolean isSystemActive() {
        return systemActive;
    }

    public static void setLoggingMode(String mode) {
        if (mode.equalsIgnoreCase("sql") || mode.equalsIgnoreCase("json")) {
            loggingMode = mode.toLowerCase();
            System.out.println("📦 Logging mode set to: " + loggingMode);
        } else {
            System.err.println("⚠️ Unsupported logging mode: " + mode);
        }
    }

    public static String getLoggingMode() {
        return loggingMode;
    }

    public static Map<String, Boolean> getAllSensorStates() {
        return new HashMap<>(sensorStates);
    }
}
