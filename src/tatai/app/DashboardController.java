package tatai.app;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.TransitionFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Map;

public class DashboardController {

    @FXML
    private Tile accuracyTile, answerTime, roundScore, questionSetBar, roundLength, triesRadial;

    @FXML
    private Pane dataPane, backBtn, advBtn;

    public void initialize() {
        dataPane.setOpacity(0);
        populateAccuracyTile();
        populateTTAnswerTile();
        populateRoundScoreGraph();
        //populateTime();
        populateTriesBar();
        populateQuestionSetBar();
        populateRoundLength();
    }

    public void fadeIn() {
        TransitionFactory.fadeIn(dataPane).play();
    }

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

    private void populateQuestionSetBar() {
        for (Map.Entry<String, QuestionGenerator> entry : Main.questionGenerators.entrySet()) {
            String generator = entry.getKey();
            ResultSet total = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"' AND questionSet = '"+generator+"'");
            try {
                total.next();
                questionSetBar.addBarChartItem(new BarChartItem(generator, total.getInt(1)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

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

    private void populateRoundLength() {
        ResultSet data = Main.database.returnOp("SELECT roundlength FROM (SELECT roundlength, roundID FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 20) as output ORDER BY output.roundID ASC");
        ResultSet latest = Main.database.returnOp("SELECT roundlength FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 1");
        try {
            latest.next();
            roundLength.setValue(latest.getDouble(1));
            while (data.next()) {
                roundLength.addChartData(new ChartData(data.getDouble(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backBtnPressed() throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        scene.setRoot(root);
    }

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
