package p3.graph;

import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.util.SerializedEdge;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class EdgeTests {

    @ParameterizedTest
    @JsonClasspathSource("edge.json")
    public void testCompareTo(@Property("edge1") SerializedEdge<String> serializedEdge1,
                              @Property("edge2") SerializedEdge<String> serializedEdge2,
                              @Property("expectedValue") int expectedValue) {
        Edge<String> edge1 = serializedEdge1.toEdge();
        Edge<String> edge2 = serializedEdge2.toEdge();
        Context context = contextBuilder()
            .add("edge1", serializedEdge1)
            .add("edge2", serializedEdge2)
            .build();

        if (expectedValue > 0) {
            assertTrue(edge1.compareTo(edge2) > 0, context,
                result -> "[[[edge1.compareTo(edge2)]]] did not return the correct value (expected positive)");
        } else if (expectedValue < 0) {
            assertTrue(edge1.compareTo(edge2) < 0, context,
                result -> "[[[edge1.compareTo(edge2)]]] did not return the correct value (expected negative)");
        } else {
            assertTrue(edge1.compareTo(edge2) == 0, context,
                result -> "[[[edge1.compareTo(edge2)]]] did not return the correct value (expected 0)");
        }
    }
}
