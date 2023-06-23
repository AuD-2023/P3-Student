package p3.graph;

import java.util.Set;

/**
 * An undirected graph with nodes of type {@code N}.
 *
 * <p>
 * A graph is a set of nodes and a set of edges.
 * Each edge connects two nodes.
 * There can be at most one edge between two nodes.
 * </p>
 *
 * <p>
 * A graph is not necessarily mutable.
 * </p>
 *
 * @param <N> the type of the nodes in this graph.
 */
public interface Graph<N> {

    /**
     * Returns all nodes in this graph.
     * @return a set of all nodes in this graph.
     */
    Set<N> getNodes();

    /**
     * Returns all edges in this graph.
     * @return a set of all edges in this graph.
     */
    Set<Edge<N>> getEdges();

    /**
     * Returns all edges that are adjacent to the given node.
     * <p> An edge is adjacent to a node if the node is on of the nodes the edge is connected to.
     * @param node the node to get the adjacent edges of.
     * @return a set of all edges that are adjacent to the given node.
     */
    Set<Edge<N>> getAdjacentEdges(N node);

    /**
     * Creates a mutable copy of this graph with the same nodes and edges.
     *
     * <p>
     * The nodes and edges in the copy are the same objects as in the original graph.
     * Only the graph structure is copied.
     * </p>
     */
    MutableGraph<N> toMutableGraph();

    /**
     * Creates an immutable copy of this graph with the same nodes and edges if the graph is mutable,
     * or returns the graph itself if it is already immutable.
     *
     * <p>
     * The nodes and edges in the copy are the same objects as in the original graph.
     * Only the graph structure is copied.
     * </p>
     */
    Graph<N> toGraph();

    /**
     * Creates a new empty immutable graph.
     * @return a new empty immutable graph.
     * @param <N> the type of the nodes in the graph.
     */
    @SuppressWarnings("unchecked")
    static <N> Graph<N> of() {
        return (Graph<N>) BasicGraph.EMPTY.get();
    }

    /**
     * Creates a new immutable graph with the given nodes and edges.
     * @param nodes the nodes in the graph.
     * @param edges the edges in the graph.
     * @return a new immutable graph with the given nodes and edges.
     * @param <N> the type of the nodes in the graph.
     */
    static <N> Graph<N> of(Set<N> nodes, Set<Edge<N>> edges) {
        return new BasicGraph<>(nodes, edges);
    }
}
