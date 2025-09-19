package main.config;

/**
 * ⏱️ SimulationClock – Provides a controllable clock so simulation and
 * analytics components can run faster-than-real-time or be paused.
 *
 * Public API
 * ───────────────────────────────────────────────────────────
 *  • now()            – current simulated epoch-ms
 *  • setMultiplier(x) – speed up or slow down
 *  • pause()/resume() – freeze / unfreeze time progression
 *  • advance(ms)      – jump forward (unit tests)
 *  • reset()          – real-time, multiplier = 1
 */
public final class SimulationClock {

    /* ── Internal state (all guarded by synchronized methods) ───── */
    private static long   realStartMs = System.currentTimeMillis();
    private static long   simStartMs  = realStartMs;
    private static double multiplier  = 1.0;

    private static boolean paused     = false;
    private static long    pauseReal  = 0;
    private static long    pauseSim   = 0;

    private SimulationClock() { /* util class – do not instantiate */ }

    /* ── TIME QUERY ─────────────────────────────────────────────── */

    /** Returns simulated epoch-ms (fast-forwarded if multiplier >1). */
    public static synchronized long now() {
        if (paused) return pauseSim;
        long realNow = System.currentTimeMillis();
        long delta   = realNow - realStartMs;
        return simStartMs + (long) (delta * multiplier);
    }

    /* ── CONTROL METHODS ────────────────────────────────────────── */

    /** Sets a new speed multiplier (e.g., 2.0 = double speed). */
    public static synchronized void setMultiplier(double m) {
        if (m <= 0) throw new IllegalArgumentException("Multiplier must be > 0");
        long currentSim = now();           // capture current sim time
        realStartMs = System.currentTimeMillis();
        simStartMs  = currentSim;
        multiplier  = m;
    }

    /** Pauses simulation time (now() becomes constant). */
    public static synchronized void pause() {
        if (!paused) {
            pauseReal = System.currentTimeMillis();
            pauseSim  = now();
            paused    = true;
        }
    }

    /** Resumes simulation time using existing multiplier. */
    public static synchronized void resume() {
        if (paused) {
            long realNow  = System.currentTimeMillis();
            long realDiff = realNow - pauseReal;
            realStartMs += realDiff;   // shift real-start so sim time is continuous
            paused = false;
        }
    }

    public static synchronized boolean isPaused() { return paused; }

    /** Resets clock to real-time progression (multiplier = 1). */
    public static synchronized void reset() {
        realStartMs = System.currentTimeMillis();
        simStartMs  = realStartMs;
        multiplier  = 1.0;
        paused      = false;
    }

    /** Unit-test helper – jump simulated time forward by delta-ms. */
    public static synchronized void advance(long deltaMs) {
        simStartMs += deltaMs;
    }
}
