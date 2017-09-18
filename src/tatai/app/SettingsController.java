package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.util.TransitionFactory;

import java.io.IOException;

public class SettingsController {

    @FXML
    private Pane navPane;

    @FXML
    private Pane navPaneData;

    @FXML
    private Label username;

    @FXML
    private Label onlineUsername;

    @FXML
    private Pane gameSettingsBtn;

    @FXML
    private Pane generalSettingsBtn;

    @FXML
    private Pane cardPane;

    @FXML
    private Pane generalSettingsPane;

    @FXML
    private JFXCheckBox disableAnimCheckbox;

    @FXML
    private JFXCheckBox longerAnimCheckbox;

    @FXML
    private JFXButton clearDataButton;

    @FXML
    private JFXButton deleteUserButton;

    @FXML
    private Pane gameSettingsPane;

    @FXML
    private JFXCheckBox strictCheckingCheckbox;

    @FXML
    private JFXCheckBox autoRecordCheckbox;

    @FXML
    private JFXCheckBox longerRecordCheckbox;

    @FXML
    private Pane backBtn;

    @FXML
    private ImageView animImage;

    private Pane _frontPane;
    private ParallelTransition flyingTransition;

    public void initialize() {
        // setup for in animations
        navPaneData.setOpacity(0);
        cardPane.setOpacity(0);
        // Set initial values
        username.setText(Main.currentUser);
        if (Main.transitionDuration != 300) {
            longerAnimCheckbox.setSelected(true);
        }
        // Setup the page change transition
        _frontPane = gameSettingsPane;
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), animImage);
        st.setFromX(1);
        st.setFromY(1);
        st.setToY(1.25);
        st.setToX(1.25);
        st.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = TransitionFactory.fadeOut(animImage);
        ft.setInterpolator(Interpolator.EASE_OUT);
        ft.setFromValue(100);
        flyingTransition = new ParallelTransition(ft, st);
        flyingTransition.setOnFinished(event -> animImage.setVisible(false));
    }

    void fadeIn() {
        TransitionFactory.fadeIn(navPaneData).play();
        TransitionFactory.fadeIn(cardPane).play();
    }

    @FXML
    private void backBtnPressed() throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();

        loader.<MainMenuController>getController().setupFade(true); // setup for fade in
        TransitionFactory.fadeOut(navPaneData).play();
        FadeTransition ft = TransitionFactory.fadeOut(cardPane);
        TranslateTransition tt = TransitionFactory.move(navPane, 370, 0, (int)(Main.transitionDuration*1.5));
        ft.setOnFinished(event -> tt.play());
        tt.setOnFinished(event -> {scene.setRoot(root); loader.<MainMenuController>getController().fadeIn();});
        ft.play();
    }

    @FXML
    private void generalSettingsBtnPressed() {
        if (_frontPane != generalSettingsPane) {
            bringToFront(generalSettingsPane);
        }
        generalSettingsBtn.setStyle("-fx-background-color: #424242");
    }

    @FXML
    private void gameSettingsBtnPressed() {
        if (_frontPane != gameSettingsPane) {
            bringToFront(gameSettingsPane);
        }
        gameSettingsBtn.setStyle("-fx-background-color: #424242");
    }

    @FXML
    private void longerAnimCheckboxChanged() {
        if (longerAnimCheckbox.isSelected()) {
            Main.transitionDuration = 600;
        } else {
            Main.transitionDuration = 300;
        }
    }

    private void bringToFront(Pane pane) {
        // Load the animImage with the current front pane
        WritableImage snapshot = _frontPane.snapshot(new SnapshotParameters(), null);
        animImage.setImage(snapshot);
        animImage.setVisible(true);
        // Juggle the image to the front, with the pane just behind it.
        pane.toFront();
        animImage.toFront();
        // Animate it away
        flyingTransition.playFromStart();
        _frontPane = pane;

        // set all buttons as not highlighted;
        gameSettingsBtn.setStyle("");
        generalSettingsBtn.setStyle("");
    }



}
