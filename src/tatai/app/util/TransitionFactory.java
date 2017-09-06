package tatai.app.util;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import tatai.app.Main;

/*
Generates objects used in the transitions of scenes
 */
public class TransitionFactory {
    /*
        Generates a FadeTransition object to fade the target in from 0% opacity
    */
    public static FadeTransition fadeIn(Node target) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(Main.transitionDuration), target);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        return fadeIn;
    }

    /*
        Generates a FadeTransition object to fade the target out from 100% opacity
    */
    public static FadeTransition fadeOut(Node target) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(Main.transitionDuration), target);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        return fadeOut;
    }
}
