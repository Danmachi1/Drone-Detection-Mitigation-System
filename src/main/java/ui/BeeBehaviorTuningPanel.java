package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import logic.strategy.SwarmBehaviorModel;

/**
 * 🐝 BeeBehaviorTuningPanel – Lets an operator live-tune the parameters of the
 * {@link SwarmBehaviorModel}'s flocking algorithm (cohesion, separation,
 * alignment).  Sliders push new values to the shared model immediately.
 */
public class BeeBehaviorTuningPanel extends GridPane {

    private final SwarmBehaviorModel model;

    public BeeBehaviorTuningPanel(SwarmBehaviorModel model) {
        this.model = model;

        setPadding(new Insets(10));
        setHgap(10); setVgap(6);

        /* Build three sliders */
        addRow(0, label("Cohesion"),   slider(0,1, model.getCohesion()));
        addRow(1, label("Separation"), slider(0,20, model.getSeparationDistance()));
        addRow(2, label("Alignment"),  slider(0,1, model.getAlignment()));

        setStyle("-fx-border-color:#888; -fx-border-radius:6; -fx-border-width:1;");
    }

    /* ---------- helpers ----------- */
    private Label label(String txt){ return new Label(txt+ ":"); }

    private Slider slider(double min,double max,double init){
        Slider s = new Slider(min,max,init);
        s.setShowTickLabels(true); s.setShowTickMarks(true); s.setBlockIncrement((max-min)/20);
        s.valueProperty().addListener((obs,oldV,newV)->updateModel());
        return s;
    }

    private void updateModel(){
        double cohes = ((Slider)getChildren().get(1)).getValue();
        double sep   = ((Slider)getChildren().get(3)).getValue();
        double align = ((Slider)getChildren().get(5)).getValue();
        model.setCohesion(cohes);
        model.setSeparationDistance(sep);
        model.setAlignment(align);
    }
}
