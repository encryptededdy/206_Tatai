package tatai.app;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import tatai.app.questions.Round;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.PopoverFactory;
import tatai.app.util.Record;
import tatai.app.util.TransitionFactory;
import tatai.app.util.Translator;

import java.io.IOException;
import java.util.Random;

public class QuestionController {

    private Round _currentRound;
    private Record answerRecording;
    private TranslateTransition easterEggTT;
    private boolean easterEggEnabled = false;

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
    private Pane tutorialNotif;

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

    // Help Popups
    PopOver recordHelp = PopoverFactory.helpPopOver("Click the microphone icon to start recording your voice,\nthen pronounce the number on screen.\nThe microphone will be red while recording\nYou can also press [ENTER] to record");
    PopOver playHelp = PopoverFactory.helpPopOver("Click the play button to listen\nto your recording");
    PopOver checkHelp = PopoverFactory.helpPopOver("Click the check button to check\nyour pronunciation and move\nto the next question\nYou can also press [ENTER] to check");
    PopOver nextHelp = PopoverFactory.helpPopOver("Click the next button to contiune\nto the next question\nYou can also press [ENTER] to contiune");

    public void initialize() {
        // Setup for the transition
        questionPane.setOpacity(0);
        controlsPane.setOpacity(0);
    }

    void fadeIn() {
        TransitionFactory.fadeIn(questionPane).play();
        FadeTransition controlsTransition = TransitionFactory.fadeIn(controlsPane);
        if (Main.showTutorial) {
            controlsTransition.setOnFinished(event -> {
                TranslateTransition tt = new TranslateTransition();
                tt.setByY(60);
                tt.setDuration(Duration.millis(200));
                tt.setNode(tutorialNotif);
                tt.setInterpolator(Interpolator.EASE_OUT);
                tt.play();
                recordHelp.show(recordBtn, -5);
            });
        }
        controlsTransition.play();
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
                    loader.<CompleteScreenController>getController().executePreviousRoundScoreQuery();
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

    private void hideHelpTexts() {
        recordHelp.hide();
        playHelp.hide();
        checkHelp.hide();
        nextHelp.hide();
    }

    @FXML
    void menuBtnPressed(ActionEvent event) throws IOException {
        hideHelpTexts();
        Scene scene = menuBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        scene.setRoot(root);
    }

    // When questionpane is rightclicked - easter egg
    @FXML
    void questionRightClick() {
        easterEggEnabled = true;
        Random rng = new Random();
        System.out.println("activated");
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(-1);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        KeyValue kv = new KeyValue(colorAdjust.hueProperty(), 1.0);
        KeyFrame kf = new KeyFrame(Duration.seconds(2), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        // moving meme
        easterEggTT = new TranslateTransition();
        easterEggTT.setByY(rng.nextInt(50)-25);
        easterEggTT.setByX(rng.nextInt(50)-25);
        easterEggTT.setDuration(Duration.millis(100));
        easterEggTT.setNode(questionPane);
        easterEggTT.setInterpolator(Interpolator.LINEAR);
        easterEggTT.setOnFinished(event -> {
            easterEggTT.setByY(rng.nextInt(50)-25);
            easterEggTT.setByX(rng.nextInt(50)-25);
            easterEggTT.play();
        });
        easterEggTT.play();
        // rotating meme
        RotateTransition rt = new RotateTransition(Duration.seconds(2), questionPane);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();
        // scaling meme
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), questionPane);
        st.setByX(1.5f);
        st.setByY(1.5f);
        st.setCycleCount(Animation.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
        backgroundImage.setImage(new Image(getClass().getResourceAsStream("resources/bliss.jpg")));
        backgroundImage.setEffect(colorAdjust);
    }

    @FXML
    void recordBtnHover() {
        if (easterEggEnabled) {
            Random rng = new Random();
            controlsPane.setLayoutY(rng.nextInt(450));
        }
    }

    @FXML
    void recordBtnPressed() {
        // TODO: Actually implement the recording logic
        recordHelp.hide();
        answerRecording = new Record();
        playBtn.setDisable(true);
        checkBtn.setDisable(true);
        recordBtn.setDisable(true);
        answerRecording.setOnFinished(event1 -> { // set what to do when we finish recording
            playBtn.setDisable(false);
            checkBtn.setDisable(false);
            recordBtn.setDisable(false);
            recordBtn.setStyle("-fx-background-color: #3F51B5;");
            if (Main.showTutorial) {
                checkHelp.show(checkBtn, -5);
                playHelp.show(playBtn, -5);
            }
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
        playHelp.hide();
        checkHelp.hide();
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

    /**
     * When the disable button is pressed in the tutorial notification
     */
    @FXML
    void tutorialNotifDisabledPressed() {
        hideHelpTexts();
        Main.showTutorial = false;
        tutorialNotifOKPressed();
    }

    @FXML
    void tutorialNotifOKPressed() {
        TranslateTransition tt = new TranslateTransition();
        tt.setByY(-60);
        tt.setDuration(Duration.millis(200));
        tt.setNode(tutorialNotif);
        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.play();
    }

    @FXML
    void nextBtnPressed() {
        nextHelp.hide();
        Main.showTutorial = false;
        // Ok, move on to the next question.
        nextQuestionBtn.setDefaultButton(false);
        recordBtn.setDefaultButton(true);
        generateQuestion();
    }

    private void answerCorrect() {
        nextQuestionBtn.setVisible(true); // show the next question btn
        incorrectIcon.setVisible(false);
        correctIcon.setVisible(true); // show the tick
        resultsLabel.setText("Correct! (" + Translator.toDisplayable(_currentRound.currentAnswer()) + ")");
        resultsPane.setOpacity(0);
        resultsPane.setVisible(true);
        nextQuestionBtn.setDefaultButton(true);
        if (Main.showTutorial) {
            nextHelp.show(nextQuestionBtn, -5);
        }
        TransitionFactory.fadeIn(resultsPane).play();
    }

    private void answerIncorrect() {
        incorrectIcon.setVisible(true);
        correctIcon.setVisible(false); // show the cross
        playBtn.setDisable(true); // avoid the user accidentally playing back existing recording
        if (_currentRound.isLastAttempt()) { // Last attempt, so show the answer and allow to proceed to the next question
            nextQuestionBtn.setVisible(true); // show next question btn
            nextQuestionBtn.setDefaultButton(true);
            if (Main.showTutorial) {
                nextHelp.show(nextQuestionBtn, -5);
            }
            resultsLabel.setText("Correct answer: " + Translator.toDisplayable(_currentRound.currentAnswer()));
        } else { // First attempt, prompt user to try again.
            resultsLabel.setText("Please try again.");
            recordBtn.setDefaultButton(true);
        }
        resultsPane.setOpacity(0);
        resultsPane.setVisible(true);
        TransitionFactory.fadeIn(resultsPane).play();
    }

}
