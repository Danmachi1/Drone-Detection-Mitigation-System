package logic.prediction;

import logic.zones.ZoneManager;
import logic.prediction.MotionBehaviorClassifier.BehaviorType;

import java.util.HashMap;
import java.util.Map;

/**
 * 🧠 IntentEstimator - Predicts strategic intent of a moving target based on zones and behavior.
 * Supports rule-based logic and fallback to hybrid-AI (if enabled).
 */
public class IntentEstimator {

    public enum IntentType {
        APPROACHING_ASSET, ENTERING_NOFLY, FLEEING, SCANNING, LOITERING, UNKNOWN
    }

    public static class IntentResult {
        public final IntentType type;
        public final double confidence;

        public IntentResult(IntentType type, double confidence) {
            this.type = type;
            this.confidence = confidence;
        }
    }

    /**
     * Estimates intent from target position and movement pattern.
     */
    public IntentResult estimate(double[] position, double[] velocity, BehaviorType behavior) {
        if (position == null || velocity == null) return new IntentResult(IntentType.UNKNOWN, 0.0);

        boolean headingToAsset = ZoneManager.isHeadingToAsset(position, velocity);
        boolean headingToNoFly = ZoneManager.isHeadingToNoFly(position, velocity);
        boolean insideRestricted = ZoneManager.isInsideRestricted(position);

        if (headingToNoFly && !insideRestricted) {
            return new IntentResult(IntentType.ENTERING_NOFLY, 0.9);
        }

        if (headingToAsset) {
            return new IntentResult(IntentType.APPROACHING_ASSET, 0.85);
        }

        if (behavior == BehaviorType.STRAIGHT && !headingToAsset) {
            return new IntentResult(IntentType.FLEEING, 0.75);
        }

        if (behavior == BehaviorType.CIRCLE || behavior == BehaviorType.ZIGZAG) {
            return new IntentResult(IntentType.SCANNING, 0.6);
        }

        if (behavior == BehaviorType.WEAVING) {
            return new IntentResult(IntentType.LOITERING, 0.5);
        }

        return new IntentResult(IntentType.UNKNOWN, 0.3);
    }

    /**
     * Optional hook for AI-based fallback logic (can be replaced with ML later).
     */
    public IntentResult estimateWithHybrid(double[] position, double[] velocity, BehaviorType behavior, boolean useAI) {
        if (!useAI) return estimate(position, velocity, behavior);

        // Placeholder for AI model logic
        // e.g., LSTM.predictIntent(position, velocity, behavior)
        return estimate(position, velocity, behavior); // fallback to rule logic for now
    }
}
