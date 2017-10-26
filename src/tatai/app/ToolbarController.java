package tatai.app;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

/**
 * Abstract controller used for toolbar screens (CustomGenerator, TataiNet, Dashboard)
 *
 * @author Edward
 */
public abstract class ToolbarController {
    @FXML protected ImageView backgroundImage;
    @FXML protected Pane dataPane, backBtn, backgroundPane;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        dataPane.setOpacity(0);
    }

    /**
     * Fades in the screen (to animate in)
     */
    void fadeIn() {
        TransitionFactory.fadeIn(dataPane).play();
    }

    /**
     * Animate out the screen then switch to the main menu
     * @throws IOException Exception can be thrown when loading FXML
     */
    @FXML void backBtnPressed() throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = Layout.MAINMENU.loader();
        Parent root = loader.load();
        loader.<MainMenuController>getController().setupFade(false);
        // Fade out items
        FadeTransition ft = TransitionFactory.fadeOut(dataPane, Main.transitionDuration/2);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), backgroundPane);
        st.setToX(0.5);
        st.setOnFinished(event -> {scene.setRoot(root); loader.<MainMenuController>getController().fadeIn();});
        ft.setOnFinished(event -> st.play());
        // animate
        ft.play();
    }
}
