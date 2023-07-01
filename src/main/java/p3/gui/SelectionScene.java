package p3.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * A scene for selecting which of the two algorithms should be displayed.
 * The scene contains two buttons, one for each algorithm.
 */

public class SelectionScene extends AnimationScene {
    final Button dijkstraButton = new Button("Dijkstra");
    final Button kruskalButton = new Button("Kruskal");

    public SelectionScene() {
        super(new StackPane());
        StackPane root = (StackPane) getRoot();
        root.setPrefSize(700, 700);

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(kruskalButton, dijkstraButton);
        vBox.setAlignment(Pos.CENTER);

        root.getChildren().add(vBox);
    }

    @Override
    public String getTitle() {
        return "Selection Scene";
    }
}
