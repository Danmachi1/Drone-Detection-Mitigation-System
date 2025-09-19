package logic.threat;

import logic.prediction.IntentEstimator;
import logic.prediction.MotionBehaviorClassifier.BehaviorType;
import logic.prediction.IntentEstimator.IntentResult;
import logic.zones.ZoneManager;

/**
 * ☣️ ThreatReasoningEngine - Evaluates threat level of a target based on motion, zones, and intent.
 * Supports hybrid reasoning (rules + AI-ready architecture).
 */
public class ThreatReasoningEngine {

    public enum ReasoningMode { RULE, AI, HYBRID }

    private ReasoningMode mode = ReasoningMode.HYBRID;
    private final IntentEstimator intentEstimator = new IntentEstimator();

    /**
     * Sets the reasoning mode (manual override or config-based).
     */
    public void setReasoningMode(ReasoningMode mode) {
        this.mode = mode;
    }

    /**
     * Computes a threat score for a target (0.0 = safe, 1.0 = critical).
     * Fusion of zone, intent, behavior, and position.
     */
    public ThreatResult computeThreat(double[] position, double[] velocity, BehaviorType behavior, double fusionConfidence) {
        if (position == null || velocity == null || behavior == null) {
            return new ThreatResult(0.0, "Insufficient data");
        }

        IntentResult intent = intentEstimator.estimateWithHybrid(position, velocity, behavior, mode != ReasoningMode.RULE);
        boolean nearAsset = ZoneManager.isNearAsset(position);
        boolean insideNoFly = ZoneManager.isInsideNoFly(position);

        double score = 0.0;
        StringBuilder reason = new StringBuilder();

        // Zone logic
        if (insideNoFly) {
            score += 0.4;
            reason.append("Inside no-fly zone. ");
        } else if (nearAsset) {
            score += 0.3;
            reason.append("Near critical asset. ");
        }

        // Intent logic
        if (intent.type == IntentEstimator.IntentType.ENTERING_NOFLY) {
            score += 0.3;
            reason.append("Intent: entering no-fly zone. ");
        } else if (intent.type == IntentEstimator.IntentType.APPROACHING_ASSET) {
            score += 0.25;
            reason.append("Intent: approaching asset. ");
        } else if (intent.type == IntentEstimator.IntentType.SCANNING) {
            score += 0.15;
            reason.append("Intent: scanning behavior. ");
        }

        // Behavior logic
        if (behavior == BehaviorType.CIRCLE || behavior == BehaviorType.ZIGZAG) {
            score += 0.2;
            reason.append("Behavior: evasive/circling. ");
        } else if (behavior == BehaviorType.FLEEING) {
            score -= 0.1;
            reason.append("Behavior: fleeing. ");
        }

        // Fusion confidence bonus
        score *= Math.min(1.0, Math.max(0.0, fusionConfidence));
        reason.append(String.format("Fusion confidence: %.2f", fusionConfidence));

        return new ThreatResult(Math.min(score, 1.0), reason.toString());
    }

    /**
     * Result object for threat score and traceable explanation.
     */
    public static class ThreatResult {
        public final double threatScore;
        public final String reasoning;

        public ThreatResult(double threatScore, String reasoning) {
            this.threatScore = threatScore;
            this.reasoning = reasoning;
        }
    }
}
