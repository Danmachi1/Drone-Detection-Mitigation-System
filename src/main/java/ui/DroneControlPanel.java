package ui;

import javafx.application.Platform;

import logic.mission.MissionProfile;   // <-
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logic.swarm.DroneAgent;
import logic.swarm.SwarmManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 🚁 DroneControlPanel – Displays every drone, its role, and battery.
 * Lets the operator reset a drone or abort its mission.
 */
public class DroneControlPanel extends VBox {

    private static final int REFRESH_MS = 2_000;

    private final SwarmManager swarm;
    private final VBox         droneList = new VBox(4);
    private final Timer        timer     = new Timer("drone-panel", true);

    public DroneControlPanel(SwarmManager swarm) {
        this.swarm = swarm;

        setPadding(new Insets(10));
        setSpacing(8);
        getChildren().addAll(new Label("🚁 Drone Control"), droneList);

        /* periodic refresh */
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() { Platform.runLater(DroneControlPanel.this::refresh); }
        }, 0, REFRESH_MS);
    }

    /* ------------------------------------------------------------ */

    private void refresh() {
        droneList.getChildren().clear();

        for (DroneAgent drone : swarm.getAllDrones()) {

            HBox row  = new HBox(6);
            Label lbl = new Label(drone.getId());

            /* ---------- action buttons ---------- */
            Button launch = new Button("Launch");
            launch.setOnAction(e ->
                swarm.assignDroneMission(drone.getId(), MissionProfile.DEFAULT));

            Button land = new Button("Land");
            land.setOnAction(e -> swarm.cancelMission(drone.getId()));

            Button rth = new Button("Return-Home");
            rth.setOnAction(e -> swarm.returnToBase(drone.getId()));


            Button reset = new Button("Reset");
            reset.setOnAction(e -> swarm.resetDrone(drone.getId()));

            Button abort = new Button("Abort");
            abort.setOnAction(e -> swarm.abortMission(drone.getId()));

            row.getChildren().addAll(lbl, launch, land, rth, reset, abort);
            droneList.getChildren().add(row);     // ✅ was rowsVBox – fixed
        }
        


    }

   

    /** Stop the refresh timer; call when window closes. */
    public void shutdown() { timer.cancel(); }
    
}
