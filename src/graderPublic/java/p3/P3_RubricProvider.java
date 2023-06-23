package p3;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.GradeResult;
import org.sourcegrade.jagr.api.rubric.Grader;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;
import org.sourcegrade.jagr.api.testing.RubricConfiguration;
import p3.graph.AdjacencyGraphTests;
import p3.graph.AdjacencyMatrixTests;
import p3.graph.BasicGraphTests;
import p3.graph.EdgeTests;
import p3.solver.DijkstraPathCalculatorTests;
import p3.solver.KruskalMSTCalculatorTests;
import p3.transform.AdjacencyGraphTransformer;
import p3.util.SerializedEdge;
import p3.util.SerializedGraph;

import java.util.List;
import java.util.Set;

public class P3_RubricProvider implements RubricProvider {

    private static final Criterion H1_A_1 = makeCriterion(
        "Der Konstruktor von [[[BasicGraph]]] funktioniert korrekt für Graphen mit 2 Knoten.",
        JUnitTestRef.ofMethod(() -> BasicGraphTests.class.getDeclaredMethod("testTwoNodes", Set.class, Set.class))
    );
    private static final Criterion H1_A_2 = makeCriterion(
        "Der Konstruktor von [[[BasicGraph]]] funktioniert korrekt für Graphen mit 3 Knoten.",
        JUnitTestRef.ofMethod(() -> BasicGraphTests.class.getDeclaredMethod("testThreeNodes", Set.class, Set.class))
    );
    private static final Criterion H1_A_3 = makeCriterion(
        "Der Konstruktor von [[[BasicGraph]]] funktioniert korrekt für Graphen mit mehr als 3 Knoten.",
        JUnitTestRef.ofMethod(() -> BasicGraphTests.class.getDeclaredMethod("testMultipleNodes", Set.class, Set.class))
    );
    private static final Criterion H1_A = Criterion.builder()
        .shortDescription("H1 a) BasicGraph")
        .addChildCriteria(H1_A_1, H1_A_2, H1_A_3)
        .build();

    private static final Criterion H1_B_1 = makeCriterion(
        "[[[addEdge]]] funktioniert korrekt.",
        JUnitTestRef.ofMethod(() -> AdjacencyMatrixTests.class.getDeclaredMethod("testAddEdge", int.class, SerializedEdge.class))
    );
    private static final Criterion H1_B_2 = makeCriterion(
        "[[[getWeight]]] funktioniert korrekt.",
        JUnitTestRef.ofMethod(() -> AdjacencyMatrixTests.class.getDeclaredMethod("testGetWeight", int.class, SerializedEdge.class))
    );
    private static final Criterion H1_B_3 = makeCriterion(
        "[[[getAdjacent]]] funktioniert korrekt.",
        JUnitTestRef.ofMethod(() -> AdjacencyMatrixTests.class.getDeclaredMethod("testGetAdjacent", int.class, List.class))
    );
    private static final Criterion H1_B = Criterion.builder()
        .shortDescription("H1 b) AdjacencyMatrix")
        .addChildCriteria(H1_B_1, H1_B_2, H1_B_3)
        .build();

    private static final Criterion H1_C_1 = makeCriterion(
        "[[[nodeIndices]]] und [[[indexNodes]]] werden im Konstruktor von [[[AdjacencyGraph]]] korrekt gesetzt.",
        JUnitTestRef.ofMethod(() -> AdjacencyGraphTests.class.getDeclaredMethod("testConstructorMaps", Set.class, Set.class))
    );
    private static final Criterion H1_C_2 = makeCriterion(
        "[[[edges]]] wird im Konstruktor von [[[AdjacencyGraph]]] korrekt gesetzt.",
        JUnitTestRef.ofMethod(() -> AdjacencyGraphTests.class.getDeclaredMethod("testConstructorEdges", Set.class, Set.class))
    );
    private static final Criterion H1_C_3 = makeCriterion(
        "[[[getAdjacentEdges]]] funktioniert korrekt, wenn alle Kanten mit mehr als 0 gewichtet sind.",
        JUnitTestRef.ofMethod(() -> AdjacencyGraphTests.class.getDeclaredMethod("testGetAdjacentEdgesPositiveWeight", Set.class, Set.class))
    );
    private static final Criterion H1_C_4 = makeCriterion(
        "[[[getAdjacentEdges]]] funktioniert vollständig korrekt.",
        JUnitTestRef.ofMethod(() -> AdjacencyGraphTests.class.getDeclaredMethod("testGetAdjacentEdgesWithZeroWeights", Set.class, Set.class))
    );

