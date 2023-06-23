package p3.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import p3.graph.Graph;
import p3.graph.MutableGraph;
import p3.gui.dijkstra.DijkstraScene;
import p3.gui.kruskal.KruskalScene;

import java.util.Map;

/**
 * Main entry point in executing the GUI.
 */
public class MyApplication extends Application {

    @Override
    public void start(Stage primaryStage) {

        AnimationScene scene = createKruskalScene();

        primaryStage.setScene(scene);
        primaryStage.setTitle(scene.getTitle());

        primaryStage.show();
    }

    /**
     * Creates a scene to visualize Dijkstra's algorithm.
     * @return a scene to visualize Dijkstra's algorithm.
     */
    private static AnimationScene createDijkstraScene() {
        Graph<String> graph = createGraph();

        DijkstraScene<String> scene = new DijkstraScene<>();
        scene.init(graph, Map.of(
            "A", new Location(1, 2),
            "B", new Location(0, 20),
            "C", new Location(-30, -20),
            "D", new Location(30, -10),
            "E", new Location(45, -5),
            "F", new Location(50, 10)), "A", "F");

        return scene;
    }

    /**
     * Creates a scene to visualize Kruskal's algorithm.
     * @return a scene to visualize Kruskal's algorithm.
     */
    private static AnimationScene createKruskalScene() {
        Graph<String> graph = createGraph();

        KruskalScene<String> scene = new KruskalScene<>();
        scene.init(graph, Map.of(
            "A", new Location(1, 2),
            "B", new Location(0, 20),
            "C", new Location(-30, -20),
            "D", new Location(30, -10),
            "E", new Location(45, -5),
            "F", new Location(50, 10)));
        return scene;
    }

    /**
     * Creates the graph to visualize.
     * @return the graph to visualize.
     */
    private static Graph<String> createGraph() {
        return MutableGraph.<String>of()
            .putEdgesAndNodes("A", "B", 4)
            .putEdgesAndNodes("A", "C", 1)
            .putEdgesAndNodes("B", "C", 3)
            .putEdgesAndNodes("B", "D", 5)
            .putEdgesAndNodes("D", "E", 1)
            .putEdgesAndNodes("E", "F", 3)
            .putEdgesAndNodes("D", "F", 2)
            .putEdgesAndNodes("C", "D", 10)
            .toGraph();
    }
}
