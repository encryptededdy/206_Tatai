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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import tatai.app.util.DevQuotes;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

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

        // Setup the quote animation and text
        DevQuotes.generateQuote(devQuote);
    }
    @FXML private JFXButton levelBtn;
    @FXML private ImageView backgroundImage;
    @FXML private JFXComboBox<String> questionDropDown;
    @FXML private Pane mainPane, mainDataPane;
    @FXML private JFXButton statisticsBtn, logoutBtn, closeBtn, netBtn, settingsBtn, practiceBtn;
    @FXML private Rectangle fadeBox;
    @FXML private Label devQuote;

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

    @FXML private void practiceBtnPressed() throws IOException {
        switchToToolbar(Layout.PRACTICE);
    }

    @FXML private void levelBtnPressed() throws IOException {
        Scene scene = levelBtn.getScene();
        FXMLLoader loader = Layout.LEVEL.loader();
        Parent root = loader.load();
        FadeTransition ft = TransitionFactory.fadeOut(mainDataPane, (int)(Main.transitionDuration*0.5));
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<LevelSelectorController>getController().fadeIn();});
        ft.play();
    }

    /**
     * Opens the settings screen when the settings button is pressed
     */
    @FXML private void settingsBtnPressed() throws IOException {
        Scene scene = settingsBtn.getScene();
        FXMLLoader loader = Layout.SETTINGS.loader();
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
        switchToToolbar(Layout.DASHBOARD);
    }

    /**
     * Logs out the current users when logout is pressed
     */
    @FXML private void logoutBtnPressed() throws IOException {
        Main.database.storeStore(); // save store info
        Main.database.stopSession();
        Main.currentUser = null;
        Main.currentSession = 0;

        // Load the new scene
        Scene scene = logoutBtn.getScene();
        FXMLLoader loader = Layout.LOGIN.loader();
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
        switchToToolbar(Layout.TATAINET);
    }

    @FXML private void storeBtnPressed() throws IOException {
        switchToToolbar(Layout.STORE);
    }

    /**
     * Animate to a ToolbarController screen
     * @param target The layout to switch to
     * @throws IOException Thrown on FXML load failure
     */
    private void switchToToolbar(Layout target) throws IOException {
        // Load the new scene
        Scene scene = practiceBtn.getScene();
        FXMLLoader loader = target.loader();
        Parent root = loader.load();
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainDataPane, (int) (Main.transitionDuration * 0.5));
        // Expand
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), mainPane);
        st.setToX(2);
        st.setOnFinished(event1 -> {
            scene.setRoot(root);
            loader.<ToolbarController>getController().fadeIn();
        }); // switch scenes when fade complete
        ft.setOnFinished(event -> st.play());
        ft.play();
    }
}
