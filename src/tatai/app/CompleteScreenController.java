package tatai.app;

import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tatai.app.questions.Round;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.DisplaysAchievements;
import tatai.app.util.Layout;
import tatai.app.util.achievements.AchievementView;
import tatai.app.util.factories.TransitionFactory;
import tatai.app.util.queries.MostRecentRoundQuery;
import tatai.app.util.queries.PreviousRoundScoreQuery;
import tatai.app.util.store.BalanceView;

import java.io.IOException;

/**
 * Controller class for the complete screen which comes after a round is completed.
 *
 * @author Zach Huxford
 * @author Edward
 */

public class CompleteScreenController implements DisplaysAchievements {

    private Round _mostRecentRound;
    private BalanceView balanceView;

    @FXML private JFXButton menuBtn;
    @FXML private JFXButton roundStatsBtn;
    @FXML private JFXButton replayBtn;
    @FXML private JFXButton nextRoundBtn;
    @FXML private Label scoreMessageLabel;
    @FXML private Label scoreLabel;
    @FXML private Label yourScoreLabel;
    @FXML private Pane mainPane;
    @FXML private Pane scorePane, questionPaneclrShadow, balancePane;
    @FXML private Pane controlsPane;
    @FXML private Pane roundStatsPane;
    @FXML private TableView<ObservableList> resultsTable;
    @FXML private JFXButton statsChangeGraphBtn;
    @FXML private Label statLabelAverage;
    @FXML private Label statLabelAverageNo;
    @FXML private Label statLabelOverall;
    @FXML private Label statLabelOverallNo;
    @FXML private BarChart pastRoundScoresBarChart;
    @FXML private VBox graphVBox;
    @FXML private VBox roundStatsVBox;
    @FXML private ImageView backgroundImage;
    @FXML private Pane challengeResultsPane, achievementPane;
    @FXML private Label resultText;
    @FXML private Label otherUserNameText;
    @FXML private Label yourScore;
    @FXML private Label theirScore;
    @FXML private JFXProgressBar countdownBar;
    @FXML private JFXButton closeChallenge;

    private Timeline resultsWaitBar;

    private QuestionGenerator nextGenerator;

    private TranslateTransition achievementTransition;

