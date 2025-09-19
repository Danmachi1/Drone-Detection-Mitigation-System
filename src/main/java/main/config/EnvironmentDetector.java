package main.config;

/**
 * 🌐 EnvironmentDetector – Utility that identifies the current runtime
 * environment (OS, architecture, simulation vs. production) so other components
 * can enable/disable hardware-specific logic as needed.
 *
 * All values are lazily cached.  Tests can override via the static setters.
 */
public final class EnvironmentDetector {

    /* Cached basic properties */
    private static final String osName = System.getProperty("os.name").toLowerCase();
    private static final String arch   = System.getProperty("os.arch").toLowerCase();

    /* Runtime overrides (useful for tests / CI) */
    private static boolean simOverride  = false;
    private static Boolean gpuAvailable = null;

    private EnvironmentDetector() { /* util – no instances */ }

    /* ── OS detection ───────────────────────────────────────────── */

    public static boolean isWindows() { return osName.contains("win"); }
    public static boolean isMac()     { return osName.contains("mac"); }
    public static boolean isLinux()   { return osName.contains("nux"); }

    /* ── Architecture detection ─────────────────────────────────── */

    /** Detects Apple-Silicon & generic ARM64. */
    public static boolean isArmArch() {
        return arch.contains("arm") || arch.contains("aarch") || arch.contains("m1");
    }

    /* ── Simulation / production mode ───────────────────────────── */

    /**
     * Returns true when:
     *  • system property <code>-DSIM_MODE=true</code> is passed, or
     *  • {@link #forceSimulation(boolean)} is used in unit tests.
     */
    public static boolean isSimulation() {
        return simOverride || Boolean.getBoolean("SIM_MODE");
    }

    /* ── GPU availability heuristic ─────────────────────────────── */

    public static boolean hasGpu() {
        if (gpuAvailable == null) {
            gpuAvailable = Boolean.getBoolean("GPU_PRESENT") ||
                           System.getenv().containsKey("NVIDIA_VISIBLE_DEVICES");
        }
        return gpuAvailable;
    }

    /* ── Override helpers ───────────────────────────────────────── */

    /** Force simulation mode (unit tests). */
    public static void forceSimulation(boolean sim) { simOverride = sim; }

    /** Force GPU availability flag (unit tests). */
    public static void forceGpu(boolean gpu)        { gpuAvailable = gpu; }
}
