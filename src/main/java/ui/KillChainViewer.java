				package ui;
				
				import javafx.application.Platform;
				import javafx.scene.control.Label;
				import javafx.scene.layout.*;
				import javafx.scene.paint.Color;
				import logic.engage.EngagementManager;
				
				import java.util.HashMap;
				import java.util.Map;
				import java.util.Timer;
				import java.util.TimerTask;
				
				/**
				 * 🧬 KillChainViewer - Displays the full status of each threat along the kill chain.
				 * Useful for diagnostics, overrides, and operator trust.
				 */
				public class KillChainViewer extends VBox {
				
				    private final Map<String, HBox> threatStages = new HashMap<>();
				    private final EngagementManager engagementManager;
				
				    public KillChainViewer(EngagementManager manager) {
				        this.engagementManager = manager;
				        setSpacing(10);
				        getChildren().add(new Label("🔗 Kill Chain Status"));
				
				        // Auto-refresh every 2s
				        Timer timer = new Timer(true);
				        timer.schedule(new TimerTask() {
				            public void run() {
				                Platform.runLater(() -> refresh());
				            }
				        }, 0, 2000);
				    }
				
				    private void refresh() {
				        getChildren().removeIf(node -> node instanceof HBox);
				        threatStages.clear();
				
				        Map<String, String[]> chains = engagementManager.getKillChainStatus(); // Example: {threatId: ["DETECT", "CLASSIFY", ...]}
				        for (Map.Entry<String, String[]> entry : chains.entrySet()) {
				            String id = entry.getKey();
				            String[] phases = entry.getValue();
				
				            HBox row = new HBox(5);
				            Label idLabel = new Label("🛰️ " + id);
				            idLabel.setMinWidth(80);
				            row.getChildren().add(idLabel);
				
				            for (String phase : phases) {
				                Label stage = new Label(phase);
				                stage.setTextFill(getColor(phase));
				                stage.setStyle("-fx-border-color: white; -fx-padding: 2 6;");
				                row.getChildren().add(stage);
				            }
				
				            threatStages.put(id, row);
				            getChildren().add(row);
				        }
				    }
				
				    private Color getColor(String phase) {
				        return switch (phase.toUpperCase()) {
				            case "DETECT" -> Color.LIGHTBLUE;
				            case "CLASSIFY" -> Color.ORANGE;
				            case "TRACK" -> Color.GOLD;
				            case "DECIDE" -> Color.YELLOW;
				            case "ENGAGE" -> Color.RED;
				            case "CONFIRM" -> Color.LIMEGREEN;
				            default -> Color.GRAY;
				        };
				    }
				}
