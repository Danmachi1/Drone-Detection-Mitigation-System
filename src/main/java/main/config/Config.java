package main.config;

import logic.zones.Zone;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import main.config.TerrainDataLoader;
import ui.SystemLogPanel;

/**
 * 🛠️ Config – Lightweight singleton-style utility that stores key/value settings
 * loaded at boot.  Provides simple getters used across the code-base.
 *
 * Supported getters currently in use:
 *   • getString(key)
 *   • getMap(key)
 *   • getSubMap(parentKey, subKey)
 *   • getZoneList(key)
 *
 * A properties file named <code>config.properties</code> is read automatically if present.
 * Unit tests or runtime code can inject values programmatically via the setter helpers.
 */
public final class Config {
	// 🌦️ Holds current weather conditions for overlays
	public static Map<String, String> WEATHER_DATA = new HashMap<>();
		
    /* ── INTERNAL STORAGE ────────────────────────────────────────── */
    private static final Map<String, String>              stringProps = new HashMap<>();
    private static final Map<String, Map<String, String>> mapProps    = new HashMap<>();
    private static final Map<String, List<Zone>>          zoneLists   = new HashMap<>();
    public static String VISION_MODE = "Normal";

    /* ── STATIC INITIALISER – optional properties load ───────────── */
    static { tryLoadProperties("config.properties"); }

    /* Attempts to read key=value pairs from a simple .properties text file. */
    private static void tryLoadProperties(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.lines().forEach(line -> {
                String l = line.trim();
                if (l.isEmpty() || l.startsWith("#")) return;
                int idx = l.indexOf('=');
                if (idx > 0) {
                    stringProps.put(l.substring(0, idx).trim(),
                                    l.substring(idx + 1).trim());
                }
            });
         
            SystemLogPanel.append("⚙️  Config loaded " + stringProps.size() +
                    " entries from " + path);

        } catch (Exception ignored) {
            /* file is optional – silence is OK. */
        }
    }

    /* ───────────────────────────────────────────────────────────────
       STRING ACCESSORS
       ─────────────────────────────────────────────────────────────── */
    public static String getString(String key) { return stringProps.getOrDefault(key, ""); }
    public static void   setString(String key, String value) { stringProps.put(key, value); }

    /* ───────────────────────────────────────────────────────────────
       MAP ACCESSORS
       ─────────────────────────────────────────────────────────────── */
    public static Map<String, String> getMap(String key) {
        return mapProps.getOrDefault(key, Collections.emptyMap());
    }

    public static Map<String, String> getSubMap(String parentKey, String subKey) {
        if (!mapProps.containsKey(parentKey)) return Collections.emptyMap();
        String val = mapProps.get(parentKey).get(subKey);
        return val == null ? Collections.emptyMap()
                           : Collections.singletonMap(subKey, val);
    }

    public static void setMap(String key, Map<String, String> map) {
        mapProps.put(key, map);
    }

    /* ───────────────────────────────────────────────────────────────
       ZONE-LIST ACCESSORS
       ─────────────────────────────────────────────────────────────── */
    public static List<Zone> getZoneList(String key) {
        return zoneLists.getOrDefault(key, Collections.emptyList());
    }

    public static void registerZoneList(String key, List<Zone> zones) {
        zoneLists.put(key, zones);
    }

    /* ───────────────────────────────────────────────────────────────
       UTILITIES
       ─────────────────────────────────────────────────────────────── */
    /** Clears **all** stored config data (handy for tests). */
    public static void reset() {

    	SystemLogPanel.append("Config reset");
        stringProps.clear();
        mapProps.clear();
        zoneLists.clear();
    }
    /* ─────────────────────────── NEW overload ─────────────────────────── */

    /**
     * Returns the configured value for {@code key} or {@code defaultValue} if
     * it is missing, empty, or null.  This is the overload required by
     * <code>FusionCoordinator</code> line&nbsp;31.
     */
    public static String getString(String key, String defaultValue) {
        String value = getString(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    /* ───────────── NEW helper required by DroneAgent ───────────── */

    /**
     * Returns the configured numeric value for {@code key} or
     * {@code defaultValue} when the property is missing, blank, or
     * cannot be parsed as a double.
     */
    public static double getDouble(String key, double defaultValue) {
        String v = getString(key);
        if (v == null || v.isBlank()) return defaultValue;
        try { return Double.parseDouble(v); }
        catch (NumberFormatException nfe) { return defaultValue; }
    }
    public static Map<String, Object> getMapAsObject(String key) {
        Map<String, String> original = getMap(key);
        Map<String, Object> converted = new HashMap<>();
        for (Map.Entry<String, String> entry : original.entrySet()) {
            converted.put(entry.getKey(), entry.getValue());
        }
        return converted;
    }

    private Config() { /* util – do not instantiate */ }
    public static double[][] ELEVATION_DATA;

    /**
     * Initializes elevation data (real if available, simulated otherwise).
     */
    static {
        double[][] result = TerrainDataLoader.loadOrSimulate("elevation.csv", 100, 100);
        if (result != null) {
            ELEVATION_DATA = result;
            
            SystemLogPanel.append("🌄 Simulated elevation data generated.");

        } else {
           
            SystemLogPanel.append("🌄 Real elevation data loaded from TerrainAwarenessManager");

        }

        // 🆕 Default weather condition if no real data provider is plugged in
        WEATHER_DATA.put("condition", "rain");
        WEATHER_DATA.put("severity", "0.5");
    }

    
    public static double[][] generateSimulatedElevation(int width, int height) {
        return TerrainDataLoader.generateSimulatedElevation(width, height);
    }


}
