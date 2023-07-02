package p3.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * A scene for selecting which of the two algorithms should be displayed.
 * The scene contains two buttons, one for each algorithm.
 */

public class SelectionScene extends AnimationScene {
    final Button dijkstraButton = new Button("Dijkstra");
    final Button kruskalButton = new Button("Kruskal");

    public SelectionScene() {
        super(new BorderPane());
        BorderPane root = (BorderPane) getRoot();
        root.setPrefSize(700, 200);

        kruskalButton.setPrefSize(100, 50);
        dijkstraButton.setPrefSize(100, 50);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(kruskalButton, dijkstraButton);
        hBox.setAlignment(Pos.CENTER);

        root.setCenter(hBox);

        Label title = new Label("Select Which Algorithm To Animate");
        title.setStyle("-fx-font: 24 arial;");
        title.setPadding(new Insets(20, 0, 0 ,0));
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);
    }

    @Override
    public String getTitle() {
        return "Selection Scene";
    }
}
