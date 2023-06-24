package p3.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BasicMutableGraph<N> extends BasicGraph<N> implements MutableGraph<N> {

    /**
     * The nodes in this graph.
     * This is used instead of the fields in {@link BasicGraph} to allow for mutation.
     */
    private final Set<N> mutableNodes = new HashSet<>();

    /**
     * The edges in this graph.
     * This is used instead of the fields in {@link BasicGraph} to allow for mutation.
     */
    private final Set<Edge<N>> mutableEdges = new HashSet<>();

    /**
     * A map from nodes to the edges that are adjacent to them.
     * If a node has no adjacent edges, it is mapped to an empty set.
     * This is used instead of the fields in {@link BasicGraph} to allow for mutation.
     */
    private final Map<N, Set<Edge<N>>> mutableBacking = new HashMap<>();

    /**
     * Constructs a new empty {@link BasicMutableGraph}.
     */
    public BasicMutableGraph() {
        this(Set.of(), Set.of());
    }

    public BasicMutableGraph(Set<N> nodes, Set<Edge<N>> edges) {
        // Usually we wouldn't use inheritance and just copy the constructor code here,
        // but this would include the solution to the exercise.

        super(nodes, edges);
        mutableNodes.addAll(nodes);
        mutableEdges.addAll(edges);

        for (Map.Entry<N, Set<Edge<N>>> entry : backing.entrySet()) {
            mutableBacking.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    @Override
    public Set<N> getNodes() {
        return mutableNodes;
    }

    @Override
    public Set<Edge<N>> getEdges() {
        return mutableEdges;
    }

    @Override
    public MutableGraph<N> toMutableGraph() {
        return MutableGraph.of(mutableNodes, mutableEdges);
    }

    @Override
    public Graph<N> toGraph() {
        return Graph.of(mutableNodes, mutableEdges);
    }

    @Override
    public MutableGraph<N> putNode(N node) {
        if (!mutableBacking.containsKey(node)) {
            mutableBacking.put(node, new HashSet<>());
            mutableNodes.add(node);
        }
        return this;
    }

    @Override
    public MutableGraph<N> putEdge(N a, N b, int weight) {
        final Set<Edge<N>> edgesA = mutableBacking.get(a);
        final Set<Edge<N>> edgesB = mutableBacking.get(b);
        if (edgesA == null || edgesB == null) {
            throw new IllegalArgumentException("Node not found: " + (edgesA == null ? a : b));
        }

        final Edge<N> edge = Edge.of(a, b, weight);
        edgesA.add(edge);
        edgesB.add(edge);
        mutableEdges.add(edge);
        return this;
    }

    @Override
    public MutableGraph<N> putEdgesAndNodes(N a, N b, int weight) {
        mutableNodes.add(a);
        mutableNodes.add(b);

        final Set<Edge<N>> edgesA = mutableBacking.computeIfAbsent(a, k -> new HashSet<>());
        final Set<Edge<N>> edgesB = mutableBacking.computeIfAbsent(b, k -> new HashSet<>());

        final Edge<N> edge = Edge.of(a, b, weight);
        edgesA.add(edge);
        edgesB.add(edge);
        mutableEdges.add(edge);

        return this;
    }
}