    /**
     * Setup javafx objects to be animated in.
     * Also decides if the next round button will be disabled.
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);

        scoreMessageLabel.setOpacity(0);
        yourScoreLabel.setOpacity(0);
        scoreLabel.setOpacity(0);
        roundStatsBtn.setOpacity(0);
        replayBtn.setOpacity(0);
        //nextRoundBtn.setOpacity(0);
        roundStatsPane.setLayoutY(500);
        graphVBox.setOpacity(0);
        graphVBox.setMouseTransparent(true);

        // Client wait bar
        resultsWaitBar = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(countdownBar.progressProperty(), 1)),
                new KeyFrame(Duration.seconds(60), new KeyValue(countdownBar.progressProperty(), 0))
        );

        // Achievement transition
        achievementTransition = TransitionFactory.move(achievementPane, 0, -60);
        achievementTransition.setFromY(0);
        achievementTransition.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft2 = TransitionFactory.fadeOut(achievementPane);
        PauseTransition pt2 = new PauseTransition(Duration.seconds(1.5));
        pt2.setOnFinished(event -> ft2.play());
        achievementTransition.setOnFinished(event -> pt2.play());

        // Load the balance pane
        balanceView = new BalanceView();
        balancePane.getChildren().add(balanceView.getPane());
    }

    void netMode(int id) {
        replayBtn.setDisable(true);
        nextRoundBtn.setDisable(true);
        int score = _mostRecentRound.getScore();
        resultsWaitBar.play();
        yourScore.setText(Integer.toString(score));
        challengeResultsPane.setVisible(true);

        Task<JsonObject> resultsWait = new Task<JsonObject>() {
            @Override
            protected JsonObject call() {
                try {
                    return Main.netConnection.finishRound(id, score);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        resultsWait.setOnSucceeded(event -> {
            JsonObject result = resultsWait.getValue();
            if (result == null) {
                resultText.setText("Connection Error");
                resultsWaitBar.stop();
            } else {
                // Parse the JSON
                if (result.get("finished").getAsBoolean()) {
                    // We're finished
                    countdownBar.setVisible(false);
                    otherUserNameText.setText(result.get("otherUser").getAsString()+"'s Score");
                    int otherScore = result.get("otherScore").getAsInt();
                    theirScore.setText(Integer.toString(otherScore));

                    if (score < otherScore) {
                        resultText.setText("You Lost!");
                        resultText.setStyle("-fx-background-color: #F44336");
                    } else if (score > otherScore) {
                        resultText.setText("You Won!");
                        resultText.setStyle("-fx-background-color: #4CAF50");
                    } else {
                        resultText.setText("Draw!");
                    }

                } else {
                    // Error :(
                    resultText.setText("Connection Error");
                    resultsWaitBar.stop();
                }
            }
        });

        new Thread(resultsWait).start();
    }

    /**
     * fades in all of the javafx objects once the scene has been changed to the complete screen.
     */
    void fadeIn() {
        TransitionFactory.fadeIn(scoreMessageLabel).play();
        TransitionFactory.fadeIn(yourScoreLabel).play();
        TransitionFactory.fadeIn(scoreLabel).play();
        TransitionFactory.fadeIn(roundStatsBtn).play();
        FadeTransition ft = TransitionFactory.fadeIn(replayBtn);
        ft.setOnFinished(actionEvent -> {
            if (!Main.store.achievements.getAchievements().get("Off To A Good Start").isCompleted()) {
                Main.store.achievements.getAchievements().get("Off To A Good Start").setCompleted(this, achievementPane);
            }
            if (!Main.store.achievements.getAchievements().get("Look At You Go!").isCompleted() && _mostRecentRound.getStreak() >= 10) {
                Main.store.achievements.getAchievements().get("Look At You Go!").setCompleted(this, achievementPane);
            }
            balanceView.updateBalance();
        });

        ft.play();
        // JavaFX Bug means this breaks the disable property.
        //TransitionFactory.fadeIn(nextRoundBtn).play();
    }

