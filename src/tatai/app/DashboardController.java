package tatai.app;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

/**
 * Controls the dashboard statistics screen
 *
 * @author Edward
 */

public class DashboardController extends ToolbarController {

    @FXML private Tile accuracyTile, answerTime, roundScore, questionSetBar, roundLength, triesRadial, totalTime;
    @FXML private Pane advBtn, noDataErrorPane;
    @FXML private LineChart<String, Number> questionScoreChart;

    /**
     * Sets up the dashboard for fade in, and populates all dashboard fields
     */
    public void initialize() {
        super.initialize();
        populateTiles();
    }

    /**
     * Calls all methods to populate dashboard tiles
     */
    private void populateTiles() {
        if (hasCompletedRound()) {
            populateAccuracyTile();
            populateTTAnswerTile();
            populateRoundAccuracyGraph();
            populateTime();
            populateTriesBar();
            populateQuestionSetBar();
            populateRoundLength();
            populateRoundScoreGraph();
        } else {
            noDataErrorPane.setVisible(true);
        }
    }

    /**
     * Queries the sql database to see if the current user has completed a round
     */
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

    private void populateTime() {
        ResultSet avg = Main.database.returnOp("SELECT SUM(roundlength) FROM rounds WHERE username = '"+Main.currentUser+"'");
        try {
            avg.next();
            totalTime.setDuration(LocalTime.ofSecondOfDay(avg.getLong(1)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the graph of the last 20 accuracies along with the current score
     */
    private void populateRoundAccuracyGraph() {
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
     * Populates the graph of the last 20 scores along with the current score
     */
    private void populateRoundScoreGraph() {
        ResultSet data = Main.database.returnOp("SELECT score FROM (SELECT score, roundID FROM rounds WHERE username = '"+ Main.currentUser+"' AND score IS NOT NULL ORDER BY roundID DESC LIMIT 50) as output ORDER BY output.roundID ASC");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try {
            int i = 1;
            while (data.next()) {
                series.getData().add(new XYChart.Data<>(Integer.toString(i), data.getInt(1)));
                i++;
                System.out.println("Adding: "+data.getInt(1));
                //roundScore.addChartData(new ChartData(((data.getDouble(2)/data.getDouble(1))*100)));
            }
            questionScoreChart.getXAxis().setVisible(false);
            questionScoreChart.setLegendVisible(false);
            questionScoreChart.setCreateSymbols(false);
            questionScoreChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Populates the bar graph of the number of questions answered per question set.
     */
    private void populateQuestionSetBar() {
        int max = 100;
        for (String generator : Main.store.generators.getGeneratorsString()) {
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
     * Fade out and switch to the Advanced Statistics screen
     * @throws IOException Exception can be thrown when loading FXML
     */
    @FXML void advBtnPressed() throws IOException {
        Scene scene = advBtn.getScene();
        FXMLLoader loader = Layout.STATISTICS.loader();
        Parent root = loader.load();
        FadeTransition ft = TransitionFactory.fadeOut(dataPane);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<StatisticsController>getController().fadeIn();});
        ft.play();
    }
}
