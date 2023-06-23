package p3.gui;

/**
 * A data class for storing a node and its location in a 2D plane.
 * @param value The node.
 * @param location The location of the node.
 * @param <N> The type of the node.
 */
public record LocationNode<N>(N value, Location location) {
}