    private static final Criterion H1_C = Criterion.builder()
        .shortDescription("H1 c) AdjacencyGraph")
        .addChildCriteria(H1_C_1, H1_C_2, H1_C_3, H1_C_4)
        .build();

    private static final Criterion H1 = Criterion.builder()
        .shortDescription("H1: Graphenrepräsentation")
        .addChildCriteria(H1_A, H1_B, H1_C)
        .build();

    private static final Criterion H2_A = Criterion.builder()
        .shortDescription("H2 a) [[[Edge.compareTo]]]")
        .addChildCriteria(makeCriterion(
            "[[[Edge.compareTo]]] funktioniert korrekt.",
            JUnitTestRef.ofMethod(() -> EdgeTests.class.getDeclaredMethod("testCompareTo", SerializedEdge.class, SerializedEdge.class, int.class))
        ))
        .build();

    private static final Criterion H2_B = Criterion.builder()
        .shortDescription("H2 b) [[[init]]]")
        .addChildCriteria(makeCriterion(
            "[[[init]]] funktioniert korrekt.",
            JUnitTestRef.ofMethod(() -> KruskalMSTCalculatorTests.class.getDeclaredMethod("testInit", SerializedGraph.class))
        ))
        .build();

    private static final Criterion H2_C_1 = makeUngradedCriterion(
        "Alle Gruppen beinhalten die korrekten Werte nach Aufruf von [[[joinGroups]]]."
    );
    private static final Criterion H2_C_2 = makeUngradedCriterion(
        "[[[joinGroups]]] fügt die Werte in die größere der beiden Mengen ein."
    );
    private static final Criterion H2_C = Criterion.builder()
        .shortDescription("H2 c) [[[joinGroups]]]")
        .addChildCriteria(H2_C_1, H2_C_2)
        .build();

    private static final Criterion H2_D_1 = makeUngradedCriterion(
        "[[[acceptEdge]]] funktioniert korrekt, wenn beide Knoten in der gleichen Menge sind."
    );
    private static final Criterion H2_D_2 = makeUngradedCriterion(
        "[[[acceptEdge]]] funktioniert korrekt, wenn beide Knoten in unterschiedlichen Mengen sind."
    );
    private static final Criterion H2_D_3 = makeUngradedCriterion(
        "[[[acceptEdge]]] ruft [[[joinGroups]]] an der richtigen Stelle auf."
    );
    private static final Criterion H2_D_4 = makeUngradedCriterion(
        "[[[acceptEdge]]] gibt die korrekten Werte zurück."
    );
    private static final Criterion H2_D = Criterion.builder()
        .shortDescription("H2 d) [[[acceptEdge]]]")
        .addChildCriteria(H2_D_1, H2_D_2, H2_D_3, H2_D_4)
        .build();

    private static final Criterion H2_E_1 = makeUngradedCriterion(
        "[[[calculateMST]]] ruft [[[acceptEdge]]] mit allen Kanten auf."
    );
    private static final Criterion H2_E_2 = makeUngradedCriterion(
        "[[[calculateMST]]] funktioniert vollständig korrekt."
    );
    private static final Criterion H2_E = Criterion.builder()
        .shortDescription("H2 e) [[[calculateMST]]]")
        .addChildCriteria(H2_E_1, H2_E_2)
        .build();

    private static final Criterion H2 = Criterion.builder()
        .shortDescription("H2: Kruskal")
        .addChildCriteria(H2_A, H2_B, H2_C, H2_D, H2_E)
        .build();

    private static final Criterion H3_A = Criterion.builder()
        .shortDescription("H3 a) [[[init]]]")
        .addChildCriteria(makeCriterion(
            "Methode [[[init]]] funktioniert vollständig korrekt.",
            JUnitTestRef.ofMethod(() -> DijkstraPathCalculatorTests.class.getDeclaredMethod("testInit", SerializedGraph.class, Object.class))
        ))
        .build();

