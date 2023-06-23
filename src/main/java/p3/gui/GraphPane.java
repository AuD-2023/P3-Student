package p3.gui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import p3.graph.Edge;
import p3.graph.Graph;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static p3.gui.GraphStyle.DEFAULT_EDGE_COLOR;
import static p3.gui.GraphStyle.DEFAULT_NODE_COLOR;
import static p3.gui.GraphStyle.EDGE_STROKE_WIDTH;
import static p3.gui.GraphStyle.GRID_FIVE_TICKS_WIDTH;
import static p3.gui.GraphStyle.GRID_LINE_COLOR;
import static p3.gui.GraphStyle.GRID_TEN_TICKS_WIDTH;
import static p3.gui.GraphStyle.NODE_DIAMETER;
import static p3.gui.GraphStyle.NODE_STROKE_WIDTH;
import static p3.gui.GraphStyle.TEXT_COLOR;

/**
 * A {@link Pane} that displays a {@link Graph}.
 *
 * @param <N> the type of the nodes in the graph
 */
@SuppressWarnings("unused")
public class GraphPane<N> extends Pane {

    private static final double SCALE_IN = 1.1;
    private static final double SCALE_OUT = 1 / SCALE_IN;
    private static final double MAX_SCALE = 100;
    private static final double MIN_SCALE = 3;

    private final AtomicReference<Point2D> lastPoint = new AtomicReference<>();
    private AffineTransform transformation = new AffineTransform();

    private final Text positionText = new Text();

    private final Map<LocationNode<N>, LabeledNode> nodes = new HashMap<>();
    private final Map<N, LocationNode<N>> nodeLocations = new HashMap<>();
    private final Map<Edge<LocationNode<N>>, LabeledEdge> edges = new HashMap<>();

    private final List<Node> grid = new ArrayList<>();

    private boolean alreadyCentered = false;

    /**
     * Creates a new, empty {@link GraphPane}.
     */
    public GraphPane() {
        this(List.of(), List.of());
    }

    /**
     * Creates a new {@link GraphPane} and displays the given {@link Graph}.
     * The nodes are displayed at the given locations.
     *
     * @param graph         the {@link Graph} to display.
     * @param nodeLocations the locations of the nodes in the graph.
     */
    public GraphPane(Graph<N> graph, Map<N, Location> nodeLocations) {
        this(graph.getNodes().stream().map(node -> new LocationNode<>(node, nodeLocations.get(node))).toList(),
            graph.getEdges().stream().map(edge -> Edge.of(
                new LocationNode<>(edge.a(), nodeLocations.get(edge.a())),
                new LocationNode<>(edge.b(), nodeLocations.get(edge.b())),
                edge.weight())
            ).toList());
    }

    /**
     * Creates a new {@link GraphPane}, displays the given components and centers itself.
     *
     * @param nodes The {@linkplain LocationNode nodes} to display.
     * @param edges The {@linkplain Edge edges} to display.
     */
    private GraphPane(Collection<? extends LocationNode<N>> nodes,
                      Collection<? extends Edge<LocationNode<N>>> edges) {

        // avoid division by zero when scale = 1
        transformation.scale(MIN_SCALE, MIN_SCALE);

        for (Edge<LocationNode<N>> edge : edges) {
            addEdge(edge);
        }

        for (LocationNode<N> node : nodes) {
            addNode(node);
        }

        initListeners();
        drawGrid();
        drawPositionText();
        positionText.setFill(TEXT_COLOR);
        center();
    }

    // --- Edge Handling --- //

    /**
     * Adds an {@linkplain Edge edge} to this {@link GraphPane} and displays it.
     *
     * @param edge The {@linkplain Edge edge} to display.
     */
    public void addEdge(Edge<LocationNode<N>> edge) {
        edges.put(edge, drawEdge(edge));
    }

