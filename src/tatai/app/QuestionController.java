package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.questions.Question;
import tatai.app.questions.Round;
import tatai.app.questions.generators.NumberGenerator;
import tatai.app.questions.generators.NumberGenerator99;
import tatai.app.questions.generators.QuestionGenerator;

import java.io.IOException;

public class QuestionController {

    private Round _currentRound;

    @FXML
    private JFXButton recordBtn;

    @FXML
    private JFXButton playBtn;

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

    public void initialize() {
        // Setup for the transition
        questionPane.setOpacity(0);
        controlsPane.setOpacity(0);
    }

    void fadeIn() {
        // Fade in
        FadeTransition ft1 = new FadeTransition(Duration.millis(Main.transitionDuration), questionPane);
        //ft1.setFromValue(0.0);
        ft1.setToValue(1.0);
        FadeTransition ft2 = new FadeTransition(Duration.millis(Main.transitionDuration), controlsPane);
        //ft2.setFromValue(0.0);
        ft2.setToValue(1.0);
        ft1.play();
        ft2.play();
    }

    public void setQuestionSet(String questionSet) {
        // TODO: This is ugly
        QuestionGenerator generator;
        switch (questionSet) {
            case "Numbers":
                generator = new NumberGenerator();
                break;
            case "Tens Numbers":
                generator = new NumberGenerator99();
                break;
            default:
                throw new RuntimeException("Unrecognised Question Generator - "+questionSet);
        }
        _currentRound = new Round(generator, 10); // TODO: numQuestions shouldn't be hardcoded
        generateQuestion();
    }

    private void generateQuestion() {
        // Fade out
        FadeTransition fade = new FadeTransition(Duration.millis(Main.transitionDuration), questionLabel);
        fade.setToValue(0);
        fade.play();
        fade.setOnFinished(event -> {
            if (_currentRound.hasNext()) {
                questionLabel.setText(_currentRound.next().toString());
                questionNumberLabel.setText("Question "+_currentRound.questionNumber());
                checkBtn.setDisable(true);
                playBtn.setDisable(true);
                // Fade in
                FadeTransition fadeIn = new FadeTransition(Duration.millis(Main.transitionDuration), questionLabel);
                fadeIn.setToValue(1);
                fadeIn.play();
            } else {
                // no more questions! Show completion screen
                // TODO: Completion screen.
                System.out.println("this is the part where you show the completion screen");
            }
        });
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
        playBtn.setDisable(false);
    }

    @FXML
    void checkBtnPressed(ActionEvent event) {
        // TODO: Do HTK, Check the answer, and do something if the answer is wrong.
        generateQuestion();
    }

    @FXML
    void playBtnPressed(ActionEvent event) {
        // TODO
    }

}
