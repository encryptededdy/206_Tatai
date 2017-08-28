package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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

    public void initialize() {
        questionDropDown.getItems().addAll(
                "Numbers",
                "Sums",
                "Something else",
                "Lorem Ipsum");
        questionDropDown.setValue("Numbers");
    }

    @FXML
    private JFXButton practiceBtn;

    @FXML
    private JFXButton settingsBtn;

    @FXML
    private JFXButton micTestBtn;

    @FXML
    private JFXComboBox<String> questionDropDown;

    @FXML
    private Pane mainPane;

    @FXML
    void practiceBtnPressed(ActionEvent event) throws IOException {
        // Load the new scene
        Scene scene = practiceBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(questionDropDown.getValue());
        // Fade out
        FadeTransition ft = new FadeTransition(Duration.millis(Main.transitionDuration), mainPane);
        ft.setToValue(0);
        ft.play();
        ft.setOnFinished(event1 -> scene.setRoot(root)); // switch scenes when fade complete
    }
}
