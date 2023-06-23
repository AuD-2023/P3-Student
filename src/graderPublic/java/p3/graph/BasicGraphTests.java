package p3.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.util.SerializedEdge;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;
import static p3.util.Utils.getFieldValue;

@TestForSubmission
public class BasicGraphTests {

    private static Field backingField;
    private static Field nodesField;
    private static Field edgesField;

    @BeforeAll
    public static void setup() {
        try {
            backingField = BasicGraph.class.getDeclaredField("backing");
            backingField.setAccessible(true);
            nodesField = BasicGraph.class.getDeclaredField("nodes");
            nodesField.setAccessible(true);
            edgesField = BasicGraph.class.getDeclaredField("edges");
            edgesField.setAccessible(true);
        } catch (NoSuchFieldException | InaccessibleObjectException e) {
            // FIXME: https://github.com/sourcegrade/jagr/issues/242
//            Jagr.Default.getInjector().getInstance(Logger.class).error("An exception occurred during setup", e);
        }
    }

    @ParameterizedTest
    @SuppressWarnings("DuplicatedCode")
    @JsonClasspathSource(value = "basicGraph.json", data = "twoNodes")
    public <N> void testTwoNodes(@Property("nodes") Set<N> nodes,
                                 @Property("edges") Set<SerializedEdge<N>> serializedEdges) {
        Set<Edge<N>> edges = deserializeEdges(serializedEdges);
        BasicGraph<N> basicGraph = new BasicGraph<>(nodes, edges);

        testNodesCorrect(nodes, basicGraph);
        testEdgesCorrect(edges, basicGraph);
        testBackingCorrect(nodes, edges, basicGraph);
    }

    @ParameterizedTest
    @SuppressWarnings("DuplicatedCode")
    @JsonClasspathSource(value = "basicGraph.json", data = "threeNodes")
    public <N> void testThreeNodes(@Property("nodes") Set<N> nodes,
                                   @Property("edges") Set<SerializedEdge<N>> serializedEdges) {
        Set<Edge<N>> edges = deserializeEdges(serializedEdges);
        BasicGraph<N> basicGraph = new BasicGraph<>(nodes, edges);

        testNodesCorrect(nodes, basicGraph);
        testEdgesCorrect(edges, basicGraph);
        testBackingCorrect(nodes, edges, basicGraph);
    }

    @ParameterizedTest
    @SuppressWarnings("DuplicatedCode")
    @JsonClasspathSource(value = "basicGraph.json", data = "multipleNodes")
    public <N> void testMultipleNodes(@Property("nodes") Set<N> nodes,
                                      @Property("edges") Set<SerializedEdge<N>> serializedEdges) {
        Set<Edge<N>> edges = deserializeEdges(serializedEdges);
        BasicGraph<N> basicGraph = new BasicGraph<>(nodes, edges);

        testNodesCorrect(nodes, basicGraph);
        testEdgesCorrect(edges, basicGraph);
        testBackingCorrect(nodes, edges, basicGraph);
    }

    private static <N> void testNodesCorrect(Set<N> expectedNodes, BasicGraph<N> basicGraphInstance) {
        Set<N> actualNodes = getFieldValue(nodesField, basicGraphInstance);
        Context context = contextBuilder()
            .add("expected nodes", expectedNodes)
            .add("actual nodes", actualNodes)
            .build();

        assertEquals(expectedNodes.size(), actualNodes.size(), context,
            result -> "Size of the set of actual nodes ([[[actualNodes.size()]]]) differs from the expected value");
        assertTrue(actualNodes.containsAll(expectedNodes), context,
            result -> "actualNodes contains unexpected nodes: " + actualNodes.stream()
                .filter(node -> !expectedNodes.contains(node))
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]")));
    }

    private static <N> void testEdgesCorrect(Set<Edge<N>> expectedEdges, BasicGraph<N> basicGraphInstance) {
        Set<Edge<N>> actualEdges = getFieldValue(edgesField, basicGraphInstance);
        Context context = contextBuilder()
            .add("expected edges", expectedEdges)
            .add("actual edges", actualEdges)
            .build();

        assertEquals(expectedEdges.size(), actualEdges.size(), context,
            result -> "Size of the set of actual edges ([[[actualEdges.size()]]]) differs from the expected value");
        assertTrue(actualEdges.containsAll(expectedEdges), context,
            result -> "actualEdges contains unexpected edges: " + actualEdges.stream()
                .filter(edge -> !expectedEdges.contains(edge))
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]")));
    }

    private static <N> void testBackingCorrect(Set<N> nodes, Set<Edge<N>> edges, BasicGraph<N> basicGraphInstance) {
        Map<N, Set<Edge<N>>> actualBacking = getFieldValue(backingField, basicGraphInstance);
        Context context = contextBuilder()
            .add("nodes", nodes)
            .add("edges", edges)
            .add("actual backing", actualBacking)
            .build();

        assertEquals(nodes.size(), actualBacking.size(), context,
            result -> "[[[backing]]] does not contain a mapping for every node. The size of the key set does not equal the expected value");
        assertTrue(actualBacking.keySet().containsAll(nodes), context,
            result -> "[[[backing]]]'s key set ([[[backing.keySet()]]]) contains unexpected nodes: " + actualBacking.keySet()
                .stream()
                .filter(node -> !nodes.contains(node))
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]")));
        for (N node : nodes) {
            Set<Edge<N>> mappedEdges = assertNotNull(actualBacking.get(node), context,
                result -> "[[[backing]]] contains no mapping for node [[[%s]]] or is mapped to [[[null]]]".formatted(node));
            Set<Edge<N>> expectedMappedEdges = edges.stream()
                .filter(edge -> Objects.equals(edge.a(), node) || Objects.equals(node, edge.b()))
                .collect(Collectors.toSet());

            assertEquals(expectedMappedEdges.size(), mappedEdges.size(), context,
                result -> "the size of [[[backing]]]'s mapping for node [[[%s]]] differs from the expected value".formatted(node));
            assertTrue(mappedEdges.containsAll(expectedMappedEdges), context,
                result -> "[[[backing]]]'s mapping for node [[[%s]]] contains unexpected edges: ".formatted(node) + mappedEdges.stream()
                    .filter(edge -> !expectedMappedEdges.contains(edge))
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "[", "]")));
        }
    }

    private static <N> Set<Edge<N>> deserializeEdges(Set<SerializedEdge<N>> serializedEdges) {
        return serializedEdges.stream()
            .map(SerializedEdge::toEdge)
            .collect(Collectors.toSet());
    }
}
