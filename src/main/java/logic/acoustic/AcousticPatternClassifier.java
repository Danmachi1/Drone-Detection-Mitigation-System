package logic.acoustic;

import java.util.*;

/**
 * 🔊 AcousticPatternClassifier - Classifies incoming audio signal patterns based on known drone acoustic signatures.
 * Supports stealth detection and audio-based threat identification.
 */
public class AcousticPatternClassifier {

    public enum AcousticType {
        KNOWN_MICRO_DRONE, KNOWN_MINI_DRONE, UNKNOWN_DRONE, NOISE, BIRD, VEHICLE
    }

    // Simulated acoustic fingerprint hashes for known drones (simplified for example)
    private final Map<String, AcousticType> signatureLibrary = new HashMap<>();

    public AcousticPatternClassifier() {
        signatureLibrary.put("A93F", AcousticType.KNOWN_MICRO_DRONE);
        signatureLibrary.put("D44B", AcousticType.KNOWN_MINI_DRONE);
        signatureLibrary.put("FFFF", AcousticType.NOISE);
        signatureLibrary.put("BB29", AcousticType.BIRD);
        signatureLibrary.put("C8C8", AcousticType.VEHICLE);
    }

    /**
     * Classifies incoming FFT summary as acoustic type.
     */
    public AcousticType classify(String fftHash) {
        return signatureLibrary.getOrDefault(fftHash, AcousticType.UNKNOWN_DRONE);
    }

    /**
     * Returns true if the detected pattern is considered hostile or suspicious.
     */
    public boolean isThreat(AcousticType type) {
        return type == AcousticType.KNOWN_MICRO_DRONE ||
               type == AcousticType.KNOWN_MINI_DRONE ||
               type == AcousticType.UNKNOWN_DRONE;
    }

    /**
     * Adds or updates a new fingerprint to the library.
     */
    public void addFingerprint(String hash, AcousticType type) {
        signatureLibrary.put(hash, type);
    }

    /**
     * Removes all fingerprints (e.g. for retraining).
     */
    public void resetLibrary() {
        signatureLibrary.clear();
    }
}
