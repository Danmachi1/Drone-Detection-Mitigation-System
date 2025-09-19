package logic.threat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 📊 ThreatDatabase – Central registry for current active threats.
 * Allows retrieval by UI or command modules.
 */
public class ThreatDatabase {

    private static final Map<String, Threat> activeThreats = new ConcurrentHashMap<>();

    /** Adds or updates a threat */
    public static void registerThreat(Threat threat) {
        if (threat != null && threat.getId() != null) {
            activeThreats.put(threat.getId(), threat);
        }
    }

    /** Removes a threat from the list (e.g. neutralized) */
    public static void removeThreat(String id) {
        activeThreats.remove(id);
    }

    /** Returns a live list of all known threats */
    public static List<Threat> getAllThreats() {
        return new ArrayList<>(activeThreats.values());
    }

    /** Optional: fetch by ID */
    public static Threat getThreatById(String id) {
        return activeThreats.get(id);
    }

    /** Clears all threat data */
    public static void clearAll() {
        activeThreats.clear();
    }
}
