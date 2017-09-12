package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import tatai.app.questions.Round;
import tatai.app.util.TransitionFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class CompleteScreenController {
    Round _mostRecentRound;

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

    @FXML
    private Pane roundStatsPane;

    @FXML
    private TableView resultsTable;

    @FXML
    private JFXButton statsChangeGraphBtn;

    public void initialize() {
        scoreMessageLabel.setOpacity(0);
        yourScoreLabel.setOpacity(0);
        scoreLabel.setOpacity(0);
        roundStatsBtn.setOpacity(0);
        replayBtn.setOpacity(0);
        nextRoundBtn.setOpacity(0);
        roundStatsPane.setLayoutY(500);
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
        if (roundStatsBtn.getText().equals("Round Stats")) {
            TranslateTransition tt = new TranslateTransition();
            tt.setByY(-485);
            tt.setDuration(Duration.millis(1000));
            tt.setNode(roundStatsPane);
            tt.setInterpolator(Interpolator.EASE_BOTH);
            tt.play();
            roundStatsBtn.setText("Back");
        } else {
            TranslateTransition tt = new TranslateTransition();
            tt.setByY(485);
            tt.setDuration(Duration.millis(1000));
            tt.setNode(roundStatsPane);
            tt.setInterpolator(Interpolator.EASE_BOTH);
            tt.play();
            roundStatsBtn.setText("Round Stats");
        }
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
        _mostRecentRound = round;
    }

    public void statsChangeGraphBtnPressed() {

    }
}
