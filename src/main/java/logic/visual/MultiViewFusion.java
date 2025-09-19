package logic.visual;

import java.util.*;

/**
 * 🎥 MultiViewFusion - Combines data from FalconVision, InfraredView, and WideCam to produce a unified visual threat picture.
 * Enhances reliability before engagement.
 */
public class MultiViewFusion {

    public static class VisualInput {
        public final String droneId;
        public final FalconVisionAnalyzer.VisualType visualType;
        public final boolean detectedInInfrared;
        public final boolean detectedInWideView;
        public final double clarityScore; // 0.0 - 1.0

        public VisualInput(String droneId, FalconVisionAnalyzer.VisualType type, boolean inIR, boolean inWide, double clarity) {
            this.droneId = droneId;
            this.visualType = type;
            this.detectedInInfrared = inIR;
            this.detectedInWideView = inWide;
            this.clarityScore = clarity;
        }
    }

    public static class FusionResult {
        public final String droneId;
        public final FalconVisionAnalyzer.VisualType fusedType;
        public final double confidenceScore;
        public final boolean confirmedThreat;

        public FusionResult(String id, FalconVisionAnalyzer.VisualType type, double confidence, boolean threat) {
            this.droneId = id;
            this.fusedType = type;
            this.confidenceScore = confidence;
            this.confirmedThreat = threat;
        }
    }

    /**
     * Fuses multiple visual sources to form a final classification with confidence.
     */
    public FusionResult fuse(List<VisualInput> inputs) {
        if (inputs.isEmpty()) return null;

        String id = inputs.get(0).droneId;
        Map<FalconVisionAnalyzer.VisualType, Integer> votes = new HashMap<>();
        double claritySum = 0.0;
        int irCount = 0, wideCount = 0;

        for (VisualInput input : inputs) {
            votes.put(input.visualType, votes.getOrDefault(input.visualType, 0) + 1);
            claritySum += input.clarityScore;
            if (input.detectedInInfrared) irCount++;
            if (input.detectedInWideView) wideCount++;
        }

        FalconVisionAnalyzer.VisualType majorityType = votes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(FalconVisionAnalyzer.VisualType.UNKNOWN);

        double confidence = (votes.getOrDefault(majorityType, 0) / (double) inputs.size()) * 0.5
                          + (claritySum / inputs.size()) * 0.3
                          + ((irCount + wideCount) / (double) (2 * inputs.size())) * 0.2;

        boolean threat = new FalconVisionAnalyzer().isVisualThreat(majorityType);

        return new FusionResult(id, majorityType, confidence, threat && confidence > 0.6);
    }
}
