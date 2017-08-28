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

    private QuestionGenerator _generator;

    public void initialize() {
        // TODO: add fade in
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

    public void setQuestionSet(String questionSet) {
        switch (questionSet) {
            case "Numbers":
                _generator = new NumberGenerator();
                break;
            default:
                throw new RuntimeException("Unrecognised Question Generator - "+questionSet);
        }
        generateQuestion();
    }

    private void generateQuestion() {
        // Not final
        Question question = new Question(_generator);
        questionLabel.setText(question.getQuestion());
    }

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
