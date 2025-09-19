package logic.ai;

import main.config.UIModeConstants;

/**
 * Runtime keeper of the selected AI mode.
 * A tiny class now, but ready for observers / events later.
 */
public final class AIManager {

    private static String currentMode = UIModeConstants.AI_MODE;

    private AIManager() {}

    public static String getMode() {
        return currentMode;
    }

    public static void setMode(String mode) {
        if (mode != null && !mode.isBlank()) {
            currentMode = mode;
        }
    }
    public static void onModeChange(String newMode) {
        setMode(newMode);
        System.out.println("🧠 AI Mode changed to: " + newMode);
        // You could later notify observers here (e.g., using listeners or event bus)
    }

}
