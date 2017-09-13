package tatai.app;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
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

    @FXML
    private ImageView backgroundImage;

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
                _currentRound.finish();
                // no more questions! Show completion screen
                // Load the new scene
                Scene scene = playBtn.getScene();
                FXMLLoader loader = new FXMLLoader(Main.completeLayout);
                try {
                    Parent root = loader.load();
                    loader.<CompleteScreenController>getController().setMostRecentRound(_currentRound);
                    loader.<CompleteScreenController>getController().executeRecentRoundQuery();
                    // Fade out
                    FadeTransition ft0 = TransitionFactory.fadeOut(questionNumberLabel);
                    FadeTransition ft2 = TransitionFactory.fadeOut(recordBtn);
                    FadeTransition ft3 = TransitionFactory.fadeOut(playBtn);
                    FadeTransition ft4 = TransitionFactory.fadeOut(checkBtn);
                    ft4.setOnFinished(event1 -> {scene.setRoot(root); loader.<CompleteScreenController>getController().fadeIn();}); // switch scenes when fade complete
                    ft0.play();
                    ft2.play();
                    ft3.play();
                    ft4.play();
                } catch (IOException iox) {
                    // completescreen.fxml missing
                    iox.printStackTrace();
                }

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

    // When questionpane is rightclicked - easter egg
    @FXML
    void questionRightClick() {
        System.out.println("activated");
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(-1);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        KeyValue kv = new KeyValue(colorAdjust.hueProperty(), 1.0);
        KeyFrame kf = new KeyFrame(Duration.seconds(3), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        backgroundImage.setEffect(colorAdjust);
    }

    @FXML
    void recordBtnPressed() {
        // TODO: Actually implement the recording logic
        answerRecording = new Record();
        playBtn.setDisable(true);
        checkBtn.setDisable(true);
        recordBtn.setDisable(true);
        answerRecording.setOnFinished(event1 -> { // set what to do when we finish recording
            playBtn.setDisable(false);
            checkBtn.setDisable(false);
            recordBtn.setDisable(false);
            recordBtn.setStyle("-fx-background-color: #3F51B5;");
        });
        recordBtn.setStyle("-fx-background-color: #F44336;");
        answerRecording.record(2000);
        recordBtn.setDefaultButton(false);
        checkBtn.setDefaultButton(true);
    }

    @FXML
    void checkBtnPressed() {
        checkBtn.setDisable(true);
        // If the answer is correct, call answerCorrect();
        // If not, call answerIncorrect();
        String userAnswer = answerRecording.speechToText();
        System.out.println(userAnswer);
        checkBtn.setDefaultButton(false);
        if (_currentRound.checkAnswer(userAnswer)) {
            answerCorrect();
        } else {
            answerIncorrect();
        }
        if (!_currentRound.hasNext()) {
            nextQuestionBtn.setText("End Round");
        }
    }

    @FXML
    void playBtnPressed() {
        answerRecording.play();
    }

    @FXML
    void nextBtnPressed() {
        // Ok, move on to the next question.
        nextQuestionBtn.setDefaultButton(false);
        recordBtn.setDefaultButton(true);
        generateQuestion();
    }

    private void answerCorrect() {
        nextQuestionBtn.setVisible(true); // show the next question btn
        incorrectIcon.setVisible(false);
        correctIcon.setVisible(true); // show the tick
        resultsLabel.setText("Correct!");
        resultsPane.setOpacity(0);
        resultsPane.setVisible(true);
        nextQuestionBtn.setDefaultButton(true);
        TransitionFactory.fadeIn(resultsPane).play();
    }

    private void answerIncorrect() {
        incorrectIcon.setVisible(true);
        correctIcon.setVisible(false); // show the cross
        playBtn.setDisable(true); // avoid the user accidentally playing back existing recording
        if (_currentRound.isLastAttempt()) { // Last attempt, so show the answer and allow to proceed to the next question
            nextQuestionBtn.setVisible(true); // show next question btn
            nextQuestionBtn.setDefaultButton(true);
            resultsLabel.setText("Correct answer: " + _currentRound.currentAnswer());
        } else { // First attempt, prompt user to try again.
            resultsLabel.setText("Please try again.");
            recordBtn.setDefaultButton(true);
        }
        resultsPane.setOpacity(0);
        resultsPane.setVisible(true);
        TransitionFactory.fadeIn(resultsPane).play();
    }

}