    private static final Criterion H3_B_1 = makeCriterion(
        "[[[extractMin]]] funktioniert korrekt, wenn alle Knoten in [[[remainingNodes]]] sind.",
        JUnitTestRef.ofMethod(() -> DijkstraPathCalculatorTests.class.getDeclaredMethod("testExtractMinSimple", SerializedGraph.class, List.class, Object.class))
    );
    private static final Criterion H3_B_2 = makeCriterion(
        "[[[extractMin]]] funktioniert vollständig korrekt.",
        JUnitTestRef.ofMethod(() -> DijkstraPathCalculatorTests.class.getDeclaredMethod("testExtractMinFull", SerializedGraph.class, List.class, Set.class, Object.class))
    );
    private static final Criterion H3_B = Criterion.builder()
        .shortDescription("H3 b) [[[extractMin]]]")
        .addChildCriteria(H3_B_1, H3_B_2)
        .build();

    private static final Criterion H3_C_1 = makeUngradedCriterion(
        "[[[relax]]] passt [[[distances]]] korrekt an."
    );
    private static final Criterion H3_C_2 = makeUngradedCriterion(
        "[[[relax]]] passt [[[predecessors]]] korrekt an."
    );
    private static final Criterion H3_C = Criterion.builder()
        .shortDescription("H3 c) [[[relax]]]")
        .addChildCriteria(H3_C_1, H3_C_2)
        .build();

    private static final Criterion H3_D = Criterion.builder()
        .shortDescription("H3 d) [[[reconstructPath]]]")
        .addChildCriteria(makeUngradedCriterion(
            "[[[reconstructPath]]] funktioniert vollständig korrekt.", 0, 2
        ))
        .build();

    private static final Criterion H3_E_1 = makeUngradedCriterion(
        "[[[calculatePath]]] funktioniert korrekt für Graphen mit 2 Knoten."
    );
    private static final Criterion H3_E_2 = makeUngradedCriterion(
        "[[[calculatePath]]] funktioniert korrekt für Graphen mit 3 Knoten."
    );
    private static final Criterion H3_E_3 = makeUngradedCriterion(
        "[[[calculatePath]]] funktioniert korrekt für Graphen mit mehr als 3 Knoten."
    );
    private static final Criterion H3_E = Criterion.builder()
        .shortDescription("H3 e) [[[calculatePath]]]")
        .addChildCriteria(H3_E_1, H3_E_2, H3_E_3)
        .build();

    private static final Criterion H3 = Criterion.builder()
        .shortDescription("H3: Dijkstra")
        .addChildCriteria(H3_A, H3_B, H3_C, H3_D, H3_E)
        .build();

    public static final Rubric RUBRIC = Rubric.builder()
        .title("P3")
        .addChildCriteria(H1, H2, H3)
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }

    @Override
    public void configure(RubricConfiguration configuration) {
        RubricProvider.super.configure(configuration);
        configuration.addTransformer(new AdjacencyGraphTransformer());
    }

    private static Criterion makeCriterion(String description, JUnitTestRef... jUnitTestRefs) {
        return makeCriterion(description, 0, 1, jUnitTestRefs);
    }

    private static Criterion makeCriterion(String description, int minPoints, int maxPoints, JUnitTestRef... jUnitTestRefs) {
        Grader.TestAwareBuilder graderBuilder = Grader.testAwareBuilder();
        for (JUnitTestRef jUnitTestRef : jUnitTestRefs) {
            graderBuilder.requirePass(jUnitTestRef);
        }

        return Criterion.builder()
            .shortDescription(description)
            .minPoints(minPoints)
            .maxPoints(maxPoints)
            .grader(graderBuilder
                .pointsFailedMin()
                .pointsPassedMax()
                .build())
            .build();
    }

    private static Criterion makeUngradedCriterion(String description) {
        return makeUngradedCriterion(description, 0, 1);
    }

    private static Criterion makeUngradedCriterion(String description, int minPoints, int maxPoints) {
        return Criterion.builder()
            .shortDescription(description)
            .minPoints(minPoints)
            .maxPoints(maxPoints)
            .grader((testCycle, criterion) -> GradeResult.of(minPoints, maxPoints, "Not graded by public grader"))
            .build();
    }
}
