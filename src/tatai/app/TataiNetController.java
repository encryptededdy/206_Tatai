package tatai.app;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.util.TransitionFactory;

import java.io.IOException;

public class TataiNetController {

    @FXML private ImageView backgroundImage;

    @FXML private Pane backgroundPane, dataPane, controls, backBtn, progressPane;

    @FXML private JFXListView<?> highscoreList;

    @FXML private JFXComboBox<String> scoreboardComboGameMode;

    @FXML private Label connectingLabel;

    /**
     * Populates the gamemode selector ComboBox
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);
        scoreboardComboGameMode.getItems().addAll(Main.questionGenerators.keySet());
        scoreboardComboGameMode.setValue(Main.questionGenerators.keySet().iterator().next()); // Automatically selects the first object.
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
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
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
