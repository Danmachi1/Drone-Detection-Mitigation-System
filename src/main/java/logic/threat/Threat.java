package logic.threat;

import java.util.*;
import logic.prediction.IntentEstimator;
import java.util.Arrays;
	


/**
 * 🛡️ Threat - Represents any enemy unit tracked in the SkyShield system,
 * such as a drone, missile, or unknown aerial object.
 */
public class Threat {

    public enum ThreatType { DRONE, MISSILE, DECOY, UNKNOWN }

    private final String id;
    private ThreatType type;
    private boolean entangled = false;
    private boolean disabled = false;
    private boolean weakened = false;
    private boolean neutralized = false;
    
    /* Kinematic + confidence state (new) */
    private volatile double[] position2D = new double[]{Double.NaN, Double.NaN};
    private volatile double[] velocity2D = new double[]{Double.NaN, Double.NaN};
    private volatile boolean  armed       = false;
    private volatile double   confidence  = 0.0;   // 0-1
    

    private long lastDisabledTime = 0;
    private final long DISABLE_DURATION_MS = 5000;

    private final List<String> reasonLog = new ArrayList<>();
    public Threat() {
        this.id   = "UNKNOWN-" + System.nanoTime();
        this.type = ThreatType.UNKNOWN;
    }
    public Threat(String id, ThreatType type) {
        this.id = id;
        this.type = type	;
    }

    public void weaken(double severity) {
        if (severity > 0.5) {
            this.weakened = true;
            log("🔻 Threat weakened: severity=" + severity);
        }
        if (severity >= 1.0) {
            this.neutralized = true;
            log("☠️ Threat neutralized via overkill.");
        }
    }

    public void entangle() {
        this.entangled = true;
        log("🪢 Threat entangled by net.");
    }

    public void disableTemporarily() {
        this.disabled = true;
        this.lastDisabledTime = System.currentTimeMillis();
        log("⚡ Threat disabled temporarily.");
    }

    public void updateState() {
        if (disabled && System.currentTimeMillis() - lastDisabledTime > DISABLE_DURATION_MS) {
            disabled = false;
            log("🔄 Threat re-enabled after EMP.");
        }
    }

    public boolean isNeutralized() {
        return neutralized;
    }

    public boolean isActive() {
        updateState();
        return !(neutralized || disabled || entangled);
    }

    public String getId() {
        return id;
    }

    public ThreatType getType() {
        return type;
    }

    public void setType(ThreatType type) {
        this.type = type;
    }

    public List<String> getReasonLog() {
        return Collections.unmodifiableList(reasonLog);
    }

    private void log(String msg) {
        reasonLog.add(System.currentTimeMillis() + ": " + msg);
    }

    public boolean isEntangled() {
        return entangled;
    }

    public boolean isDisabled() {
        updateState();
        return disabled;
    }

    public boolean isWeakened() {
        return weakened;
    }
    /* ─────────────────────── 2-D helper (new) ─────────────────────── */

    /**
     * @return {@code [x,y]} in world units, or {@code [NaN,NaN]} if the
     *         fields cannot be extracted.  Needed by {@code MissionBuilder}.
     */
    public double[] getPosition2D() {
        // Try common field names first
        for (String[] pair : new String[][] {
                {"x", "y"}, {"posX", "posY"}, {"latitude", "longitude"}}) {
            try {
                java.lang.reflect.Field fx = getClass().getDeclaredField(pair[0]);
                java.lang.reflect.Field fy = getClass().getDeclaredField(pair[1]);
                fx.setAccessible(true);
                fy.setAccessible(true);
                Object ox = fx.get(this);
                Object oy = fy.get(this);
                if (ox instanceof Number nx && oy instanceof Number ny)
                    return new double[]{nx.doubleValue(), ny.doubleValue()};
            } catch (NoSuchFieldException ignored) {
            } catch (Exception e) { /* access error – fall through */ }
        }
        // Fallback: unknown position
        return new double[]{Double.NaN, Double.NaN};
    }

    /* ───────────────── intent tagging (new) ───────────────── */

    /** Most recent intent inference and confidence (0 – 1). */
    private volatile IntentEstimator.IntentType intentType =
            IntentEstimator.IntentType.UNKNOWN;
    private volatile double intentConfidence = 0.0;

    /**
     * Updates the threat’s current intent label and confidence.
     *
     * @param type  enum value from {@link IntentEstimator.IntentType}
     * @param conf  confidence score (0.0 – 1.0)
     */
    public void setIntent(IntentEstimator.IntentType type, double conf) {
        if (type != null) this.intentType = type;
        this.intentConfidence = conf;
    }

    public double[] getPosition() {
        return Arrays.copyOf(position2D, position2D.length);
    }

    public double[] getVelocity() {
        return Arrays.copyOf(velocity2D, velocity2D.length);
    }

    public boolean isArmed() {
        return armed;
    }

    public double getConfidence() {
        return confidence;
    }
    public void setPosition(double[] pos) {
        if (pos != null && pos.length >= 2)
            this.position2D = Arrays.copyOf(pos, 2);
    }

    public void setVelocity(double[] vel) {
        if (vel != null && vel.length >= 2)
            this.velocity2D = Arrays.copyOf(vel, 2);
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public void setConfidence(double conf) {
        this.confidence = Math.max(0.0, Math.min(1.0, conf));
    }



}
