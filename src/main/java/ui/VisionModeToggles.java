package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.config.Config;

/**
 * 👁 VisionModeToggles - Allows operator to switch vision overlay modes globally.
 */
public class VisionModeToggles extends VBox {

    private final Label status = new Label("🟢 Vision mode: " + Config.VISION_MODE);

    public VisionModeToggles() {
        setSpacing(10);
        setPadding(new Insets(10));
        getChildren().add(new Label("👁 Vision Mode Toggle"));

        Button normalBtn = new Button("Normal");
        Button irBtn = new Button("Infrared");
        Button falconBtn = new Button("FalconVision");
        Button multiBtn = new Button("MultiView");

        normalBtn.setOnAction(e -> setMode("Normal"));
        irBtn.setOnAction(e -> setMode("Infrared"));
        falconBtn.setOnAction(e -> setMode("FalconVision"));
        multiBtn.setOnAction(e -> setMode("MultiView"));

        getChildren().addAll(normalBtn, irBtn, falconBtn, multiBtn, status);
    }

    private void setMode(String mode) {
        Config.VISION_MODE = mode;
        status.setText("✅ Vision mode set to: " + mode);
        System.out.println("👁 Vision mode updated: " + mode);
    }
}