    /**
     * Adds the {@linkplain Edge edges} to this {@link GraphPane} and displays them.
     *
     * @param edges The {@linkplain Edge edges} to display.
     */
    public void addAllEdges(Collection<? extends Edge<LocationNode<N>>> edges) {
        for (Edge<LocationNode<N>> edge : edges) {
            addEdge(edge);
        }
    }

    /**
     * Removes the given {@linkplain Edge edge} from this {@link GraphPane}.
     * The {@linkplain Edge edge} will not be displayed after anymore calling this method.
     * If the given {@linkplain Edge edge} is not part of this {@linkplain GraphPane} the method does nothing.
     *
     * @param edge The {@linkplain Edge edge} to remove.
     */
    public void removeEdge(Edge<LocationNode<N>> edge) {
        LabeledEdge labeledEdge = edges.remove(edge);

        if (labeledEdge != null) {
            getChildren().removeAll(labeledEdge.line(), labeledEdge.text());
        }
    }

    /**
     * Updates the color used to draw the given {@linkplain Edge edge}.
     *
     * @param edge  The {@linkplain Edge edge} to update.
     * @param color The new color.
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void setEdgeColor(Edge<N> edge, Color color) {
        getLabeledEdge(edge).setStrokeColor(color);
    }

    /**
     * Updates the dashing settings of the stroke used to draw the given {@linkplain Edge edge}.
     *
     * @param edge       The {@linkplain Edge edge} to update.
     * @param dash       Whether to use dashing or not.
     * @param dashLength The length of the individual dashes.
     * @param gapLength  The length of the gaps between the dashes.
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void setEdgeDash(Edge<N> edge, boolean dash, double dashLength, double gapLength) {
        LabeledEdge labeledEdge = getLabeledEdge(edge);

        labeledEdge.line().getStrokeDashArray().clear();

        if (dash) {
            labeledEdge.line().getStrokeDashArray().addAll(dashLength, gapLength);
        }
    }

    /**
     * Resets the color used to draw the given {@linkplain Edge edge} to the default color ({@link GraphStyle#DEFAULT_EDGE_COLOR}).
     *
     * @param edge The {@linkplain Edge edge} to update.
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void resetEdgeColor(Edge<N> edge) {
        setEdgeColor(edge, DEFAULT_EDGE_COLOR);
    }

    /**
     * Updates the position of all {@linkplain Edge edges} on this {@link GraphPane}.
     */
    public void redrawEdges() {
        for (Edge<LocationNode<N>> edge : edges.keySet()) {
            redrawEdge(edge);
        }
    }

    /**
     * Updates the position of the given {@linkplain Edge edge}.
     *
     * @param edge The {@linkplain Edge edge} to update.
     * @throws IllegalArgumentException If the given {@linkplain Edge edge} is not part of this {@link GraphPane}.
     */
    public void redrawEdge(Edge<LocationNode<N>> edge) {
        if (!edges.containsKey(edge)) {
            throw new IllegalArgumentException("The given edge is not part of this GraphPane");
        }

        Point2D transformedMidPoint = transform(midPoint(edge));
        Point2D transformedPointA = transform(edge.a().location());
        Point2D transformedPointB = transform(edge.b().location());

        LabeledEdge labeledEdge = edges.get(edge);

        labeledEdge.line().setStartX(transformedPointA.getX());
        labeledEdge.line().setStartY(transformedPointA.getY());

        labeledEdge.line().setEndX(transformedPointB.getX());
        labeledEdge.line().setEndY(transformedPointB.getY());

        labeledEdge.text().setX(transformedMidPoint.getX());
        labeledEdge.text().setY(transformedMidPoint.getY());
    }

    // --- Node Handling --- //

    /**
     * Adds a {@linkplain LocationNode node} to this {@link GraphPane} and displays it.
     *
     * @param node The {@linkplain LocationNode node} to display.
     */
    public void addNode(LocationNode<N> node) {
        nodes.put(node, drawNode(node));
        nodeLocations.put(node.value(), node);
    }

