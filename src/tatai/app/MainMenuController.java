package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private JFXButton practiceNumbersBtn;

    @FXML
    private JFXButton practiceSumsBtn;

    @FXML
    private JFXButton settingsBtn;

    @FXML
    private JFXButton micTestBtn;

    @FXML
    private Pane mainPane;

    @FXML
    void practiceNumbersBtnPressed(ActionEvent event) throws IOException {
        // Load the new scene
        Scene scene = practiceNumbersBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();
        // Fade out
        FadeTransition ft = new FadeTransition(Duration.millis(Main.transitionDuration), mainPane);
        ft.setToValue(0);
        ft.play();
        ft.setOnFinished(event1 -> scene.setRoot(root)); // switch scenes when fade complete
    }

}
