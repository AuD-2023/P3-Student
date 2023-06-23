package p3.gui;

import javafx.scene.paint.Color;

/**
 * Settings class for changing the appearance of the displayed graph.
 */
public class GraphStyle {

    // --- Node --- //

    public static final double NODE_DIAMETER = 15;

    // --- Stroke --- //
    public static final int NODE_STROKE_WIDTH = 3;
    public static final int EDGE_STROKE_WIDTH = 2;

    // --- Grid --- //

    public static final float GRID_FIVE_TICKS_WIDTH = .125f;
    public static final float GRID_TEN_TICKS_WIDTH = .25f;
    public static final Color GRID_LINE_COLOR = Color.LIGHTGRAY;

    // --- Text --- //

    public static final Color TEXT_COLOR = Color.GRAY;

    // --- Default --- //

    public static final Color DEFAULT_NODE_COLOR = Color.DARKGRAY;
    public static final Color DEFAULT_EDGE_COLOR = Color.LIGHTGRAY;

    // --- Dijkstra --- //

    public static final Color DIJKSTRA_VISITED_NODE_STROKE_COLOR = Color.ORANGE;
    public static final Color DIJKSTRA_VISITED_NODE_FILL_COLOR = DEFAULT_NODE_COLOR;

    public static final Color DIJKSTRA_PREDECESSOR_EDGE = Color.ORANGE;
    public static final boolean DIJKSTRA_PREDECESSOR_EDGE_DASHED = true;
    public static final double DIJKSTRA_PREDECESSOR_EDGE_DASH_LENGTH = 50;
    public static final double DIJKSTRA_PREDECESSOR_EDGE_GAP_LENGTH = 10;

    public static final Color DIJKSTRA_CURRENT_NODE_STROKE_COLOR = Color.BLUE;
    public static final Color DIJKSTRA_CURRENT_NODE_FILL_COLOR = DEFAULT_NODE_COLOR;

    public static final Color DIJKSTRA_CURRENT_EDGE = Color.BLUE;
    public static final boolean DIJKSTRA_CURRENT_EDGE_DASHED = true;
    public static final double DIJKSTRA_CURRENT_EDGE_DASH_LENGTH = 50;
    public static final double DIJKSTRA_CURRENT_EDGE_GAP_LENGTH = 10;

    public static final Color DIJKSTRA_UNVISITED_NODE_STROKE_COLOR = DEFAULT_NODE_COLOR;
    public static final Color DIJKSTRA_UNVISITED_NODE_FILL_COLOR = DEFAULT_NODE_COLOR;

    public static final Color DIJKSTRA_UNVISITED_EDGE = DEFAULT_EDGE_COLOR;
    public static final boolean DIJKSTRA_UNVISITED_EDGE_DASHED = false;
    public static final double DIJKSTRA_UNVISITED_EDGE_DASH_LENGTH = 50;
    public static final double DIJKSTRA_UNVISITED_EDGE_GAP_LENGTH = 10;

    public static final Color DIJKSTRA_RESULT_NODE_STROKE_COLOR = Color.BLUE;
    public static final Color DIJKSTRA_RESULT_NODE_FILL_COLOR = DEFAULT_NODE_COLOR;

    public static final Color DIJKSTRA_RESULT_EDGE = Color.BLUE;
    public static final boolean DIJKSTRA_RESULT_EDGE_DASHED = true;
    public static final double DIJKSTRA_RESULT_EDGE_DASH_LENGTH = 50;
    public static final double DIJKSTRA_RESULT_EDGE_GAP_LENGTH = 10;

    // --- Kruskal --- //

    public static final Color KRUSKAL_ACCEPTED_EDGE = Color.BLUE;
    public static final boolean KRUSKAL_ACCEPTED_EDGE_DASHED = true;
    public static final double KRUSKAL_ACCEPTED_EDGE_DASH_LENGTH = 50;
    public static final double KRUSKAL_ACCEPTED_EDGE_GAP_LENGTH = 10;

    public static final Color KRUSKAL_REJECTED_EDGE = Color.ORANGE;
    public static final boolean KRUSKAL_REJECTED_EDGE_DASHED = true;
    public static final double KRUSKAL_REJECTED_EDGE_DASH_LENGTH = 50;
    public static final double KRUSKAL_REJECTED_EDGE_GAP_LENGTH = 10;

    public static final Color KRUSKAL_UNVISITED_EDGE = DEFAULT_EDGE_COLOR;
    public static final boolean KRUSKAL_UNVISITED_EDGE_DASHED = false;
    public static final double KRUSKAL_UNVISITED_EDGE_DASH_LENGTH = 50;
    public static final double KRUSKAL_UNVISITED_EDGE_GAP_LENGTH = 10;

    public static final Color KRUSKAL_RESULT_EDGE = Color.BLUE;
    public static final boolean KRUSKAL_RESULT_EDGE_DASHED = false;
    public static final double KRUSKAL_RESULT_EDGE_DASH_LENGTH = 50;
    public static final double KRUSKAL_RESULT_EDGE_GAP_LENGTH = 10;
}
