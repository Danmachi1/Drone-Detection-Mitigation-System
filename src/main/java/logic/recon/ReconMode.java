package logic.recon;

/**
 * 🧬 ReconMode - Enum representing bio-inspired recon sweep strategies.
 */
public enum ReconMode {
    BEE_ROLES,
    FALCON_VISION,
    BAT_ECHO,
    SHARK_SCAN,
    ANT_SWARM;

    /**
     * Returns the matching ReconMode enum from a user-friendly name.
     */
    public static ReconMode fromName(String name) {
        switch (name.toUpperCase().replace(" ", "_")) {
            case "BEEROLES", "BEE_ROLES" -> {
                return BEE_ROLES;
            }
            case "FALCONVISION", "FALCON_VISION" -> {
                return FALCON_VISION;
            }
            case "BATECHO", "BAT_ECHO" -> {
                return BAT_ECHO;
            }
            case "SHARKSCAN", "SHARK_SCAN" -> {
                return SHARK_SCAN;
            }
            case "ANTSWARM", "ANT_SWARM" -> {
                return ANT_SWARM;
            }
            default -> throw new IllegalArgumentException("Unknown Recon Mode: " + name);
        }
    }

    @Override
    public String toString() {
        return switch (this) {
            case BEE_ROLES -> "BeeRoles";
            case FALCON_VISION -> "FalconVision";
            case BAT_ECHO -> "BatEcho";
            case SHARK_SCAN -> "SharkScan";
            case ANT_SWARM -> "AntSwarm";
        };
    }
}
