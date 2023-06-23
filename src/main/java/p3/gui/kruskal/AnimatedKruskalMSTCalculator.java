package p3.gui.kruskal;

import javafx.application.Platform;
import p3.graph.Edge;
import p3.graph.Graph;
import p3.gui.Animation;
import p3.solver.KruskalMSTCalculator;

import java.util.List;
import java.util.Set;

/**
 * A Kruskal MST calculator that animates the calculation.
 * <p>
 * It stops every time after the init or acceptEdge method is called.
 * </p>
 *
 * @param <N> the type of the nodes.
 */
public class AnimatedKruskalMSTCalculator<N> extends KruskalMSTCalculator<N> implements Animation {

    private static final Object syncObject = new Object();

    private final KruskalScene<N> kruskalScene;

    public AnimatedKruskalMSTCalculator(Graph<N> graph, KruskalScene<N> kruskalScene) {
        super(graph);
        this.kruskalScene = kruskalScene;
    }

    @Override
    protected boolean acceptEdge(Edge<N> edge) {
        boolean accepted = super.acceptEdge(edge);

        Platform.runLater(() -> kruskalScene.refresh(edge, accepted));
        waitUntilNextStep();

        return accepted;
    }

    @Override
    protected void init() {
        super.init();

        Platform.runLater(() -> kruskalScene.refresh(null, false));
        waitUntilNextStep();
    }

    @Override
    public void start() {
        Graph<N> mst = super.calculateMST();

        Platform.runLater(() -> kruskalScene.showResult(mst));
    }

    @Override
    public Object getSyncObject() {
        return syncObject;
    }

    public List<Set<N>> getMstGroups() {
        return mstGroups;
    }

    private void waitUntilNextStep() {
        synchronized (syncObject) {
            try {
                syncObject.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
