package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import tatai.app.util.TransitionFactory;
import java.io.IOException;

/**
 * Controller for the Main Menu screen. Handles all button presses etc.
 * Layout is from mainmenu.fxml
 *
 * @author Edward
 * @author Zach
 */
public class MainMenuController {

    public void initialize() {
        questionDropDown.getItems().addAll(Main.questionGenerators.keySet());
        questionDropDown.setValue(Main.questionGenerators.keySet().iterator().next()); // Automatically selects the first object.
    }

    @FXML
    private JFXButton practiceBtn;

    @FXML
    private JFXButton settingsBtn;

    @FXML
    private JFXButton micTestBtn;

    @FXML
    private JFXButton closeBtn;

    @FXML
    private JFXComboBox<String> questionDropDown;

    @FXML
    private Pane mainPane;

    @FXML
    private Rectangle fadeBox;

    @FXML
    private void practiceBtnPressed(ActionEvent event) throws IOException {
        // Load the new scene
        Scene scene = practiceBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(questionDropDown.getValue()); // pass through the selected question set
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainPane);
        ft.setOnFinished(event1 -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();}); // switch scenes when fade complete
        ft.play();
    }

    @FXML
    private void closeApplication(ActionEvent event) {
        Stage mainStage = (Stage) closeBtn.getScene().getWindow();
        mainStage.close();
    }

    @FXML // Easter egg
    private void closeApplicationRight() {
        Stage mainStage = (Stage) closeBtn.getScene().getWindow();
        Media sound = new Media(getClass().getResource("resources/shutdown.wav").toString());
        MediaPlayer player = new MediaPlayer(sound);
        FadeTransition ft = new FadeTransition(Duration.millis(3200), fadeBox);
        ft.setToValue(1);
        ft.play();
        fadeBox.setVisible(true);
        player.play();
        player.setOnEndOfMedia(mainStage::close);
    }
}
