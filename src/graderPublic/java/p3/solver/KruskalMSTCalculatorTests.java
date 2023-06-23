package p3.solver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.graph.Graph;
import p3.util.SerializedGraph;
import p3.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class KruskalMSTCalculatorTests {

    private static Field mstEdgesField;
    private static Field mstGroupsField;

    @BeforeAll
    public static void setup() {
        try {
            mstEdgesField = KruskalMSTCalculator.class.getDeclaredField("mstEdges");
            mstEdgesField.setAccessible(true);
            mstGroupsField = KruskalMSTCalculator.class.getDeclaredField("mstGroups");
            mstGroupsField.setAccessible(true);
        } catch (NoSuchFieldException | InaccessibleObjectException e) {
            // FIXME: use Jagr logger when working again
        }
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "kruskalMSTCalculator.json", data = "init")
    public <N> void testInit(SerializedGraph<N> serializedGraph) {
        Graph<N> graph = serializedGraph.toGraph();
        KruskalMSTCalculator<N> kruskalMSTCalculatorInstance = new KruskalMSTCalculator<>(graph);
        kruskalMSTCalculatorInstance.init();
        List<Set<N>> mstGroups = Utils.getFieldValue(mstGroupsField, kruskalMSTCalculatorInstance);
        Context context = contextBuilder()
            .add("graph", serializedGraph)
            .add("mstGroups", mstGroups)
            .build();
        Map<N, Integer> nodeOccurrences = serializedGraph.nodes()
            .stream()
            .map(node -> Map.entry(node, 0))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(0, Utils.<Set<?>>getFieldValue(mstEdgesField, kruskalMSTCalculatorInstance).size(), context,
            result -> "[[[mstEdges]]] contains elements after calling [[[init]]]");
        assertEquals(serializedGraph.nodes().size(), mstGroups.size(), context,
            result -> "[[[mstEdges]]] does not have the correct size");
        for (Set<N> mstGroup : mstGroups) {
            Context mstGroupContext = contextBuilder()
                .add(context)
                .add("mstGroup", mstGroup)
                .build();
            assertEquals(1, mstGroup.size(), mstGroupContext,
                result -> "[[[mstGroup]]] does not have the correct size");
            N node = mstGroup.stream().findAny().get();
            nodeOccurrences.computeIfPresent(node, (key, value) -> value + 1);
            assertTrue(nodeOccurrences.get(node) == 1, mstGroupContext,
                result -> "[[[mstGroup]]] has more than one node");
        }
        Set<N> remainingNodes = serializedGraph.nodes()
            .stream()
            .filter(node -> nodeOccurrences.get(node) == 0)
            .collect(Collectors.toSet());
        assertTrue(remainingNodes.isEmpty(), context,
            result -> "[[[mstGroups]]] does not contain all nodes. Remaining nodes: " + remainingNodes);
    }
}
