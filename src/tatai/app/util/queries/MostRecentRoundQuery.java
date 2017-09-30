package tatai.app.util.queries;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import tatai.app.Main;
import tatai.app.util.Translator;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A query which obtains statistics of interest pertaining to the most recent round played by the current user and updates
 * the table object and statistics Label objects appropriately
 *
 * @author Zach
 */
public class MostRecentRoundQuery extends Query {
    private String SQLQuery;
    private Label _roundScoreLabel;
    private Label _scoreMessageLabel;
    private Label _statLabelAverage;
    private Label _statLabelAverageNo;
    private Label _statLabelOverall;
    private Label _statLabelOverallNo;
    private JFXButton _nextRoundButton;
    private int _score;
    private int _numberOfQuestions;
    private int _totalTimeToAnswer;
    private int _roundTimeLength;
    private int _numberOfAttempts;

    private int _longestStreak;
    private int _currentStreak;

    private int _shortestAnswerTime;

    private ObservableList<ObservableList> data;
    /**
     * Constructs a MostRecentRoundQuery object with a reference to the most recent roundID and many javafx Labels and one
     * table, all of which will be updated after the query has returned with the appropriate information to fill them
     *
     * Initializes many of the fields
     *
     * constructs the SQL query
     */
    public MostRecentRoundQuery(
            Label roundScore, Label scoreMessageLabel, TableView tableViewRound, Label statLabelAverage, Label statLabelAverageNo, Label statLabelOverall, Label statLabelOverallNo, JFXButton nextRoundButton, Integer roundid) {
        columnNames = new ArrayList<>(Arrays.asList("Quest.", "Ans.", "Time", "Correct", "Tries"));
        _roundScoreLabel = roundScore;
        _scoreMessageLabel = scoreMessageLabel;
        _statLabelAverage = statLabelAverage;
        _statLabelAverageNo = statLabelAverageNo;
        _statLabelOverall = statLabelOverall;
        _statLabelOverallNo = statLabelOverallNo;
        _nextRoundButton = nextRoundButton;
        tableView = tableViewRound;

        _score = 0;
        _numberOfQuestions = 0;
        _totalTimeToAnswer = 0;
        _roundTimeLength = 0;
        _numberOfAttempts = 0;

        _longestStreak = 0;
        _currentStreak =0;
        SQLQuery = "SELECT question, answer, timeToAnswer, correct, attempts FROM questions WHERE username = '"+Main.currentUser+"' AND roundid = " + roundid;
    }

    /**
     * Executes the Query in a background thread. setOnFinished listeners will be informed when this is complete and all
     * of the relevant tables and labels will be updated with the statistics
     */
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                data = FXCollections.observableArrayList();
                try{
                    ResultSet rs = Main.database.returnOp(SQLQuery);

                    // Process data for each row
                    while(rs.next()) {
                        ObservableList<String> row = FXCollections.observableArrayList();
                        columnProcess(row, rs);
                        data.add(row);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                System.out.println(_score);

                return null;
            }
        };
        task.setOnSucceeded(event -> {
            //Displays a "better luck next time" message if the score is below 8/10 or a "good job!" if 8/10 or greater
            // also disables the nextRoundButton if the score is below 8/10
            if (_score < 8) {
                _scoreMessageLabel.setText("Ma te wa, karawhiua");
                _nextRoundButton.setDisable(true);
            } else {
                _scoreMessageLabel.setText("Ka Pai!");
            }

            _roundScoreLabel.setText(_score + "/10");
            columnGenerator();
            tableView.setItems(data);
            randomlyUpdateMiscStats();
            completeQuery();
        }); // Allow Query's listeners to be triggered once we're done
        new Thread(task).start();
    }

    /**
     * Constructs a table row from one row of the ResultSet
     * also increments the appropriate counters other misc statistics can be generated.
     * @param row
     * @param rs
     * @throws SQLException
     */
    private void columnProcess(ObservableList<String> row, ResultSet rs) throws SQLException {
        // Process each column
        row.add(rs.getString(1)); // Question
        row.add(Translator.toDisplayable(rs.getString(2))); // Answer

        _totalTimeToAnswer += rs.getInt(3);
        row.add(rs.getString(3)); // AnswerTime
        if (_shortestAnswerTime == 0) {
            _shortestAnswerTime = rs.getInt(3);
        } else if (_shortestAnswerTime > rs.getInt(3)) {
            _shortestAnswerTime = rs.getInt(3);
        }

        row.add((rs.getBoolean(4)) ? "✓" : "✗"); // Correct
        if (rs.getBoolean(4)) {
            _score++;
            _currentStreak++;
        } else {
            _currentStreak = 0;
        }

        if (_currentStreak > _longestStreak) {
            _longestStreak = _currentStreak;
        }

        _numberOfQuestions++;

        _numberOfAttempts += rs.getInt(5);
        row.add(rs.getString(5)); // Attempts
    }

    // These Misc Stats methods should probably be it's own interface or class

    /**
     * updates the statLabelAverage with the average time to answer statistic for the most recent round
     */
    private void setAverageTimeToAnswer() {
        int averageTimeToAnswer = _totalTimeToAnswer / _numberOfQuestions;
        _statLabelAverage.setText("Average Time To Answer");
        _statLabelAverageNo.setText(Integer.toString(averageTimeToAnswer));
    }

    /**
     * updates the statLabelAverage with the average number of attempts statistic for the most recent round
     */
    private void setAverageNumberOfAttempts() {
        int averageNumberOfAttempts = _numberOfAttempts / _numberOfQuestions;
        _statLabelAverage.setText("Average Number of Attempts");
        _statLabelAverageNo.setText(Integer.toString(averageNumberOfAttempts));
    }

    /**
     * updates the statLabelOverall with the overall round time statistic for the most recent round
     */
    private void setOverallRoundTime() {
        _statLabelOverall.setText("Overall Round Time");
        _statLabelOverallNo.setText(Integer.toString(_totalTimeToAnswer));
    }

    /**
     * updates the statLabelOverall with the longest streak this round statistic for the most recent round
     */
    private void setOverallLongestStreak() {
        _statLabelOverall.setText("Your Longest Streak This Round");
        _statLabelOverallNo.setText(Integer.toString(_longestStreak));
    }

    /**
     * updates the statLabelOverall with the quickest time to answer statistic for the most recent round
     */
    private void setOverallQuickestAnswer() {
        _statLabelOverall.setText("Your Quickest Answer This Round");
        _statLabelOverallNo.setText(Integer.toString(_shortestAnswerTime));
    }

    /**
     * Randomly chooses what miscellaneous stats the labels will be updated with and then runs the chosen methods from above
     * to update the labels
     */
    private void randomlyUpdateMiscStats() {
        int averageRand = Math.toIntExact(Math.round(Math.random()));
        if (averageRand == 0) {
            setAverageTimeToAnswer();
        } else {
            setAverageNumberOfAttempts();
        }

        int overallRand = Math.toIntExact(Math.round(2.0 * Math.random()));
        if (overallRand == 0) {
            setOverallRoundTime();
        } else if (overallRand == 1) {
            setOverallLongestStreak();
        } else {
            setOverallQuickestAnswer();
        }

    }

}
