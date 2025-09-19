package main.config;

import java.io.FileReader;
import java.util.*;
import com.google.gson.*;

/**
 * 👩‍✈️ OperatorProfileManager – Central registry for human operator accounts.
 * Roles & permissions determine which UI panels and kill-chain actions are
 * available in the ground-station software.
 *
 * JSON file format:
 * [
 *   { "id":"OP-001", "name":"Alice",   "role":"SUPERVISOR",
 *     "permissions":["ENGAGE","CONFIG","SHUTDOWN"] },
 *   { "id":"OP-002", "name":"Bob",     "role":"OBSERVER",
 *     "permissions":["VIEW_ONLY"] }
 * ]
 */
public final class OperatorProfileManager {

    /* ── Internal DTO ───────────────────────────────────────────── */
    public static class Profile {
        public final String  id, name, role;
        public final Set<String> permissions;

        public Profile(String id, String name, String role, Collection<String> perms) {
            this.id = id; this.name = name; this.role = role;
            this.permissions = new HashSet<>(perms);
        }
    }

    /* Registry keyed by operator-ID */
    private static final Map<String, Profile> profiles = new HashMap<>();

    private OperatorProfileManager() { /* util */ }

    /* ── Loader (JSON) ───────────────────────────────────────────── */

    public static void loadFromJson(String path) {
        try (FileReader reader = new FileReader(path)) {
            JsonArray arr = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement el : arr) {
                JsonObject o   = el.getAsJsonObject();
                String id      = o.get("id").getAsString();
                String name    = o.get("name").getAsString();
                String role    = o.get("role").getAsString();

                Set<String> perms = new HashSet<>();
                o.get("permissions").getAsJsonArray()
                                   .forEach(p -> perms.add(p.getAsString()));

                registerProfile(new Profile(id, name, role, perms));
            }
            System.out.println("👤 Loaded " + profiles.size() +
                               " operator profiles from " + path);
        } catch (Exception ex) {
            System.err.println("⚠️  Failed to load operator JSON: " + ex.getMessage());
        }
    }

    /* ── Registration / queries ─────────────────────────────────── */

    public static void registerProfile(Profile p)          { profiles.put(p.id, p); }
    public static void removeProfile(String id)            { profiles.remove(id);   }
    public static Profile getProfile(String id)            { return profiles.get(id); }

    public static boolean isAuthorized(String id, String permission) {
        Profile p = profiles.get(id);
        return p != null && p.permissions.contains(permission);
    }

    public static Collection<Profile> listOperators()      { return profiles.values(); }

    /* ── Utilities (tests) ──────────────────────────────────────── */
    public static void reset() { profiles.clear(); }
}
