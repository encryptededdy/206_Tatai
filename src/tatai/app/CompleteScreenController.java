package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import tatai.app.questions.Round;
import tatai.app.util.TransitionFactory;
import tatai.app.util.queries.MostRecentRoundQuery;
import tatai.app.util.queries.PreviousRoundScoreQuery;

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

    @FXML
    private Label statLabelAverage;

    @FXML
    private Label statLabelAverageNo;

    @FXML
    private Label statLabelOverall;

    @FXML
    private Label statLabelOverallNo;

    @FXML
    private BarChart pastRoundScoresBarChart;

    @FXML
    private VBox graphVBox;

    @FXML
    private VBox roundStatsVBox;

    @FXML
    private ImageView backgroundImage;


    public void initialize() {
        backgroundImage.setImage(Main.background);

        scoreMessageLabel.setOpacity(0);
        yourScoreLabel.setOpacity(0);
        scoreLabel.setOpacity(0);
        roundStatsBtn.setOpacity(0);
        replayBtn.setOpacity(0);
        nextRoundBtn.setOpacity(0);
        roundStatsPane.setLayoutY(500);
        graphVBox.setOpacity(0);
        graphVBox.setMouseTransparent(true);

        //testPopulateBarChart();
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
        roundStatsBtn.setDisable(true);
        if (roundStatsBtn.getText().equals("Round Stats")) {
            TranslateTransition tt = TransitionFactory.move(roundStatsPane, 0, -485, 500);
            tt.setInterpolator(Interpolator.EASE_OUT);
            tt.setOnFinished(event1 -> {
                roundStatsBtn.setDisable(false);
                roundStatsBtn.setText("Back");
            });
            tt.play();
            TransitionFactory.fadeIn(roundStatsPane, 500).play();
        } else {
            TranslateTransition tt = TransitionFactory.move(roundStatsPane, 0, 485, 500);
            tt.setInterpolator(Interpolator.EASE_IN);
            tt.setOnFinished(event1 -> {
                roundStatsBtn.setDisable(false);
                roundStatsBtn.setText("Round Stats");
            });
            tt.play();
            TransitionFactory.fadeOut(roundStatsPane, 500).play();
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

    public void executeRecentRoundQuery () {
        MostRecentRoundQuery mrrq = new MostRecentRoundQuery(scoreLabel, scoreMessageLabel, resultsTable, statLabelAverage, statLabelAverageNo, statLabelOverall, statLabelOverallNo, _mostRecentRound.getRoundID());
        mrrq.execute();
    }

    public void executePreviousRoundScoreQuery() {
        PreviousRoundScoreQuery prsq = new PreviousRoundScoreQuery(pastRoundScoresBarChart);
        prsq.execute();
    }

    public void statsChangeGraphBtnPressed() {
        if (roundStatsVBox.getOpacity() == 1) {
            FadeTransition ft0 = TransitionFactory.fadeOut(roundStatsVBox);
            FadeTransition ft1 = TransitionFactory.fadeOut(resultsTable);
            ft0.setOnFinished(actionEvent -> {
                graphVBox.setMouseTransparent(false);
                TransitionFactory.fadeIn(graphVBox).play();
            });
            ft1.play();
            ft0.play();
        } else {
            FadeTransition ft2 = TransitionFactory.fadeOut(graphVBox);
            ft2.setOnFinished(actionEvent -> {
                graphVBox.setMouseTransparent(true);
                TransitionFactory.fadeIn(roundStatsVBox).play();
                TransitionFactory.fadeIn(resultsTable).play();
            });
            ft2.play();
        }
    }
}
