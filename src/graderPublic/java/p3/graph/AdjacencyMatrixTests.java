package p3.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import p3.util.SerializedEdge;
import p3.util.Utils;

import java.lang.reflect.Field;
import java.util.List;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class AdjacencyMatrixTests {

    private static Field matrixField;

    @BeforeAll
    public static void setup() {
        try {
            matrixField = AdjacencyMatrix.class.getDeclaredField("matrix");
            matrixField.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessError e) {
            // FIXME: use Jagr logger when working again
        }
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "adjacencyMatrix.json", data = "addEdge")
    public void testAddEdge(@Property("matrixSize") int matrixSize,
                            @Property("edge") SerializedEdge<Integer> serializedEdge) {
        AdjacencyMatrix adjacencyMatrixInstance = new AdjacencyMatrix(matrixSize);
        Context context = contextBuilder()
            .add("matrix size", matrixSize)
            .add("edge", serializedEdge)
            .build();
        adjacencyMatrixInstance.addEdge(serializedEdge.a(), serializedEdge.b(), serializedEdge.weight());

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int cellValue = Utils.<int[][]>getFieldValue(matrixField, adjacencyMatrixInstance)[i][j];
                String matrixString = "[[[matrix[%d][%d]]]]".formatted(i, j);
                if (i == serializedEdge.a() && j == serializedEdge.b()) {
                    assertEquals(serializedEdge.weight(), cellValue, context,
                        result -> matrixString + " determines the weight from node A to node B and should therefore be " + serializedEdge.weight());
                } else if (i == serializedEdge.b() && j == serializedEdge.a()) {
                    assertEquals(serializedEdge.weight(), cellValue, context,
                        result -> matrixString + " determines the weight from node B to node A and should therefore be " + serializedEdge.weight());
                } else {
                    assertEquals(0, cellValue, context,
                        result -> matrixString + " should have default value 0 since it should not have been modified");
                }
            }
        }
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "adjacencyMatrix.json", data = "addEdge")
    public void testGetWeight(@Property("matrixSize") int matrixSize,
                              @Property("edge") SerializedEdge<Integer> serializedEdge) {
        AdjacencyMatrix adjacencyMatrixInstance = new AdjacencyMatrix(matrixSize);
        int[][] backingMatrix = Utils.getFieldValue(matrixField, adjacencyMatrixInstance);
        backingMatrix[serializedEdge.a()][serializedEdge.b()] = serializedEdge.weight();
        backingMatrix[serializedEdge.b()][serializedEdge.a()] = serializedEdge.weight();
        Context context = contextBuilder()
            .add("matrix size", matrixSize)
            .add("edge", serializedEdge)
            .build();

        assertEquals(serializedEdge.weight(), adjacencyMatrixInstance.getWeight(serializedEdge.a(), serializedEdge.b()), context,
            result -> "[[[getWeight(%d, %d)]]] did not return the correct value".formatted(serializedEdge.a(), serializedEdge.b()));
        assertEquals(serializedEdge.weight(), adjacencyMatrixInstance.getWeight(serializedEdge.b(), serializedEdge.a()), context,
            result -> "[[[getWeight(%d, %d)]]] did not return the correct value".formatted(serializedEdge.b(), serializedEdge.a()));
    }

    @ParameterizedTest
    @JsonClasspathSource(value = "adjacencyMatrix.json", data = "getAdjacent")
    public void testGetAdjacent(@Property("matrixSize") int matrixSize,
                                @Property("edges") List<SerializedEdge<Integer>> serializedEdges) {
        AdjacencyMatrix adjacencyMatrixInstance = new AdjacencyMatrix(matrixSize);
        int[][] backingMatrix = Utils.getFieldValue(matrixField, adjacencyMatrixInstance);
        Context context = contextBuilder()
            .add("matrix size", matrixSize)
            .add("edges", serializedEdges)
            .build();
        for (SerializedEdge<Integer> edge : serializedEdges) {
            backingMatrix[edge.a()][edge.b()] = edge.weight();
            backingMatrix[edge.b()][edge.a()] = edge.weight();
        }

        for (int i = 0; i < matrixSize; i++) {
            int[] expectedAdjacent = backingMatrix[i];
            int[] actualAdjacent = adjacencyMatrixInstance.getAdjacent(i);

            assertEquals(matrixSize, actualAdjacent.length, context,
                result -> "The returned array does not have the expected length");
            for (int j = 0; j < matrixSize; j++) {
                final int finalJ = j;
                assertEquals(expectedAdjacent[j], actualAdjacent[j], context,
                    result -> "The returned array differs from the expected one at index " + finalJ);
            }
        }
    }
}
