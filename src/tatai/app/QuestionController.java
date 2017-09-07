package tatai.app;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import tatai.app.questions.Round;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Record;
import tatai.app.util.TransitionFactory;

import java.io.IOException;

public class QuestionController {

    private Round _currentRound;
    private Record answerRecording;

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

    @FXML
    private Pane resultsPane;

    @FXML
    private Label resultsLabel;

    @FXML
    private MaterialDesignIconView correctIcon;

    @FXML
    private MaterialDesignIconView incorrectIcon;

    @FXML
    private JFXButton nextQuestionBtn;

    public void initialize() {
        // Setup for the transition
        questionPane.setOpacity(0);
        controlsPane.setOpacity(0);
    }

    void fadeIn() {
        TransitionFactory.fadeIn(questionPane).play();
        TransitionFactory.fadeIn(controlsPane).play();
    }

    void setQuestionSet(String questionSet) {
        QuestionGenerator generator;
        generator = Main.questionGenerators.get(questionSet);
        _currentRound = new Round(generator, 10); // TODO: numQuestions shouldn't be hardcoded
        generateQuestion();
    }

    private void generateQuestion() {
        // Fade out
        FadeTransition fade = TransitionFactory.fadeOut(questionLabel);
        fade.setOnFinished(event -> {
            if (_currentRound.hasNext()) {
                questionLabel.setText(_currentRound.next());
                questionNumberLabel.setText("Question "+_currentRound.questionNumber());
                playBtn.setDisable(true);
                // Hide the question feedback / results
                nextQuestionBtn.setVisible(false);
                resultsPane.setVisible(false);
                // Fade in
                TransitionFactory.fadeIn(questionLabel).play();
            } else {
                // no more questions! Show completion screen
                // TODO: Completion screen.
                System.out.println("this is the part where you show the completion screen");
            }
        });
        fade.play();
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
        answerRecording = new Record();
        playBtn.setDisable(true);
        checkBtn.setDisable(true);
        answerRecording.setOnFinished(event1 -> { // set what to do when we finish recording
            playBtn.setDisable(false);
            checkBtn.setDisable(false);
        });
        answerRecording.record(2000);
    }

    @FXML
    void checkBtnPressed(ActionEvent event) {
        checkBtn.setDisable(true);
        // TODO: Do HTK, Check the answer
        // If the answer is correct, call answerCorrect();
        // If not, call answerIncorrect();

        // TESTING ONLY
        if (Math.random() < 0.5) {
            _currentRound.checkAnswer("incorrecttest");
            answerIncorrect();
        } else {
            answerCorrect();
        }
    }

    @FXML
    void playBtnPressed(ActionEvent event) {
        // TODO
        answerRecording.play();
    }

    @FXML
    void nextBtnPressed(ActionEvent event) {
        // Ok, move on to the next question.
        generateQuestion();
    }

    private void answerCorrect() {
        nextQuestionBtn.setVisible(true); // show the next question btn
        incorrectIcon.setVisible(false);
        correctIcon.setVisible(true); // show the tick
        resultsLabel.setText("Correct!");
        resultsPane.setOpacity(0);
        resultsPane.setVisible(true);
        TransitionFactory.fadeIn(resultsPane).play();
    }

    private void answerIncorrect() {
        incorrectIcon.setVisible(true);
        correctIcon.setVisible(false); // show the cross
        playBtn.setDisable(true); // avoid the user accidentally playing back existing recording
        if (_currentRound.isLastAttempt()) { // Last attempt, so show the answer and allow to proceed to the next question
            nextQuestionBtn.setVisible(true); // show next question btn
            resultsLabel.setText("Correct answer: " + _currentRound.currentAnswer());
        } else { // First attempt, prompt user to try again.
            resultsLabel.setText("Please try again.");
        }
        resultsPane.setOpacity(0);
        resultsPane.setVisible(true);
        TransitionFactory.fadeIn(resultsPane).play();
    }

}