    /**
     * Adds the {@linkplain LocationNode nodes} to this {@link GraphPane} and displays them.
     *
     * @param nodes The {@linkplain LocationNode nodes} to display.
     */
    public void addAllNodes(Collection<? extends LocationNode<N>> nodes) {
        for (LocationNode<N> node : nodes) {
            addNode(node);
        }
    }

    /**
     * Removes the given {@linkplain LocationNode node} from this {@link GraphPane}.<p>
     * {@linkplain Edge edge}s connected to the removed {@linkplain LocationNode node} will not get removed.
     * The {@linkplain LocationNode node} will not be displayed after anymore calling this method.
     * If the given {@linkplain LocationNode node} is not part of this {@linkplain GraphPane} the method does nothing.
     *
     * @param node The {@linkplain LocationNode node} to remove.
     */
    public void removeNode(LocationNode<N> node) {
        LabeledNode labeledNode = nodes.remove(node);

        if (labeledNode != null) {
            getChildren().removeAll(labeledNode.ellipse(), labeledNode.text());
        }
    }

    /**
     * Updates the color used to draw the circumference of given {@linkplain N node}.
     *
     * @param node  The {@linkplain N node} to update.
     * @param color The new color.
     * @throws IllegalArgumentException If the given {@linkplain N node} is not part of this {@link GraphPane}.
     */
    public void setNodeStrokeColor(N node, Color color) {
        LabeledNode labeledNode = nodes.get(new LocationNode<>(node, nodeLocations.get(node).location()));

        if (labeledNode == null) {
            throw new IllegalArgumentException("The given node is not part of this GraphPane");
        }

        labeledNode.setStrokeColor(color);
    }

    /**
     * Updates the color used to fill the given {@linkplain N node}.
     *
     * @param node  The {@linkplain N node} to update.
     * @param color The new color.
     * @throws IllegalArgumentException If the given {@linkplain N node} is not part of this {@link GraphPane}.
     */
    public void setNodeFillColor(N node, Color color) {
        LabeledNode labeledNode = nodes.get(new LocationNode<>(node, nodeLocations.get(node).location()));

        if (labeledNode == null) {
            throw new IllegalArgumentException("The given node is not part of this GraphPane");
        }

        labeledNode.setFillColor(color);
    }

    /**
     * Resets the color used to draw the given {@linkplain N node} to the default color ({@link GraphStyle#DEFAULT_NODE_COLOR}).
     *
     * @param node The {@linkplain N node} to update.
     * @throws IllegalArgumentException If the given {@linkplain N node} is not part of this {@link GraphPane}.
     */
    public void resetNodeColor(N node) {
        setNodeStrokeColor(node, DEFAULT_NODE_COLOR);
    }

    /**
     * Updates the position of all {@linkplain LocationNode nodes} on this {@link GraphPane}.
     */
    public void redrawNodes() {
        for (LocationNode<N> node : nodes.keySet()) {
            redrawNode(node);
        }
    }

    /**
     * Updates the position of the given {@linkplain LocationNode node}.
     *
     * @param node The {@linkplain LocationNode node} to update.
     * @throws IllegalArgumentException If the given {@linkplain LocationNode node} is not part of this {@link GraphPane}.
     */
    public void redrawNode(LocationNode<N> node) {
        if (!nodes.containsKey(node)) {
            throw new IllegalArgumentException("The given node is not part of this GraphPane");
        }

        Point2D transformedMidPoint = transform(midPoint(node));

        LabeledNode labeledNode = nodes.get(node);

        labeledNode.ellipse().setCenterX(transformedMidPoint.getX());
        labeledNode.ellipse().setCenterY(transformedMidPoint.getY());

        labeledNode.text().setX(transformedMidPoint.getX() + NODE_DIAMETER);
        labeledNode.text().setY(transformedMidPoint.getY());
    }

