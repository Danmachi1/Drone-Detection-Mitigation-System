package storage;

import com.google.gson.*;
import logic.zones.Zone;
import main.config.Config;

import java.io.FileReader;
import java.util.*;

/**
 * 📂 ConfigLoader – Reads a JSON file that aggregates all boot-time settings
 * (strings, maps, zone lists) and registers them in {@link Config}.
 */
public final class ConfigLoader {

    /** Loads the given JSON file and populates {@link Config}. */
    public static void load(String jsonPath) {
        try (FileReader r = new FileReader(jsonPath)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();

            /* ── Simple string props ─────────────────────────── */
            if (root.has("strings")) {
                JsonObject obj = root.getAsJsonObject("strings");
                for (String k : obj.keySet()) {
                    Config.setString(k, obj.get(k).getAsString());
                }
            }

            /* ── Map props (nested objects) ──────────────────── */
            if (root.has("maps")) {
                JsonObject maps = root.getAsJsonObject("maps");
                for (String parent : maps.keySet()) {
                    JsonObject m = maps.getAsJsonObject(parent);
                    Map<String,String> javaMap = new HashMap<>();
                    for (String k : m.keySet()) javaMap.put(k, m.get(k).getAsString());
                    Config.setMap(parent, javaMap);
                }
            }

            /* ── Zone lists (array of objects) ───────────────── */
            if (root.has("zoneLists")) {
                JsonObject zroot = root.getAsJsonObject("zoneLists");
                for (String listName : zroot.keySet()) {
                    List<Zone> zones = new ArrayList<>();
                    JsonArray arr = zroot.getAsJsonArray(listName);
                    for (JsonElement el : arr) {
                        JsonObject o = el.getAsJsonObject();
                        String id = o.get("id").getAsString();
                        double x = o.get("x").getAsDouble();
                        double y = o.get("y").getAsDouble();
                        double radius = o.get("r").getAsDouble();

                        List<double[]> polygon = new ArrayList<>();
                        if (o.has("polygon")) {
                            JsonArray polyArray = o.getAsJsonArray("polygon");
                            for (JsonElement pt : polyArray) {
                                JsonArray coords = pt.getAsJsonArray();
                                polygon.add(new double[] {
                                    coords.get(0).getAsDouble(),
                                    coords.get(1).getAsDouble()
                                });
                            }
                        }

                        zones.add(new Zone(id, x, y, radius, polygon));
                    }
                    Config.registerZoneList(listName, zones);
                }
            }

            System.out.println("✅ ConfigLoader: loaded " + jsonPath);

        } catch (Exception ex) {
            System.err.println("⚠️  ConfigLoader failed: " + ex.getMessage());
        }
    }

    private ConfigLoader() { /* util – do not instantiate */ }
}
