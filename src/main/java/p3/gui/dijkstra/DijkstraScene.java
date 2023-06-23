package p3.gui.dijkstra;

import javafx.scene.layout.BorderPane;
import p3.graph.Edge;
import p3.graph.Graph;
import p3.gui.AnimationScene;
import p3.gui.ControlPane;
import p3.gui.GraphPane;
import p3.gui.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static p3.gui.GraphStyle.DIJKSTRA_CURRENT_EDGE;
import static p3.gui.GraphStyle.DIJKSTRA_CURRENT_EDGE_DASHED;
import static p3.gui.GraphStyle.DIJKSTRA_CURRENT_EDGE_DASH_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_CURRENT_EDGE_GAP_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_CURRENT_NODE_FILL_COLOR;
import static p3.gui.GraphStyle.DIJKSTRA_CURRENT_NODE_STROKE_COLOR;
import static p3.gui.GraphStyle.DIJKSTRA_PREDECESSOR_EDGE;
import static p3.gui.GraphStyle.DIJKSTRA_PREDECESSOR_EDGE_DASHED;
import static p3.gui.GraphStyle.DIJKSTRA_PREDECESSOR_EDGE_DASH_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_PREDECESSOR_EDGE_GAP_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_RESULT_EDGE;
import static p3.gui.GraphStyle.DIJKSTRA_RESULT_EDGE_DASHED;
import static p3.gui.GraphStyle.DIJKSTRA_RESULT_EDGE_DASH_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_RESULT_EDGE_GAP_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_RESULT_NODE_FILL_COLOR;
import static p3.gui.GraphStyle.DIJKSTRA_RESULT_NODE_STROKE_COLOR;
import static p3.gui.GraphStyle.DIJKSTRA_UNVISITED_EDGE;
import static p3.gui.GraphStyle.DIJKSTRA_UNVISITED_EDGE_DASHED;
import static p3.gui.GraphStyle.DIJKSTRA_UNVISITED_EDGE_DASH_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_UNVISITED_EDGE_GAP_LENGTH;
import static p3.gui.GraphStyle.DIJKSTRA_UNVISITED_NODE_FILL_COLOR;
import static p3.gui.GraphStyle.DIJKSTRA_UNVISITED_NODE_STROKE_COLOR;
import static p3.gui.GraphStyle.DIJKSTRA_VISITED_NODE_FILL_COLOR;
import static p3.gui.GraphStyle.DIJKSTRA_VISITED_NODE_STROKE_COLOR;

/**
 * A scene for displaying the Dijkstra algorithm.
 *
 * @param <N> the type of the nodes
 */
public class DijkstraScene<N> extends AnimationScene {

    private final BorderPane root;

    private GraphPane<N> graphPane;
    private Graph<N> graph;
    private DijkstraInfoPane<N> infoPane;
    private AnimatedDijkstraPathCalculator<N> animation;

    private final List<N> visitedNodes = new ArrayList<>();
    private ControlPane controlPane;

    public DijkstraScene() {
        super(new BorderPane());
        root = (BorderPane) getRoot();
        root.setPrefSize(700, 700);
    }

    public void init(Graph<N> graph, Map<N, Location> nodeLocations, N start, N end) {
        this.graph = graph;

        graphPane = new GraphPane<>(graph, nodeLocations);
        root.setCenter(graphPane);


        animation = new AnimatedDijkstraPathCalculator<>(graph, this, start, end);

        infoPane = new DijkstraInfoPane<>(graph, animation);
        root.setRight(infoPane);

        controlPane = new ControlPane();
        controlPane.init(animation, graphPane);
        root.setBottom(controlPane);

        new Thread(() -> animation.start()).start();
    }

