package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.questions.Question;
import tatai.app.questions.generators.NumberGenerator;
import tatai.app.questions.generators.NumberGenerator99;
import tatai.app.questions.generators.QuestionGenerator;

import java.io.IOException;

public class QuestionController {

    private QuestionGenerator _generator;

    private Question _currentQuestion;

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
        // TODO: This is ugly
        switch (questionSet) {
            case "Numbers":
                _generator = new NumberGenerator();
                break;
            case "Tens Numbers":
                _generator = new NumberGenerator99();
                break;
            default:
                throw new RuntimeException("Unrecognised Question Generator - "+questionSet);
        }
        generateQuestion();
    }

    private void generateQuestion() {
        // Not final
        _currentQuestion = new Question(_generator);
        questionLabel.setText(_currentQuestion.toString());

        // DEBUG (or is it DeFailure ~ewan) Code
        System.out.println("Asking: " + _currentQuestion);
        System.out.println("Answer: " + _currentQuestion.getAnswer());
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
        // TODO: Actually implement the recording logic
        checkBtn.setDisable(false);
    }

    @FXML
    void checkBtnPressed(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Wow this is informational");
        alert.setContentText("The correct answer was: " + _currentQuestion.getAnswer());
        alert.showAndWait();
        generateQuestion();
    }

    @FXML
    void playBtnPressed(ActionEvent event) {
        // TODO
    }

}
