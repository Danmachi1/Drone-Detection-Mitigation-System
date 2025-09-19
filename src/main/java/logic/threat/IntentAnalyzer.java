package logic.threat;

import logic.prediction.IntentEstimator;
import logic.prediction.IntentEstimator.IntentResult;
import logic.prediction.MotionBehaviorClassifier;
import logic.prediction.MotionBehaviorClassifier.BehaviorType;

/**
 * 🧐 IntentAnalyzer – fuses motion-pattern data and zone logic to
 * assign a current intent (with confidence) to a {@link Threat}.
 */
public class IntentAnalyzer {

    private final IntentEstimator         intentEstimator   = new IntentEstimator();
    private final MotionBehaviorClassifier behaviourClassifier = new MotionBehaviorClassifier();

    /** Keeps the last position so we can feed prev/curr into the estimator. */
    private double[] lastPosition = null;

    /**
     * Analyse one movement update and return the new intent.
     *
     * @param threat   Threat being tracked (may be {@code null} in tests)
     * @param position {x, y} current coords (metres)
     * @param velocity {vx, vy} current velocity (m/s)
     */
    public IntentResult analyze(Threat threat,
                                double[] position,
                                double[] velocity) {

        /* 1️⃣  Update motion classifier history */
        if (position != null) {
            behaviourClassifier.addPosition(position);
        }

        BehaviorType behaviour = behaviourClassifier.classify();   // classifier supplies latest pattern

        /* 2️⃣  Intent estimation – hybrid rule / AI */
        double[] prev = lastPosition;
        double[] curr = position;
        lastPosition  = position;                                  // store for next cycle

        // Determine whether the threat is armed if such a method exists
        boolean isArmed = false;
        if (threat != null) {
            try {
                isArmed = (boolean) Threat.class
                        .getMethod("isArmed")
                        .invoke(threat);
            } catch (Exception ignored) { /* method not present – assume false */ }
        }

        IntentResult result =
                intentEstimator.estimateWithHybrid(prev, curr, behaviour, isArmed);

        /* 3️⃣  Persist intent back onto the Threat object */
        if (threat != null) {
            threat.setIntent(result.type, result.confidence);
        }

        return result;
    }
}
