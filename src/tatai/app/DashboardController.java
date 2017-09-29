package tatai.app;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.TransitionFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Map;

/**
 * Controls the dashboard statistics screen
 */

public class DashboardController {

    @FXML
    private Tile accuracyTile, answerTime, roundScore, questionSetBar, roundLength, triesRadial;

    @FXML
    private Pane dataPane, backBtn, advBtn, backgroundPane, noDataErrorPane;

    @FXML
    private ImageView backgroundImage;

    /**
     * Sets up the dashboard for fade in, and populates all dashboard fields
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);
        dataPane.setOpacity(0);
        populateTiles();
    }

    /**
     * Fades in the screen (to animate in)
     */
    void fadeIn() {
        TransitionFactory.fadeIn(dataPane).play();
    }

    /**
     * Calls all methods to populate dashboard tiles
     */
    private void populateTiles() {
        if (hasCompletedRound()) {
            populateAccuracyTile();
            populateTTAnswerTile();
            populateRoundScoreGraph();
            //populateTime();
            populateTriesBar();
            populateQuestionSetBar();
            populateRoundLength();
        } else {
            noDataErrorPane.setVisible(true);
        }
    }

    private boolean hasCompletedRound() {
        boolean complete = false;
        ResultSet rs = Main.database.returnOp("SELECT COUNT(*) FROM rounds WHERE username = '"+Main.currentUser+"' AND isComplete = 1");
        try {
            rs.next();
            complete = (rs.getInt(1) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complete;
    }

    /**
     * Populates the Questions correct/total accuracy tile from the database
     */
    private void populateAccuracyTile() {
        ResultSet total = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"'");
        ResultSet correct = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"' AND correct = 1");
        try {
            total.next();
            correct.next();
            accuracyTile.setMaxValue(total.getInt(1));
            accuracyTile.setValue(correct.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the avg time to answer tile
     */
    private void populateTTAnswerTile() {
        ResultSet length = Main.database.returnOp("SELECT AVG(timeToAnswer) FROM questions WHERE username = '"+Main.currentUser+"'");
        try {
            length.next();
            answerTime.setValue(length.getDouble(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    private void populateTime() {
        ResultSet avg = Main.database.returnOp("SELECT SUM(roundlength) FROM rounds WHERE username = '"+Main.currentUser+"'");
        try {
            avg.next();
            timeTile.setDuration(LocalTime.ofSecondOfDay(avg.getLong(1)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */

    /**
     * Populates the graph of the last 20 scores along with the current score
     */
    private void populateRoundScoreGraph() {
        ResultSet data = Main.database.returnOp("SELECT noquestions, nocorrect FROM (SELECT noquestions, nocorrect, roundID FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 20) as output ORDER BY output.roundID ASC");
        ResultSet latest = Main.database.returnOp("SELECT noquestions, nocorrect FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 1");
        try {
            latest.next();
            roundScore.setValue((latest.getDouble(2)/latest.getDouble(1))*100);
            while (data.next()) {
                roundScore.addChartData(new ChartData(((data.getDouble(2)/data.getDouble(1))*100)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the bar graph of the number of questions answered per question set.
     */
    private void populateQuestionSetBar() {
        int max = 100;
        for (Map.Entry<String, QuestionGenerator> entry : Main.questionGenerators.entrySet()) {
            String generator = entry.getKey();
            ResultSet total = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"' AND questionSet = '"+generator+"'");
            try {
                total.next();
                questionSetBar.addBarChartItem(new BarChartItem(generator, total.getInt(1)));
                // track the max value
                if (total.getInt(1) > max) {
                    max = total.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // set the max value
        questionSetBar.setMaxValue(max);
    }

    /**
     * Populates the donut graph of tries to answer a questions
     */
    private void populateTriesBar() {
        ResultSet firstTry = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"' AND attempts = 1");
        ResultSet secondTry = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"' AND attempts = 2");
        ResultSet thirdTry = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"' AND attempts = 3");
        try {
            firstTry.next();
            secondTry.next();
            thirdTry.next();
            triesRadial.addChartData(new ChartData("1st Try", firstTry.getInt(1), Tile.GREEN));
            triesRadial.addChartData(new ChartData("2nd Try", secondTry.getInt(1)));
            triesRadial.addChartData(new ChartData("Incorrect", thirdTry.getInt(1), Tile.RED));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the graph of round lengths over the past 20 rounds
     */
    private void populateRoundLength() {
        ResultSet data = Main.database.returnOp("SELECT roundlength FROM (SELECT roundlength, roundID FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 20) as output ORDER BY output.roundID ASC");
        ResultSet latest = Main.database.returnOp("SELECT roundlength FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 1");
        try {
            if (!latest.next()) {
                roundLength.setValue(0);
            } else {
                roundLength.setValue(latest.getDouble(1));
                while (data.next()) {
                    if (data.getDouble(1) > 400) { // Removes outliers, as they cause errors with the graph smoothing
                        roundLength.addChartData(new ChartData(400));
                    } else {
                        roundLength.addChartData(new ChartData(data.getDouble(1)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Animate out the screen then switch to the main menu
     * @throws IOException Exception can be thrown when loading FXML
     */
    @FXML
    void backBtnPressed() throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        loader.<MainMenuController>getController().setupFade(false);
        // Fade out items
        FadeTransition ft = TransitionFactory.fadeOut(dataPane, Main.transitionDuration/2);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), backgroundPane);
        st.setToX(0.5);
        st.setOnFinished(event -> {scene.setRoot(root); loader.<MainMenuController>getController().fadeIn();});
        ft.setOnFinished(event -> st.play());
        // animate
        ft.play();
    }

    /**
     * Fade out and switch to the Advanced Statistics screen
     * @throws IOException Exception can be thrown when loading FXML
     */
    @FXML
    void advBtnPressed() throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.statisticsLayout);
        Parent root = loader.load();
        FadeTransition ft = TransitionFactory.fadeOut(dataPane);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<StatisticsController>getController().fadeIn();});
        ft.play();
    }
}
