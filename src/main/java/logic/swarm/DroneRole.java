package logic.swarm;

/** Canonical list of roles used across UI & planners. */
public enum DroneRole {
    INTERCEPTOR,
    BEE_SCOUT,
    REPAIR_UNIT,
    WOLF_FLANK;

    /** Fast utility for table/choice-box population. */
    public static String[] names() {
        DroneRole[] vals = values();
        String[] out = new String[vals.length];
        for (int i = 0; i < vals.length; i++) out[i] = vals[i].name();
        return out;
    }
}
