package tatai.app.util;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import tatai.app.Main;

/**
 * Generates objects used in the transitions of scenes
 */
public class TransitionFactory {
    /**
     * Generates a FadeTransition object to fade the target in from 0% opacity
     * @param target The JavaFX node to be faded
     * @return The FadeTransition object representing the transition
     */
    public static FadeTransition fadeIn(Node target) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(Main.transitionDuration), target);
        fadeIn.setToValue(1);
        return fadeIn;
    }

    /**
     * Generates a FadeTransition object to fade the target out from 100% opacity
     * @param target The JavaFX node to be faded
     * @return The FadeTransition object representing the transition
     */
    public static FadeTransition fadeOut(Node target) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(Main.transitionDuration), target);
        fadeOut.setToValue(0);
        return fadeOut;
    }

    public static FadeTransition fadeOut(Node target, int millis) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(millis), target);
        fadeOut.setToValue(0);
        return fadeOut;
    }
}
