package p3.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * A scene for displaying an animation.
 */
public abstract class AnimationScene extends Scene {

    /**
     * Constructs a new animation scene.
     *
     * @param root the root node of the scene
     */
    public AnimationScene(Parent root) {
        super(root);
    }

    /**
     * Gets the title of the scene.
     *
     * @return the title of the scene
     */
    public abstract String getTitle();
}
