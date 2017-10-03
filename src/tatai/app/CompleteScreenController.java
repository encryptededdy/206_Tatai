package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
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
import tatai.app.util.TransitionFactory;
import tatai.app.util.queries.MostRecentRoundQuery;
import tatai.app.util.queries.PreviousRoundScoreQuery;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Controller class for the complete screen which comes after a round is completed.
 *
 * @author Zach Huxford
 */

public class CompleteScreenController {

    Round _mostRecentRound;
    @FXML private JFXButton menuBtn;
    @FXML private JFXButton roundStatsBtn;
    @FXML private JFXButton replayBtn;
    @FXML private JFXButton nextRoundBtn;
    @FXML private Label scoreMessageLabel;
    @FXML private Label scoreLabel;
    @FXML private Label yourScoreLabel;
    @FXML private Pane mainPane;
    @FXML private Pane scorePane, questionPaneclrShadow;
    @FXML private Pane controlsPane;
    @FXML private Pane roundStatsPane;
    @FXML private TableView resultsTable;
    @FXML private JFXButton statsChangeGraphBtn;
    @FXML private Label statLabelAverage;
    @FXML private Label statLabelAverageNo;
    @FXML private Label statLabelOverall;
    @FXML private Label statLabelOverallNo;
    @FXML private BarChart pastRoundScoresBarChart;
    @FXML private VBox graphVBox;
    @FXML private VBox roundStatsVBox;
    @FXML private ImageView backgroundImage;

    // Hacky variables for next round logic
    private String _nextGeneratorName;
    private boolean _nextRoundAvailable;

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
    }

    /**
     * fades in all of the javafx objects once the scene has been changed to the complete screen.
     */
    void fadeIn() {
        TransitionFactory.fadeIn(scoreMessageLabel).play();
        TransitionFactory.fadeIn(yourScoreLabel).play();
        TransitionFactory.fadeIn(scoreLabel).play();
        TransitionFactory.fadeIn(roundStatsBtn).play();
        TransitionFactory.fadeIn(replayBtn).play();

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
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
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
     * Determines the name of the round type just played and changes scene to a new instance of question screen
     * with a question generator of the same type (after the appropriate elements fade out)
     * @throws IOException
     */
    @FXML void replayBtnPressed() throws IOException {
        // Load the new scene
        Scene scene = replayBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();
        String currentGeneratorName = _mostRecentRound.getGeneratorName();
        loader.<QuestionController>getController().setQuestionSet(currentGeneratorName);
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
        FXMLLoader loader = new FXMLLoader(Main.questionLayout);
        Parent root = loader.load();


        loader.<QuestionController>getController().setQuestionSet(_nextGeneratorName);
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
        _nextGeneratorName = getNextRoundName(round);

        _nextRoundAvailable = _nextGeneratorName != null;

        if (!_nextRoundAvailable) {
            System.out.println("Disabling button");
            nextRoundBtn.setDisable(true);
        }
        yourScoreLabel.setText("Your Score: " + Integer.toString(_mostRecentRound.getScore()));
    }

    /**
     * constructs and executes a MostRecentRoundQuery which queries the database for information about the most recent round
     * and then updates the results table stats label etc with stats about the most recent round.
     */
    void executeRecentRoundQuery () {
        MostRecentRoundQuery mrrq = new MostRecentRoundQuery(scoreLabel, scoreMessageLabel, resultsTable, statLabelAverage, statLabelAverageNo, statLabelOverall, statLabelOverallNo, nextRoundBtn, _mostRecentRound.getRoundID());
        mrrq.execute();
    }

    /**
     * constructs and executes a PreviousRoundScoreQuery which queries the database for information about the scores for the
     * 10 most recent rounds and updates the pastRoundScoresBarChart with that data.
     */
    void executePreviousRoundScoreQuery() {
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

    /**
     * Determines the name of the next round if there is one based on the LinkedHashMap of questionGenerators in Main
     * and the Round object of the most recent round.
     * @param mostRecentRound
     * @return
     */
    private String getNextRoundName(Round mostRecentRound) {
        String currentGeneratorName = mostRecentRound.getGeneratorName();
        Iterator it = Main.questionGenerators.entrySet().iterator();
        boolean isCurrentGenerator = false;
        while (it.hasNext()) {
            Map.Entry<String, QuestionGenerator> entry = (Map.Entry)it.next();
            QuestionGenerator qg = entry.getValue();
            if (isCurrentGenerator) {
                System.out.println("Found next generator: "+qg.getGeneratorName());
                return qg.getGeneratorName();
            }
            if (qg.getGeneratorName().equals(currentGeneratorName)) {
                isCurrentGenerator = true;
            }
        }
        return null;
    }
}
