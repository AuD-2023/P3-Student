package p3.util;

import p3.graph.Edge;

public record SerializedEdge<N>(N a, N b, int weight) {

    public Edge<N> toEdge() {
        return Edge.of(a, b, weight);
    }

    @Override
    public String toString() {
        return "(%s -- %s, %d)".formatted(a, b, weight);
    }
}
