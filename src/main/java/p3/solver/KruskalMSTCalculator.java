package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of Kruskal's algorithm, a minimum spanning tree algorithm.
 * @param <N> The type of the nodes in the graph.
 */
public class KruskalMSTCalculator<N> implements MSTCalculator<N> {

    /**
     * Factory for creating new instances of {@link KruskalMSTCalculator}.
     */
    public static MSTCalculator.Factory FACTORY = KruskalMSTCalculator::new;

    /**
     * The graph to calculate the MST for.
     */

    protected final Graph<N> graph;

    /**
     * The edges in the MST.
     */
    protected final Set<Edge<N>> mstEdges;

    /**
     * The groups of nodes in the MST.
     * <p> Each group is represented by a set of nodes. Initially, each node is in its own group. </p>
     * <p> When two nodes are in the same groups, they are in the same MST which is created by {@link #mstEdges}. </p>
     * <p> Every node is in exactly one group. </p>
     */
    protected final List<Set<N>> mstGroups;

    /**
     * Construct a new {@link KruskalMSTCalculator} for the given graph.
     * @param graph the graph to calculate the MST for.
     */
    public KruskalMSTCalculator(Graph<N> graph) {
        this.graph = graph;
        this.mstEdges = new HashSet<>();
        this.mstGroups = new ArrayList<>();
    }
    @Override
    public Graph<N> calculateMST() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO H2 e): remove if implemented
    }

    /**
     * Initializes the {@link #mstEdges} and {@link #mstGroups} with their default values.
     * <p> Initially, {@link #mstEdges} is empty and {@link #mstGroups} contains a set for each node in the graph.
     */
    protected void init() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO H2 b): remove if implemented
    }

    /**
     * Processes an edge during Kruskal's algorithm.
     * <p> If the edge's nodes are in the same MST (group), the edge is skipped.
     * <p> If the edge's nodes are in different MSTs (groups), the groups are merged via the {@link #joinGroups(int, int)} method.
     *
     * @param edge The edge to process.
     * @return {@code true} if the edge was accepted and the two MST's were merged,
     * {@code false} if it was skipped.
     */
    protected boolean acceptEdge(Edge<N> edge) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO H2 d): remove if implemented

    }

    /**
     * Joins two sets in the list of all MST Groups.
     * <p> After joining the larger set will additionally contain all elements of the smaller set and
     * the smaller set will be removed from the list.
     *
     * @param aIndex The index of the first set to join.
     * @param bIndex The index of the second set to join.
     */
    protected void joinGroups(int aIndex, int bIndex) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO H2 c): remove if implemented

    }
}
