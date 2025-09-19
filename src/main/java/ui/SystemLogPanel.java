package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * 📜 SystemLogPanel – Displays a live-updating log output from the backend.
 * Captures key messages, system boot status, and runtime feedback.
 */
public class SystemLogPanel extends VBox {

    private static TextArea logArea;

    public SystemLogPanel() {
        setPadding(new Insets(10));
        setSpacing(6);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(700);

        getChildren().add(logArea);
    }

    /** Call this from any backend logic to append a new log line to the UI. */
    public static void append(String message) {
        if (logArea == null) return;
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
        });
    }
}
