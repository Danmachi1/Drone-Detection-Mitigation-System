package ui;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import logic.ai.AIManager;
import main.config.UIModeConstants;
import main.config.Config;

/**
 * 🤖 AIModeSelectionPanel – Simple VBox that lets an operator switch between
 * AI control strategies at run-time.  It updates {@link Config#AI_MODE} and
 * notifies {@link AIManager} of the change.
 */
public class AIModeSelectionPanel extends VBox {

    private final Label currentLabel;

    public AIModeSelectionPanel() {
        setPadding(new Insets(10));
        setSpacing(8);

        currentLabel = new Label();
        currentLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<String> combo = buildCombo();

        getChildren().addAll(currentLabel, combo);
        updateLabel(UIModeConstants.AI_MODE);
    }

    /* Build the combo box with all supported modes */
    private ComboBox<String> buildCombo() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(
                "Offline", "Live", "Hybrid",
                "Rule-Based", "Decision-Tree", "Neural", "Failsafe"
        );
        combo.setValue(UIModeConstants.AI_MODE);

        combo.setOnAction(e -> {
            String selected = combo.getValue();
            UIModeConstants.AI_MODE = selected;      // runtime global
            AIManager.onModeChange(selected);
            updateLabel(selected);
        });
        return combo;
    }

    private void updateLabel(String mode) {
        currentLabel.setText("🧠 Current AI Mode: " + mode);
    }
}