    /**
     * Animates out the pane to switch to the main menu
     * @throws IOException
     */
    @FXML void menuBtnPressed() throws IOException {
        // If the stats screen is up, put it away.
        if (!roundStatsBtn.getText().equals("Round Stats")) roundStatsBtnPressed();
        // Load the new scene
        Scene scene = menuBtn.getScene();
        FXMLLoader loader = Layout.MAINMENU.loader();
        Parent root = loader.load();
        loader.<MainMenuController>getController().setupFade(false);

        // bring the cover back
        questionPaneclrShadow.setVisible(true);
        // Fade in the cover
        FadeTransition ft = TransitionFactory.fadeIn(questionPaneclrShadow, (int)(Main.transitionDuration*0.5));
        // Expand anim
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), questionPaneclrShadow);
        st.setToY(1.83823529);
        st.setToX(0.99);
        // Move anim
        TranslateTransition tt2 = TransitionFactory.move(controlsPane, 0, 71);
        TranslateTransition tt = TransitionFactory.move(questionPaneclrShadow, 0, (int)(73*0.544));
        ParallelTransition pt = new ParallelTransition(st, tt, tt2);
        ft.setOnFinished(event1 -> {scorePane.setVisible(false); pt.play();}); // play the expand anim when fade finished
        pt.setOnFinished(event -> {scene.setRoot(root); loader.<MainMenuController>getController().fadeIn();});
        ft.play();
    }

    /**
     * Animates in and out the roundStats pane which shows a graph containing the scores of the most recent rounds
     * and a table showing information pertaining to the most recent round in particular.
     */
    @FXML void roundStatsBtnPressed() {
        roundStatsBtn.setDisable(true);
        if (roundStatsBtn.getText().equals("Round Stats")) { // if the stats screen isn't up
            TranslateTransition tt = TransitionFactory.move(roundStatsPane, 0, -485, 500);
            tt.setInterpolator(Interpolator.EASE_OUT);
            tt.setOnFinished(event1 -> {
                roundStatsBtn.setDisable(false);
                roundStatsBtn.setText("Back");
            });
            tt.play();
            TransitionFactory.fadeIn(roundStatsPane).play();
        } else { // stats screen is up, going back.
            TranslateTransition tt = TransitionFactory.move(roundStatsPane, 0, 485, 500);
            tt.setInterpolator(Interpolator.EASE_IN);
            tt.setOnFinished(event1 -> {
                roundStatsBtn.setDisable(false);
                roundStatsBtn.setText("Round Stats");
            });
            tt.play();
            TransitionFactory.fadeOut(roundStatsPane).play();
        }
    }

    /**
     * Hide the challenge results pane
     */
    @FXML void closeChallengePressed() {
        challengeResultsPane.setVisible(false);
    }

    /**
     * Determines the name of the round type just played and changes scene to a new instance of question screen
     * with a question generator of the same type (after the appropriate elements fade out)
     * @throws IOException
     */
    @FXML void replayBtnPressed() throws IOException {
        // Load the new scene
        Scene scene = replayBtn.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(_mostRecentRound.getGenerator());
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainPane);
        ft.setOnFinished(event1 -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();}); // switch scenes when fade complete
        ft.play();
    }

    /**
     * Determines the name of the round type just played and changes scene to a new instance of question screen
     * with a question generator of the next type in the sequence determined by setMostRecentRound
     * (after the appropriate elements fade out)
     * @throws IOException
     */
    @FXML void nextRoundBtnPressed() throws IOException{
        // Load the new scene
        Scene scene = replayBtn.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();


        loader.<QuestionController>getController().setQuestionSet(nextGenerator);
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(mainPane);
        ft.setOnFinished(event1 -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();}); // switch scenes when fade complete
        ft.play();
    }

    /**
     * gives a reference to the most recent Round object from the question controller
     * also determines the name of the type of the next round if there is one.
     * @param round the Round object from the most recent Round
     *
     */
    void setMostRecentRound(Round round) {
        _mostRecentRound = round;

        if ((Main.store.generators.getNextGenerator(round.getGenerator()) == null) || round.getGenerator().isCustom()) {
            nextRoundBtn.setDisable(true);
        } else {
            nextGenerator = Main.store.generators.getNextGenerator(round.getGenerator());
        }
        yourScoreLabel.setText("Your Score: " + Integer.toString(_mostRecentRound.getScore()));
        if (_mostRecentRound.getScore() < 200) yourScoreLabel.setVisible(false);
        executeRecentRoundQuery();
        executePreviousRoundScoreQuery();
    }

    /**
     * constructs and executes a MostRecentRoundQuery which queries the database for information about the most recent round
     * and then updates the results table stats label etc with stats about the most recent round.
     */
    private void executeRecentRoundQuery () {
        MostRecentRoundQuery mrrq = new MostRecentRoundQuery(scoreLabel, scoreMessageLabel, resultsTable, statLabelAverage, statLabelAverageNo, statLabelOverall, statLabelOverallNo, nextRoundBtn, _mostRecentRound.getRoundID());
        mrrq.execute();
    }

    /**
     * constructs and executes a PreviousRoundScoreQuery which queries the database for information about the scores for the
     * 10 most recent rounds and updates the pastRoundScoresBarChart with that data.
     */
    private void executePreviousRoundScoreQuery() {
        PreviousRoundScoreQuery prsq = new PreviousRoundScoreQuery(pastRoundScoresBarChart);
        prsq.execute();
    }

    /**
     * toggles the visibility of the graphVBox, the roundStatsVbox and the resultsTable
     */
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

    public void animateAchievement(AchievementView achievement) {
        achievementPane.setOpacity(1);
        achievementPane.getChildren().clear();
        achievementPane.getChildren().setAll(achievement.getNode());
        achievementPane.setVisible(true);
        achievementTransition.play();
    }
}