    // --- Other Util --- //

    /**
     * Removes all components from this {@link GraphPane}.
     */
    public void clear() {
        for (LocationNode<N> node : new HashSet<>(nodes.keySet())) {
            removeNode(node);
        }

        for (Edge<LocationNode<N>> edge : new HashSet<>(edges.keySet())) {
            removeEdge(edge);
        }
    }

    /**
     * Updates the position of all components on this {@link GraphPane}.
     */
    public void redrawMap() {
        redrawNodes();
        redrawEdges();
    }

    /**
     * Tries to center this {@link GraphPane} as good as possible such that each node is visible while keeping the zoom factor as high as possible.
     */
    public void center() {

        if (getHeight() == 0.0 || getWidth() == 0.0) {
            return;
        }

        if (nodes.isEmpty()) {
            transformation.scale(20, 20);
            redrawGrid();
            return;
        }

        double maxX = nodes.keySet().stream().map(node -> node.location().x())
            .collect(new ComparingCollector<Integer>(Comparator.naturalOrder()));

        double maxY = nodes.keySet().stream().map(node -> node.location().y())
            .collect(new ComparingCollector<Integer>(Comparator.naturalOrder()));

        double minX = nodes.keySet().stream().map(node -> node.location().x())
            .collect(new ComparingCollector<Integer>(Comparator.reverseOrder()));

        double minY = nodes.keySet().stream().map(node -> node.location().y())
            .collect(new ComparingCollector<Integer>(Comparator.reverseOrder()));

        if (minX == maxX) {
            minX = minX - 1;
            maxX = maxX + 1;
        }

        if (minY == maxY) {
            minY = minY - 1;
            maxY = maxY + 1;
        }

        AffineTransform reverse = new AffineTransform();

        reverse.setToTranslation(minX, minY);
        reverse.scale(1.25 * (maxX - minX) / getWidth(), 1.25 * (maxY - minY) / getHeight());
        reverse.translate(-Math.abs(0.125 * reverse.getTranslateX()) / reverse.getScaleX(), -Math.abs(0.125 * reverse.getTranslateY()) / reverse.getScaleY());

        transformation = reverse;
        transformation = getReverseTransform();

        redrawGrid();
        redrawMap();

        alreadyCentered = true;
    }

    // --- Private Methods --- //

    private void initListeners() {

        setOnMouseDragged(actionEvent -> {
                Point2D point = new Point2D.Double(actionEvent.getX(), actionEvent.getY());
                Point2D diff = getDifference(point, lastPoint.get());

                transformation.translate(diff.getX() / transformation.getScaleX(), diff.getY() / transformation.getScaleY());

                redrawMap();
                redrawGrid();
                updatePositionText(point);

                lastPoint.set(point);
            }
        );

        setOnScroll(event -> {
            if (event.getDeltaY() == 0) {
                return;
            }
            double scale = event.getDeltaY() > 0 ? SCALE_IN : SCALE_OUT;

            if (((transformation.getScaleX() < MIN_SCALE || transformation.getScaleY() < MIN_SCALE) && scale < 1)
                || ((transformation.getScaleX() > MAX_SCALE || transformation.getScaleX() > MAX_SCALE) && scale > 1)) {
                return;
            }

            transformation.scale(scale, scale);

            redrawMap();
            redrawGrid();
        });


        setOnMouseMoved(actionEvent -> {
            Point2D point = new Point2D.Double(actionEvent.getX(), actionEvent.getY());
            lastPoint.set(point);
            updatePositionText(point);
        });

        widthProperty().addListener((obs, oldValue, newValue) -> {
            setClip(new Rectangle(0, 0, getWidth(), getHeight()));

            if (alreadyCentered) {
                redrawGrid();
                redrawMap();
            } else {
                center();
            }

            drawPositionText();
        });

        heightProperty().addListener((obs, oldValue, newValue) -> {
            setClip(new Rectangle(0, 0, getWidth(), getHeight()));

            if (alreadyCentered) {
                redrawGrid();
                redrawMap();
            } else {
                center();
            }

            drawPositionText();
        });
    }