    public void refresh(Edge<N> visitedEdge, N viaNode) {
        if (visitedEdge != null) {

            for (Edge<N> edge : graph.getEdges()) {
                if (edge.equals(visitedEdge)) {
                    graphPane.setEdgeColor(edge, DIJKSTRA_CURRENT_EDGE);
                    graphPane.setEdgeDash(edge, DIJKSTRA_CURRENT_EDGE_DASHED, DIJKSTRA_CURRENT_EDGE_DASH_LENGTH, DIJKSTRA_CURRENT_EDGE_GAP_LENGTH);
                } else if (Objects.equals(animation.getPredecessorNode(edge.a()), edge.b()) ||
                    Objects.equals(animation.getPredecessorNode(edge.b()), edge.a())) {
                    graphPane.setEdgeColor(edge, DIJKSTRA_PREDECESSOR_EDGE);
                    graphPane.setEdgeDash(edge, DIJKSTRA_PREDECESSOR_EDGE_DASHED, DIJKSTRA_PREDECESSOR_EDGE_DASH_LENGTH, DIJKSTRA_PREDECESSOR_EDGE_GAP_LENGTH);
                } else {
                    graphPane.setEdgeColor(edge, DIJKSTRA_UNVISITED_EDGE);
                    graphPane.setEdgeDash(edge, DIJKSTRA_UNVISITED_EDGE_DASHED, DIJKSTRA_UNVISITED_EDGE_DASH_LENGTH, DIJKSTRA_UNVISITED_EDGE_GAP_LENGTH);
                }
            }

            for (N node : graph.getNodes()) {
                if (node.equals(viaNode)) {
                    graphPane.setNodeStrokeColor(node, DIJKSTRA_CURRENT_NODE_STROKE_COLOR);
                    graphPane.setNodeFillColor(node, DIJKSTRA_CURRENT_NODE_FILL_COLOR);
                } else if (visitedNodes.contains(node)) {
                    graphPane.setNodeStrokeColor(node, DIJKSTRA_VISITED_NODE_STROKE_COLOR);
                    graphPane.setNodeFillColor(node, DIJKSTRA_VISITED_NODE_FILL_COLOR);
                } else {
                    graphPane.setNodeStrokeColor(node, DIJKSTRA_UNVISITED_NODE_STROKE_COLOR);
                    graphPane.setNodeFillColor(node, DIJKSTRA_UNVISITED_NODE_FILL_COLOR);
                }
            }
        } else {
            for (Edge<N> edge : graph.getEdges()) {
                graphPane.setEdgeColor(edge, DIJKSTRA_UNVISITED_EDGE);
                graphPane.setEdgeDash(edge, DIJKSTRA_UNVISITED_EDGE_DASHED, DIJKSTRA_UNVISITED_EDGE_DASH_LENGTH, DIJKSTRA_UNVISITED_EDGE_GAP_LENGTH);
            }

            for (N node : graph.getNodes()) {
                graphPane.setNodeStrokeColor(node, DIJKSTRA_UNVISITED_NODE_STROKE_COLOR);
                graphPane.setNodeFillColor(node, DIJKSTRA_UNVISITED_NODE_FILL_COLOR);
            }
        }

        visitedNodes.add(viaNode);

        infoPane.refresh();
    }

    public void showResult(List<N> path) {
        for (Edge<N> edge : graph.getEdges()) {
            graphPane.setEdgeColor(edge, DIJKSTRA_UNVISITED_EDGE);
            graphPane.setEdgeDash(edge, DIJKSTRA_UNVISITED_EDGE_DASHED, DIJKSTRA_UNVISITED_EDGE_DASH_LENGTH, DIJKSTRA_UNVISITED_EDGE_GAP_LENGTH);
        }

        for (N node : graph.getNodes()) {
            graphPane.setNodeStrokeColor(node, DIJKSTRA_UNVISITED_NODE_STROKE_COLOR);
            graphPane.setNodeFillColor(node, DIJKSTRA_UNVISITED_NODE_FILL_COLOR);
        }

        for (int i = 0; i < path.size() - 1; i++) {
            N node = path.get(i);
            N nextNode = path.get(i + 1);

            Edge<N> edge = graph.getAdjacentEdges(node).stream()
                .filter(e -> e.a().equals(nextNode) || e.b().equals(nextNode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No edge between " + node + " and " + nextNode + " found"));

            graphPane.setEdgeColor(edge, DIJKSTRA_RESULT_EDGE);
            graphPane.setEdgeDash(edge, DIJKSTRA_RESULT_EDGE_DASHED, DIJKSTRA_RESULT_EDGE_DASH_LENGTH, DIJKSTRA_RESULT_EDGE_GAP_LENGTH);

            graphPane.setNodeStrokeColor(node, DIJKSTRA_RESULT_NODE_STROKE_COLOR);
            graphPane.setNodeFillColor(node, DIJKSTRA_RESULT_NODE_FILL_COLOR);
        }

        graphPane.setNodeStrokeColor(path.get(path.size() - 1), DIJKSTRA_RESULT_NODE_STROKE_COLOR);
        graphPane.setNodeFillColor(path.get(path.size() - 1), DIJKSTRA_RESULT_NODE_FILL_COLOR);

        controlPane.disableNextStepButton();
    }

    public String getTitle() {
        return "Dijkstra Animation";
    }
}
