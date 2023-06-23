package p3.graph;

import java.util.Set;

/**
 * A mutable undirected graph with nodes of type {@code N}.
 *
 * <p>
 * A graph is a set of nodes and a set of edges.
 * Each edge connects two nodes.
 * There can be at most one edge between two nodes.
 * </p>
 *
 * @param <N> the type of the nodes in this graph.
 */
public interface MutableGraph<N> extends Graph<N> {

    /**
     * Adds the given node to this graph.
     * <p> If the node is already in the graph, nothing happens.
     * @param node the node to add.
     * @return this graph.
     */
    MutableGraph<N> putNode(N node);

    /**
     * Adds the given edge to this graph.
     * <p> If the edge is already in the graph, it is replaced with a new Edge created with the parameter.
     * For edges, equality is defined by the {@link Edge#equals(Object)} method, which does not include the weight of the edge.
     * <p> If the nodes of the edge are not in the graph, an {@link IllegalArgumentException} is thrown.
     * @param a one of the nodes of the edge.
     * @param b the other node of the edge.
     * @param weight the weight of the edge.
     * @return this graph.
     * @throws IllegalArgumentException if one of the nodes of the edge is not in the graph.
     */
    MutableGraph<N> putEdge(N a, N b, int weight);

    /**
     * Adds the given edge and nodes to this graph.
     * <p> If the edge is already in the graph, it is replaced with a new Edge created with the parameter.
     * For edges, equality is defined by the {@link Edge#equals(Object)} method, which does not include the weight of the edge.
     * <p> If the nodes of the edge are not in the graph, they are added.
     * @param a one of the nodes of the edge.
     * @param b the other node of the edge.
     * @param weight the weight of the edge.
     * @return this graph.
     */
    MutableGraph<N> putEdgesAndNodes(N a, N b, int weight);

    /**
     * Creates a new empty mutable graph.
     * @return a new empty mutable graph.
     * @param <N> the type of the nodes in the graph.
     */
    static <N> MutableGraph<N> of() {
        return new BasicMutableGraph<>();
    }

    /**
     * Creates a new mutable graph with the given nodes and edges.
     * @param nodes the nodes to add to the graph.
     * @param edges the edges to add to the graph.
     * @return a new mutable graph with the given nodes and edges.
     * @param <N> the type of the nodes in the graph.
     */
    static <N> MutableGraph<N> of(Set<N> nodes, Set<Edge<N>> edges) {
        return new BasicMutableGraph<>(nodes, edges);
    }
}
