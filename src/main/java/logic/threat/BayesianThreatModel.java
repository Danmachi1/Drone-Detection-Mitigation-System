package logic.threat;

import java.util.HashMap;
import java.util.Map;

/**
 * 🎲 BayesianThreatModel - Probabilistic model to classify threats using Bayesian inference.
 */
public class BayesianThreatModel {

    private Map<Threat.ThreatType, Double> probabilities = new HashMap<>();

    public BayesianThreatModel() {
        reset();
    }

    /**
     * Initializes or resets uniform prior probabilities.
     */
    public void reset() {
        probabilities.clear();
        for (Threat.ThreatType type : Threat.ThreatType.values()) {
            probabilities.put(type, 1.0 / Threat.ThreatType.values().length);
        }
    }

    /**
     * Updates posterior probabilities based on new evidence likelihoods.
     * @param likelihoodMap Map of ThreatType to P(evidence | ThreatType)
     */
    public void update(Map<Threat.ThreatType, Double> likelihoodMap) {
        for (Map.Entry<Threat.ThreatType, Double> entry : likelihoodMap.entrySet()) {
            Threat.ThreatType type = entry.getKey();
            double likelihood = entry.getValue();
            double prior = probabilities.getOrDefault(type, 0.0);
            probabilities.put(type, prior * likelihood);
        }
        normalize();
    }

    /**
     * Normalizes probabilities so they sum to 1.
     */
    private void normalize() {
        double sum = probabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum == 0) return;
        for (Threat.ThreatType type : probabilities.keySet()) {
            probabilities.put(type, probabilities.get(type) / sum);
        }
    }

    /**
     * Returns the most probable threat type.
     */
    public Threat.ThreatType getMostLikelyType() {
        return probabilities.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(Threat.ThreatType.UNKNOWN);
    }

    /**
     * Gets current probability distribution.
     */
    public Map<Threat.ThreatType, Double> getProbabilities() {
        return new HashMap<>(probabilities);
    }
}