    private LabeledEdge drawEdge(Edge<LocationNode<N>> edge) {
        Location a = edge.a().location();
        Location b = edge.b().location();

        Point2D transformedA = transform(a);
        Point2D transformedB = transform(b);

        Line line = new Line(transformedA.getX(), transformedA.getY(), transformedB.getX(), transformedB.getY());
        line.getStrokeDashArray().addAll(50.0, 10.0);

        line.setStrokeWidth(EDGE_STROKE_WIDTH);

        Point2D transformedMidPoint = transform(midPoint(edge));
        Text text = new Text(transformedMidPoint.getX(), transformedMidPoint.getY(), Integer.toString(edge.weight()));
        text.setStroke(TEXT_COLOR);

        getChildren().addAll(line, text);

        return new LabeledEdge(line, text);
    }

    private LabeledNode drawNode(LocationNode<N> node) {
        Point2D transformedPoint = transform(node.location());

        Ellipse ellipse = new Ellipse(transformedPoint.getX(), transformedPoint.getY(), NODE_DIAMETER, NODE_DIAMETER);
        ellipse.setFill(DEFAULT_NODE_COLOR);
        ellipse.setStrokeWidth(NODE_STROKE_WIDTH);
        setMouseTransparent(false);

        Text text = new Text(transformedPoint.getX(), transformedPoint.getY(), node.value().toString());
        text.setStroke(TEXT_COLOR);

        getChildren().addAll(ellipse, text);

        return new LabeledNode(ellipse, text);
    }

    private void drawGrid() {
        Color color = GRID_LINE_COLOR;

        int stepX = (int) (transformation.getScaleX() / 2);
        int stepY = (int) (transformation.getScaleY() / 2);

        int offsetX = (int) transformation.getTranslateX();
        int offsetY = (int) transformation.getTranslateY();

        // Vertical Lines
        for (int i = 0, x = offsetX % (stepX * 5); x <= getWidth(); i++, x += stepX) {
            Float strokeWidth = getStrokeWidth(i, offsetX % (stepX * 10) > stepX * 5);
            if (strokeWidth == null) continue;
            Line line = new Line(x, 0, x, getHeight());
            line.setStrokeWidth(strokeWidth);
            line.setStroke(color);
            getChildren().add(line);
            grid.add(line);
        }

        // Horizontal Lines
        for (int i = 0, y = offsetY % (stepY * 5); y <= getHeight(); i++, y += stepY) {
            Float strokeWidth = getStrokeWidth(i, offsetY % (stepY * 10) > stepY * 5);
            if (strokeWidth == null) continue;

            var line = new Line(0, y, getWidth(), y);
            line.setStrokeWidth(strokeWidth);
            line.setStroke(color);
            getChildren().add(line);
            grid.add(line);
        }
    }

    private Float getStrokeWidth(int i, boolean inverted) {
        float strokeWidth;
        if (i % 10 == 0) {
            strokeWidth = inverted ? GRID_TEN_TICKS_WIDTH : GRID_FIVE_TICKS_WIDTH;
        } else if (i % 5 == 0) {
            strokeWidth = inverted ? GRID_FIVE_TICKS_WIDTH : GRID_TEN_TICKS_WIDTH;
        } else {
            return null;
        }
        return strokeWidth;
    }

    private LabeledEdge getLabeledEdge(Edge<N> edge) {
        LabeledEdge labeledEdge = edges.get(Edge.of(
            new LocationNode<>(edge.a(), nodeLocations.get(edge.a()).location()),
            new LocationNode<>(edge.b(), nodeLocations.get(edge.b()).location()),
            edge.weight()));

        if (labeledEdge == null) {
            throw new IllegalArgumentException("The given edge is not part of this GraphPane");
        }
        return labeledEdge;
    }

