package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import tatai.app.questions.Round;
import tatai.app.util.TransitionFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CompleteScreenController {
    Round mostRecentRound;

    @FXML
    private JFXButton menuBtn;

    @FXML
    private JFXButton roundStatsBtn;

    @FXML
    private JFXButton replayBtn;

    @FXML
    private JFXButton nextRoundBtn;

    @FXML
    private Label scoreMessageLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label yourScoreLabel;

    @FXML
    private Pane mainPane;

    @FXML
    private Pane scorePane;

    @FXML
    private Pane controlsPane;

    public void initialize() {
        scoreMessageLabel.setOpacity(0);
        yourScoreLabel.setOpacity(0);
        scoreLabel.setOpacity(0);
        roundStatsBtn.setOpacity(0);
        replayBtn.setOpacity(0);
        nextRoundBtn.setOpacity(0);
    }

    void fadeIn() {
        TransitionFactory.fadeIn(scoreMessageLabel).play();
        TransitionFactory.fadeIn(yourScoreLabel).play();
        TransitionFactory.fadeIn(scoreLabel).play();
        TransitionFactory.fadeIn(roundStatsBtn).play();
        TransitionFactory.fadeIn(replayBtn).play();
        TransitionFactory.fadeIn(nextRoundBtn).play();
    }

    @FXML
    void menuBtnPressed(ActionEvent event) throws IOException {
        Scene scene = menuBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        scene.setRoot(root);
    }

    @FXML
    void roundStatsBtnPressed() {
        // TODO Implement this
    }

    @FXML
    void replayBtnPressed() throws IOException {
        // Load the new scene
        Scene scene = replayBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet("Numbers"); // TODO detect current question Generator
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainPane);
        ft.setOnFinished(event1 -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();}); // switch scenes when fade complete
        ft.play();
    }

    @FXML
    void nextRoundBtnPressed() throws IOException{
        // Load the new scene
        Scene scene = replayBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet("Tens Numbers"); // TODO detect current question Generator
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainPane);
        ft.setOnFinished(event1 -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();}); // switch scenes when fade complete
        ft.play();
    }

    public void setMostRecentRound(Round round) {

    }
}