package p3.graph;

import p3.SetUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of an immutable {@link Graph} that uses an {@link AdjacencyMatrix} to store the graph.
 * @param <N> the type of the nodes in this graph.
 */
public class AdjacencyGraph<N> implements Graph<N> {

    /**
     * The adjacency matrix that stores the graph.
     */
    private final AdjacencyMatrix matrix;

    /**
     * A map from nodes to their indices in the adjacency matrix.
     * Every node in the graph is mapped to a distinct index in the range [0, {@link #matrix}.size() -1].
     * This map is the inverse of {@link #indexNodes}.
     */
    private final Map<N, Integer> nodeIndices = new HashMap<>();

    /**
     * A map from indices in the adjacency matrix to the nodes they represent.
     * Every index in the range [0, {@link #matrix}.size() -1] is mapped to a distinct node in the graph.
     * This map is the inverse of {@link #nodeIndices}.
     */
    private final Map<Integer, N> indexNodes = new HashMap<>();

    /**
     * The nodes in this graph.
     */
    private final Set<N> nodes;

    /**
     * The edges in this graph.
     */
    private final Set<Edge<N>> edges;

    /**
     * Constructs a new {@link AdjacencyGraph} with the given nodes and edges.
     * @param nodes the nodes in the graph.
     * @param edges the edges in the graph.
     */
    public AdjacencyGraph(Set<N> nodes, Set<Edge<N>> edges) {
        matrix = new AdjacencyMatrix(nodes.size());
        this.nodes = SetUtils.immutableCopyOf(nodes);
        this.edges = SetUtils.immutableCopyOf(edges);

        throw new UnsupportedOperationException("Not implemented yet"); // TODO H1 c): remove if implemented
    }

    @Override
    public Set<N> getNodes() {
        return nodes;
    }

    @Override
    public Set<Edge<N>> getEdges() {
        return edges;
    }

    @Override
    public Set<Edge<N>> getAdjacentEdges(N node) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO H1 c): remove if implemented
    }

    @Override
    public MutableGraph<N> toMutableGraph() {
        return MutableGraph.of(nodes, edges);
    }

    @Override
    public Graph<N> toGraph() {
        return this;
    }
}
