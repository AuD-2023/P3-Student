package p3.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * A pane for controlling the animation.
 * it contains a button for stepping through the animation and a button for centering the graph.
 */
public class ControlPane extends Pane {

    private final Button nextStepButton = new Button("Next Step");
    private final Button centerButton = new Button("Center Graph");

    /**
     * Constructs a new control pane.
     */
    public ControlPane() {
        setPadding(new Insets(5));
    }

    /**
     * Initializes the control pane.
     *
     * @param animation the animation to control
     * @param graphPane the graph pane to control
     */
    public void init(Animation animation, GraphPane<?> graphPane) {
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(nextStepButton, centerButton);
        getChildren().add(hBox);

        nextStepButton.setOnAction(event -> {
            synchronized (animation.getSyncObject()) {

                animation.getSyncObject().notify();
            }
        });

        centerButton.setOnAction(event -> graphPane.center());
    }

    public void disableNextStepButton() {
        nextStepButton.setDisable(true);
    }

}
