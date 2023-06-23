package p3.graph;

/**
 * An undirected edge in a {@link Graph}.
 * @param <N> the type of the nodes in the graph.
 */
public interface Edge<N> extends Comparable<Edge<N>> {

    /**
     * The first node that this edge connects.
     * @return The first node that this edge connects.
     */
    N a();

    /**
     * The second node that this edge connects.
     * @return The second node that this edge connects.
     */
    N b();

    /**
     * The weight of the edge. The precise meaning of this value is up to the user.
     *
     * <p>
     * This value is used to order edges in the graph.
     * </p>
     *
     * @return the weight of the edge
     */
    int weight();

    /**
     * Two edges are equal if they have the same nodes.
     *
     * <p>More precisely, two edges <code>x</code> and <code>y</code> are equal iff:</p>
     * <ul>
     *     <li><code>Objects.equals(x.getA(), y.getA())</code></li>
     *     <li><code>Objects.equals(x.getB(), y.getB())</code></li>
     * </ul>
     *
     * @param other the other edge
     * @return true if the edges are equal, false otherwise
     */
    boolean equals(Object other);

    /**
     * Edges are ordered by weight.
     *
     * @param other the other edge
     * @return Whether this edge's weight is less than, equal to, or greater than the other edge's weight
     */
    @Override
    default int compareTo(Edge<N> other) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO H2 a): remove if implemented

    }

    /**
     * Creates a new edge with the given nodes and weight.
     * @param a the first node
     * @param b the second node
     * @param weight the weight of the edge
     * @return a new edge with the given nodes and weight
     * @param <N> the type of the nodes in the graph
     */
    static <N> Edge<N> of(N a, N b, int weight) {
        return new EdgeImpl<>(a, b, weight);
    }
}
