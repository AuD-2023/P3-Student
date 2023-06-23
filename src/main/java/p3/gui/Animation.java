package p3.gui;

/**
 * An interface for animations that can be visualized.
 * The animation is started by calling {@link #start()}.
 * The implementations of this interface are responsible for calling {@link Object#wait()} to wait for the next step and update the visualization before waiting.
 * To avoid blocking the JavaFX thread, the {@link #start()} method should be called from a new thread.
 * To continue the animation, {@code getSyncObject().notify()} should be called from the JavaFX thread.
 */
public interface Animation {

    /**
     * Starts the animation.
     * This method should be called from a new thread to avoid blocking the JavaFx thread.
     */
    void start();

    /**
     * Returns the object that is used for synchronization.
     * This object is used to call {@link Object#wait()} and {@link Object#notify()}.
     * Calling {@link Object#notify()} on this object should be done from the JavaFX thread and causes the animation to execute the next step.
     *
     * @return the object that is used for synchronization
     */
    Object getSyncObject();
}
