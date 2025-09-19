package main.config;

/**
 * Centralised, immutable settings that many UI panels read.
 * Moving them here prevents "constant explosion" and keeps every
 * module in-sync when a mode name changes.
 */
public final class UIModeConstants {

    private UIModeConstants() {}

    // ------------------------------------------------------------------
    //  System-wide mode flags & strings
    // ------------------------------------------------------------------
    public static  String AI_MODE         = "HYBRID";   // HYBRID | AI | DECISION_TREE
    public static final String VISION_MODE     = "FALCON";   // FALCON | THERMAL | MULTI
    public static final boolean USE_SIMULATION = false;
    public static final boolean IS_OFFLINE_MODE= false;

    // ------------------------------------------------------------------
    //  Data-channel identifiers
    // ------------------------------------------------------------------
    public static final String WEATHER_DATA    = "LOCAL";
    public static final String ELEVATION_DATA  = "DEM_CACHE";
    public static final String NO_FLY_ZONES    = "DEFAULT";
}
