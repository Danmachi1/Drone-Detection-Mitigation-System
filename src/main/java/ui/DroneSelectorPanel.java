package ui;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import logic.swarm.DroneAgent;
import logic.swarm.SwarmManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 🎯 DroneSelectorPanel – ComboBox listing every drone so other UI panes
 * (map overlay, video follow-cam) can know the current operator focus.
 *
 * External code can register a {@link SelectionListener} to be notified
 * whenever the selection changes.
 */
public class DroneSelectorPanel extends VBox {

    public interface SelectionListener { void onSelect(DroneAgent agent); }

    private final SwarmManager swarm;
    private final ComboBox<String> combo = new ComboBox<>();
    private final Label info = new Label();
    private final List<SelectionListener> listeners = new ArrayList<>();

    public DroneSelectorPanel(SwarmManager swarm) {
        this.swarm = swarm;

        setPadding(new Insets(10));
        setSpacing(6);

        combo.setPromptText("Choose drone…");
        combo.setOnAction(e -> updateInfo());

        getChildren().addAll(new Label("🎯 Drone Selector"), combo, info);

        refreshList();
    }

    /* ---------------------------------------------------------------- */

    private void refreshList() {
        combo.getItems().clear();
        for (DroneAgent d : swarm.getSwarm()) combo.getItems().add(d.getId());
    }

    private void updateInfo() {
        String id = combo.getValue();
        DroneAgent d = swarm.getById(id);
        if (d == null) { info.setText("–"); return; }

        info.setText("Role: " + d.getRole() +
                     " | Battery: " + String.format("%.0f", d.getBatteryLevel()) + "%");

        /* notify listeners */
        for (SelectionListener l : listeners) l.onSelect(d);
    }

    /* Listener registration ----------------------------------------- */

    public void addListener(SelectionListener l) { listeners.add(l); }
    public void removeListener(SelectionListener l) { listeners.remove(l); }
}
