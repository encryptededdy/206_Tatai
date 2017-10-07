package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
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

    /**
     * Populates the questionDropDown with the questionGenerators stored in Main. Preselects the first option
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);

        questionDropDown.getItems().addAll(Main.questionGenerators.keySet());
        questionDropDown.setValue(Main.questionGenerators.keySet().iterator().next()); // Automatically selects the first object.
    }
    @FXML private JFXButton practiceBtn;
    @FXML private ImageView backgroundImage;
    @FXML private JFXButton settingsBtn;
    @FXML private JFXButton netBtn;
    @FXML private JFXButton closeBtn;
    @FXML private JFXComboBox<String> questionDropDown;
    @FXML private Pane mainPane;
    @FXML private Pane mainDataPane;
    @FXML private JFXButton statisticsBtn;
    @FXML private Rectangle fadeBox;
    @FXML private JFXButton logoutBtn;

    /**
     * These classes set up transitions INTO MainMenuController;
     */
    void setupFade(Boolean fadeImage) {
        if (fadeImage) {
            backgroundImage.setOpacity(0);
        }
        mainDataPane.setOpacity(0);
    }

    void fadeIn() {
        if (backgroundImage.getOpacity() == 0) {
            TransitionFactory.fadeIn(backgroundImage).play();
        }
        TransitionFactory.fadeIn(mainDataPane).play();
    }

    /**
     * Switches scenes to begin the game (loads questionscreen.fxml) and passes the QuestionController the Question
     * set to be used (from questionDropDown)
     */
    @FXML private void practiceBtnPressed() throws IOException {
        // Load the new scene
        Scene scene = practiceBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(questionDropDown.getValue()); // pass through the selected question set
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainDataPane, (int)(Main.transitionDuration*0.5));
        // Shrink anim
        ScaleTransition st = new ScaleTransition(Duration.millis((int)(Main.transitionDuration*1.5)), mainPane);
        st.setToY(0.544);
        st.setToX(1.025);
        // Move anim
        TranslateTransition tt = TransitionFactory.move(mainPane, 0, (int)(-73*0.544), (int)(Main.transitionDuration*1.5));
        ParallelTransition pt = new ParallelTransition(st, tt);
        ft.setOnFinished(event1 -> pt.play()); // play the shrink anim when fade finished
        pt.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        ft.play();
    }

    /**
     * Opens the settings screen when the settings button is pressed
     */
    @FXML private void settingsBtnPressed() throws IOException {
        Scene scene = settingsBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.settingsLayout);
        Parent root = loader.load();
        FadeTransition ft = TransitionFactory.fadeOut(mainDataPane, (int)(Main.transitionDuration*0.5));
        FadeTransition ft2 = TransitionFactory.fadeOut(backgroundImage, (int)(Main.transitionDuration*0.5));
        ParallelTransition pt = new ParallelTransition(ft, ft2);
        TranslateTransition tt = TransitionFactory.move(mainPane, -370, 0, (int)(Main.transitionDuration*1.5));
        pt.setOnFinished(event1 -> tt.play()); // move after bkgnd has faded
        tt.setOnFinished(event -> {scene.setRoot(root); loader.<SettingsController>getController().fadeIn();});
        pt.play();
    }

    /**
     * Opens the statistics screen when the statistics button is pressed
     */
    @FXML private void statisticsBtnPressed() throws IOException {
        // Load the new scene
        Scene scene = statisticsBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.dashboardLayout);
        Parent root = loader.load();
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainDataPane, (int)(Main.transitionDuration*0.5));
        // Expand
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), mainPane);
        st.setToX(2);
        st.setOnFinished(event1 -> {scene.setRoot(root); loader.<DashboardController>getController().fadeIn();}); // switch scenes when fade complete
        ft.setOnFinished(event -> st.play());
        ft.play();
    }

    /**
     * Logs out the current users when logout is pressed
     */
    @FXML private void logoutBtnPressed() throws IOException {
        Main.database.stopSession();
        Main.currentUser = null;
        Main.currentSession = 0;

        // Load the new scene
        Scene scene = logoutBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.loginLayout);
        Parent root = loader.load();
        scene.setRoot(root);
    }

    /**
     * Closes the application when the close button is pressed.
     */
    @FXML private void closeApplication() {
        Stage mainStage = (Stage) closeBtn.getScene().getWindow();
        Main.onClose();
        mainStage.close();
    }

    /**
     * Easter egg when you right click the close button... shhh!
     */
    @FXML private void closeApplicationRight() {
        Stage mainStage = (Stage) closeBtn.getScene().getWindow();
        Media sound = new Media(getClass().getResource("resources/shutdown.wav").toString());
        MediaPlayer player = new MediaPlayer(sound);
        FadeTransition ft = new FadeTransition(Duration.millis(3200), fadeBox);
        ft.setToValue(1);
        ft.play();
        fadeBox.setVisible(true);
        player.play();
    }

    @FXML private void netBtnPressed() throws IOException {
        // Load the new scene
        Scene scene = netBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.tatainetLayout);
        Parent root = loader.load();
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainDataPane, (int)(Main.transitionDuration*0.5));
        // Expand
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), mainPane);
        st.setToX(2);
        st.setOnFinished(event1 -> {scene.setRoot(root); loader.<TataiNetController>getController().fadeIn();}); // switch scenes when fade complete
        ft.setOnFinished(event -> st.play());
        ft.play();
    }
}
