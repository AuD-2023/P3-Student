package p3.graph;

import java.util.Objects;

/**
 * A basic implementation of an {@link Edge}.
 * @param a The first node in the edge.
 * @param b The second node in the edge.
 * @param weight The weight of the edge.
 * @param <N> The type of the nodes in the graph.
 */
record EdgeImpl<N>(N a, N b, int weight) implements Edge<N> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeImpl<?> edge = (EdgeImpl<?>) o;
        return ((Objects.equals(a, edge.a) && Objects.equals(b, edge.b)) ||
                (Objects.equals(a, edge.b) && Objects.equals(b, edge.a)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b) + Objects.hash(a, b);
    }
}
