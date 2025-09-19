package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 📦 JsonStorageManager – Lightweight helper for reading / writing JSON blobs.
 *
 * Usage:
 *   MissionPlan plan = ...;
 *   JsonStorageManager.save("missions", "plan-alpha", plan);
 *
 *   MissionPlan loaded = JsonStorageManager.load(
 *           "missions", "plan-alpha", MissionPlan.class);
 *
 * The manager stores files under a root folder (<code>data/</code> by default).
 * A timestamp suffix can be appended automatically if desired.
 */
public final class JsonStorageManager {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();
    private static String rootDir = "data";

    private JsonStorageManager() { /* util */ }

    /* ───────────────────────────────────────────────────────── */

    public static void setRootDirectory(String dir) { rootDir = dir; }

    /** Encodes obj → JSON and writes to rootDir/<path>/<name>.json */
    public static <T> void save(String subPath,
                                String name,
                                T       obj,
                                boolean timestamp) {

        ensureDir(subPath);

        String file = rootDir + "/" + subPath + "/" + name +
                (timestamp ? "-" + stamp() : "") + ".json";
        try (FileWriter fw = new FileWriter(file)) {
            GSON.toJson(obj, fw);
            System.out.println("💾 Saved JSON → " + file);
        } catch (Exception ex) {
            System.err.println("⚠️  JsonStorageManager save failed: " + ex.getMessage());
        }
    }

    public static <T> void save(String path, String name, T obj) {
        save(path, name, obj, false);
    }

    /** Loads JSON into clazz from rootDir/<path>/<name>.json */
    public static <T> T load(String subPath,
                             String name,
                             Class<T> clazz) {

        String file = rootDir + "/" + subPath + "/" + name + ".json";
        try (FileReader fr = new FileReader(file)) {
            return GSON.fromJson(fr, clazz);
        } catch (Exception ex) {
            System.err.println("⚠️  JsonStorageManager load failed: " + ex.getMessage());
            return null;
        }
    }

    /* ───────────────────────────────────────────────────────── */

    private static void ensureDir(String sub) {
        File dir = new File(rootDir + "/" + sub);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("⚠️  JsonStorageManager: cannot create " + dir);
        }
    }

    private static String stamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }
}
