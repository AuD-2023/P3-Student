package p3.solver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.util.SerializedEntry;
import p3.util.SerializedGraph;
import p3.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class DijkstraPathCalculatorTests {

    private static Field distancesField;
    private static Field predecessorsField;
    private static Field remainingNodesField;

    @BeforeAll
    public static void setup() {
        try {
            distancesField = DijkstraPathCalculator.class.getDeclaredField("distances");
            distancesField.setAccessible(true);
            predecessorsField = DijkstraPathCalculator.class.getDeclaredField("predecessors");
            predecessorsField.setAccessible(true);
            remainingNodesField = DijkstraPathCalculator.class.getDeclaredField("remainingNodes");
            remainingNodesField.setAccessible(true);
        } catch (NoSuchFieldException | InaccessibleObjectException e) {
            // FIXME: use Jagr logger when working again
        }
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "dijkstraPathCalculator.json", data = "init")
    public <N> void testInit(@Property("graph") SerializedGraph<N> serializedGraph,
                             @Property("startNode") N startNode) {
        Context context = contextBuilder()
            .add("graph", serializedGraph)
            .add("start node", startNode)
            .build();
        DijkstraPathCalculator<N> dijkstraPathCalculatorInstance = new DijkstraPathCalculator<>(serializedGraph.toGraph());
        dijkstraPathCalculatorInstance.init(startNode);
        Map<N, Integer> distances = Utils.getFieldValue(distancesField, dijkstraPathCalculatorInstance);
        Map<N, N> predecessors = Utils.getFieldValue(predecessorsField, dijkstraPathCalculatorInstance);
        Set<N> remainingNodes = Utils.getFieldValue(remainingNodesField, dijkstraPathCalculatorInstance);

        assertEquals(serializedGraph.nodes().size(), distances.size(), context,
            result -> "[[[distances]]] does not have the expected number of entries");
        assertEquals(serializedGraph.nodes().size(), predecessors.size(), context,
            result -> "[[[predecessors]]] does not have the expected number of entries");
        assertEquals(serializedGraph.nodes().size(), remainingNodes.size(), context,
            result -> "[[[remainingNodes]]] does not have the expected number of entries");

        for (N node : serializedGraph.nodes()) {
            assertTrue(distances.containsKey(node), context,
                result -> "[[[distances]]] does not contain node (key) " + node);
            assertEquals(node.equals(startNode) ? 0 : Integer.MAX_VALUE, distances.get(node), context,
                result -> "The value mapped to %s in [[[distances]]] does not equal the expected value".formatted(node));

            assertTrue(predecessors.containsKey(node), context,
                result -> "[[[predecessors]]] does not contain node (key) " + node);
            assertNull(predecessors.get(node), context,
                result -> "The value mapped to %s in [[[predecessors]]] does not equal the expected value".formatted(node));

            assertTrue(remainingNodes.containsAll(serializedGraph.nodes()), context,
                result -> "");
        }
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "dijkstraPathCalculator.json", data = "extractMinSimple")
    public <N> void testExtractMinSimple(@Property("graph") SerializedGraph<N> serializedGraph,
                                         @Property("distances") List<SerializedEntry<N, Integer>> serializedDistances,
                                         @Property("expected") N expectedNode) {
        testExtractMin(serializedGraph, serializedDistances, serializedGraph.nodes(), expectedNode);
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "dijkstraPathCalculator.json", data = "extractMinFull")
    public <N> void testExtractMinFull(@Property("graph") SerializedGraph<N> serializedGraph,
                                       @Property("distances") List<SerializedEntry<N, Integer>> serializedDistances,
                                       @Property("remainingNodes") Set<N> remainingNodes,
                                       @Property("expected") N expectedNode) {
        testExtractMin(serializedGraph, serializedDistances, remainingNodes, expectedNode);
    }

    private static <N> void testExtractMin(SerializedGraph<N> serializedGraph,
                                           List<SerializedEntry<N, Integer>> serializedDistances,
                                           Set<N> remainingNodes,
                                           N expectedNode) {
        Context context = contextBuilder()
            .add("graph", serializedGraph)
            .add("distances", serializedDistances)
            .build();
        DijkstraPathCalculator<N> dijkstraPathCalculatorInstance = new DijkstraPathCalculator<>(serializedGraph.toGraph());
        Utils.setFieldValue(distancesField, dijkstraPathCalculatorInstance, serializedDistances.stream()
            .collect(Collectors.toMap(SerializedEntry::key, SerializedEntry::value)));
        Utils.setFieldValue(remainingNodesField, dijkstraPathCalculatorInstance, remainingNodes);

        assertEquals(expectedNode, dijkstraPathCalculatorInstance.extractMin(), context,
            result -> "[[[extractMin]]] did not return the expected node");
    }
}
