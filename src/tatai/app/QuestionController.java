package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.questions.Question;
import tatai.app.questions.generators.NumberGenerator;
import tatai.app.questions.generators.QuestionGenerator;

import java.io.IOException;

public class QuestionController {

    public void initialize() {
        // TODO: add fade in

        // Not final, just used to randomise number
        Question question = new Question(new NumberGenerator());
        questionLabel.setText(question.getQuestion());
    }

    @FXML
    private JFXButton recordBtn;

    @FXML
    private JFXButton checkBtn;

    @FXML
    private JFXButton menuBtn;

    @FXML
    private Label questionLabel;

    @FXML
    private Label questionNumberLabel;

    @FXML
    private Pane questionPane;

    @FXML
    private Pane controlsPane;


    @FXML
    void menuBtnPressed(ActionEvent event) throws IOException {
        Scene scene = menuBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        scene.setRoot(root);
    }

    @FXML
    void recordBtnPressed(ActionEvent event) {

    }

}
