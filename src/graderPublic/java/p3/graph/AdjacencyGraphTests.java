package p3.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.sourcegrade.jagr.api.testing.extension.JagrExecutionCondition;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.util.SerializedEdge;
import p3.util.Utils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class AdjacencyGraphTests {

    private static Field nodeIndicesField;
    private static Field indexNodesField;

    public static List<Object[]> addEdgeParameters = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        try {
            nodeIndicesField = AdjacencyGraph.class.getDeclaredField("nodeIndices");
            nodeIndicesField.setAccessible(true);
            indexNodesField = AdjacencyGraph.class.getDeclaredField("indexNodes");
            indexNodesField.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessError e) {
            // FIXME: use Jagr logger when working again
        }
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "adjacencyGraph.json", data = "constructor")
    public <N> void testConstructorMaps(@Property("nodes") Set<N> nodes,
                                        @Property("edges") Set<SerializedEdge<N>> serializedEdges) {
        Set<Edge<N>> edges = serializedEdges.stream()
            .map(SerializedEdge::toEdge)
            .collect(Collectors.toSet());
        AdjacencyGraph<N> adjacencyGraphInstance = new AdjacencyGraph<>(nodes, edges);
        Map<N, Integer> nodeIndices = Utils.getFieldValue(nodeIndicesField, adjacencyGraphInstance);
        Map<Integer, N> indexNodes = Utils.getFieldValue(indexNodesField, adjacencyGraphInstance);
        Set<Integer> indices = IntStream.range(0, nodes.size()).boxed().collect(Collectors.toSet());
        Context context = contextBuilder()
            .add("nodes", nodes)
            .add("edges", serializedEdges)
            .build();

        assertEquals(nodes.size(), nodeIndices.size(), context,
            result -> "[[[nodeIndices]]] does not have the correct size");
        assertTrue(nodeIndices.keySet().containsAll(nodes), context,
            result -> "[[[nodeIndices]]] contains unexpected nodes (keys): " + nodeIndices.keySet()
                .stream()
                .filter(node -> !nodes.contains(node))
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]")));
        assertTrue(nodeIndices.values().containsAll(indices), context,
            result -> "[[[nodeIndices]]] contains unexpected indices (values): " + nodeIndices.values()
                .stream()
                .filter(node -> !indices.contains(node))
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]")));

        assertEquals(indices.size(), indexNodes.size(), context,
            result -> "[[[indexNodes]]] does not have the correct size");
        assertTrue(indexNodes.keySet().containsAll(indices), context,
            result -> "[[[indexNodes]]] contains unexpected indices (keys): " + indexNodes.keySet()
                .stream()
                .filter(index -> !indices.contains(index))
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]")));
        assertTrue(indexNodes.values().containsAll(nodes), context,
            result -> "[[[indexNodes]]] contains unexpected nodes (values): " + indexNodes.values()
                .stream()
                .filter(node -> !nodes.contains(node))
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]")));
    }

    @ParameterizedTest
    @ExtendWith(JagrExecutionCondition.class)
    @JsonClasspathSource(value = "adjacencyGraph.json", data = "constructor")
    public <N> void testConstructorEdges(@Property("nodes") Set<N> nodes,
                                         @Property("edges") Set<SerializedEdge<N>> serializedEdges) {
        Set<Edge<N>> edges = serializedEdges.stream()
            .map(SerializedEdge::toEdge)
            .collect(Collectors.toSet());
        addEdgeParameters.clear();
        AdjacencyGraph<N> adjacencyGraphInstance = new AdjacencyGraph<>(nodes, edges);
        Map<Integer, N> indexNodes = Utils.getFieldValue(indexNodesField, adjacencyGraphInstance);
        Set<Edge<N>> remainingEdges = new HashSet<>(edges);
        Context context = contextBuilder()
            .add("nodes", nodes)
            .add("edges", serializedEdges)
            .build();

        assertEquals(edges.size(), addEdgeParameters.size(), context,
            result -> "[[[matrix.addEdge]]] was not called the expected number of times");
        addEdgeParameters.stream()
            .map(parameters -> {
                int nodeAIndex = (int) parameters[0];
                int nodeBIndex = (int) parameters[1];
                int edgeWeight = (int) parameters[2];
                N nodeA = null, nodeB = null;

                if (indexNodes.containsKey(nodeAIndex)) {
                    nodeA = indexNodes.get(nodeAIndex);
                } else {
                    fail(context, result -> "[[[indexNodes]]] does not contain a mapping for node index " + nodeAIndex);
                }
                if (indexNodes.containsKey(nodeBIndex)) {
                    nodeB = indexNodes.get(nodeBIndex);
                } else {
                    fail(context, result -> "[[[indexNodes]]] does not contain a mapping for node index " + nodeBIndex);
                }

                return Edge.of(nodeA, nodeB, edgeWeight);
            })
            .forEach(remainingEdges::remove);
        assertTrue(remainingEdges.isEmpty(), context,
            result -> "Not all edges were added to the backing adjacency matrix. Remaining edges: " + remainingEdges);
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "adjacencyGraph.json", data = "getAdjacentEdgesPositiveWeight")
    public <N> void testGetAdjacentEdgesPositiveWeight(@Property("nodes") Set<N> nodes,
                                                       @Property("edges") Set<SerializedEdge<N>> serializedEdges) {
        testGetAdjacentEdges(nodes, serializedEdges);
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "adjacencyGraph.json", data = "getAdjacentEdgesWithZeroWeights")
    public <N> void testGetAdjacentEdgesWithZeroWeights(@Property("nodes") Set<N> nodes,
                                                        @Property("edges") Set<SerializedEdge<N>> serializedEdges) {
        testGetAdjacentEdges(nodes, serializedEdges);
    }

    private static <N> void testGetAdjacentEdges(Set<N> nodes, Set<SerializedEdge<N>> serializedEdges) {
        Set<Edge<N>> edges = serializedEdges.stream()
            .map(SerializedEdge::toEdge)
            .collect(Collectors.toSet());
        AdjacencyGraph<N> adjacencyGraphInstance = new AdjacencyGraph<>(nodes, edges);

        for (N node : nodes) {
            Set<Edge<N>> expectedEdges = edges.stream()
                .filter(edge -> edge.weight() > 0 && (edge.a().equals(node) || edge.b().equals(node)))
                .collect(Collectors.toSet());
            Set<Edge<N>> actualEdges = adjacencyGraphInstance.getAdjacentEdges(node);
            Context methodCallContext = contextBuilder()
                .add("nodes", nodes)
                .add("edges", serializedEdges)
                .add("node", node)
                .add("expected edges", expectedEdges)
                .add("actual edges", actualEdges)
                .build();

            assertEquals(expectedEdges.size(), actualEdges.size(), methodCallContext,
                result -> "The set returned by [[[getAdjacent]]] does not have the correct size");
            assertTrue(actualEdges.stream().anyMatch(edge -> edge.a().equals(node) || edge.b().equals(node)), methodCallContext,
                result -> "The set returned by [[[getAdjacent]]] does not contain all expected edges");
        }
    }
}