    private Point2D locationToPoint2D(Location location) {
        return new Point2D.Double(location.x(), location.y());
    }

    private Point2D getDifference(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }

    private Point2D midPoint(Location location) {
        return new Point2D.Double(location.x(), location.y());
    }

    private Point2D midPoint(LocationNode<N> node) {
        return midPoint(node.location());
    }

    private Point2D midPoint(Edge<LocationNode<N>> edge) {
        var l1 = edge.a().location();
        var l2 = edge.b().location();
        return new Point2D.Double((l1.x() + l2.x()) / 2d, (l1.y() + l2.y()) / 2d);
    }

    private void redrawGrid() {
        getChildren().removeAll(grid);
        grid.clear();
        drawGrid();
    }

    private void drawPositionText() {
        positionText.setX(getWidth() - positionText.getLayoutBounds().getWidth());
        positionText.setY(getHeight());
        positionText.setText("(-, -)");
        if (!getChildren().contains(positionText)) {
            getChildren().add(positionText);
        }
    }

    private void updatePositionText(Point2D point) {
        point = getReverseTransform().transform(point, null);
        positionText.setText("(%d, %d)".formatted((int) point.getX(), (int) point.getY()));
        positionText.setX(getWidth() - positionText.getLayoutBounds().getWidth());
        positionText.setY(getHeight());
    }

    private AffineTransform getReverseTransform() {
        try {
            return transformation.createInverse();
        } catch (NoninvertibleTransformException e) {
            throw new IllegalStateException("transformation is not invertible");
        }
    }

    private Point2D transform(Point2D point) {
        return transformation.transform(point, null);
    }

    private Point2D transform(Location location) {
        return transformation.transform(locationToPoint2D(location), null);
    }

    private static class LabeledEdge {
        private final Line line;
        private final Text text;

        public LabeledEdge(Line line, Text text) {
            this(line, text, DEFAULT_EDGE_COLOR);
        }

        public LabeledEdge(Line line, Text text, Color strokeColor) {
            this.line = line;
            this.text = text;
            setStrokeColor(strokeColor);
        }

        public Line line() {
            return line;
        }

        public Text text() {
            return text;
        }

        public void setStrokeColor(Color strokeColor) {
            line.setStroke(strokeColor);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LabeledEdge) obj;
            return Objects.equals(this.line, that.line) &&
                Objects.equals(this.text, that.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, text);
        }

    }

    private static class LabeledNode {
        private final Ellipse ellipse;
        private final Text text;

        private LabeledNode(Ellipse ellipse, Text text) {
            this(ellipse, text, DEFAULT_NODE_COLOR);
        }

        private LabeledNode(Ellipse ellipse, Text text, Color color) {
            this.ellipse = ellipse;
            this.text = text;
            setStrokeColor(color);
        }

        public Ellipse ellipse() {
            return ellipse;
        }

        public Text text() {
            return text;
        }

        public void setStrokeColor(Color strokeColor) {
            ellipse.setStroke(strokeColor);
        }

        public void setFillColor(Color fillColor) {
            ellipse.setFill(fillColor);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LabeledNode) obj;
            return Objects.equals(this.ellipse, that.ellipse) &&
                Objects.equals(this.text, that.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ellipse, text);
        }

    }

    private record ComparingCollector<T extends Comparable<T>>(
        Comparator<T> comparator) implements Collector<T, List<T>, T> {

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            };
        }

        @Override
        public Function<List<T>, T> finisher() {
            return list -> {

                T bestFit = null;

                for (T elem : list) {
                    if (bestFit == null) {
                        bestFit = elem;
                    } else if (comparator.compare(elem, bestFit) > 0) {
                        bestFit = elem;
                    }
                }

                return bestFit;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
