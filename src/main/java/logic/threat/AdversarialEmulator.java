package logic.threat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 🤖 AdversarialEmulator - Simulates enemy threat behaviors for robust testing.
 * Patterns include random disable (EMP), entanglement (net), and weakening (damage).
 */
public class AdversarialEmulator {

    public enum EvasionPattern { NONE, RANDOM_FAIL, ZIGZAG_MOVE, CIRCLE_MOVE }

    private final Random rand = new Random();
    private EvasionPattern pattern = EvasionPattern.NONE;

    public AdversarialEmulator() {
        this.pattern = EvasionPattern.RANDOM_FAIL;
    }

    public AdversarialEmulator(EvasionPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Generates a list of Threat objects with initial states.
     */
    public List<Threat> generateThreats(int count, Threat.ThreatType type) {
        List<Threat> threats = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            threats.add(new Threat("Threat-" + i, type));
        }
        return threats;
    }

    /**
     * Applies one simulation step to each threat based on the selected pattern.
     */
    public void simulateStep(Threat threat) {
        if (threat == null || threat.isNeutralized()) return;

        switch (pattern) {
            case RANDOM_FAIL:
                if (rand.nextDouble() < 0.1) {
                    threat.disableTemporarily();
                } else if (rand.nextDouble() < 0.05) {
                    threat.entangle();
                }
                break;
            case ZIGZAG_MOVE:
            case CIRCLE_MOVE:
                // Movement patterns simulated externally via sensor data
                break;
            default:
                break;
        }
    }

    /**
     * Runs a full simulation over multiple steps for a list of threats.
     */
    public void simulate(int steps, List<Threat> threats) {
        for (int step = 0; step < steps; step++) {
            for (Threat t : threats) {
                simulateStep(t);
            }
        }
    }
}