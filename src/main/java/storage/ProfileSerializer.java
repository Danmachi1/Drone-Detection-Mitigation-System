package storage;

import main.config.OperatorProfileManager.Profile;
import main.config.OperatorProfileManager;

/**
 * 👤 ProfileSerializer – Persists and restores operator profiles en masse
 * using JsonStorageManager.  Handy for backup/restore or sharing presets.
 */
public final class ProfileSerializer {

    private static final String PATH = "profiles";
    private static final String FILE = "operators";

    /** Dumps all current operator profiles to JSON. */
    public static void saveAll() {
        JsonStorageManager.save(PATH, FILE,
                OperatorProfileManager.listOperators());
    }

    /** Loads profiles from JSON and registers them. */
    public static void loadAll() {
        Profile[] arr = JsonStorageManager.load(PATH, FILE, Profile[].class);
        if (arr != null) {
            for (Profile p : arr) {
                OperatorProfileManager.registerProfile(p);
            }
        }
    }

    private ProfileSerializer() { /* util */ }
}
