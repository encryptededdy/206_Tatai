package tatai.app.util.queries;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import tatai.app.Main;
import tatai.app.util.Translator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Query that gets a question log of all question attempts since a specified date and outputs to a TableView
 *
 * @author Edward
 */
public class MostRecentRoundQuery extends Query {
    private String SQLQuery;
    private Label _roundScoreLabel;
    private Label _scoreMessageLabel;
    private Label _statLabelAverage;
    private Label _statLabelAverageNo;
    private Label _statLabelOverall;
    private Label _statLabelOverallNo;
    private int _score;
    private int _numberOfQuestions;
    private int _totalTimeToAnswer;
    private int _roundTimeLength;
    private int _numberOfAttempts;

    private int _longestStreak;
    private int _currentStreak;
    private boolean _previousAnsCorrect;

    private ObservableList<ObservableList> data;
    /**
     * Constructs a MostRecentRoundQuery object with constraints
     * @param roundScore Which Label to write the score to
     * @param roundid What round to read
     */
    public MostRecentRoundQuery(
            Label roundScore, Label scoreMessageLabel, TableView tableViewRound, Label statLabelAverage, Label statLabelAverageNo, Label statLabelOverall, Label statLabelOverallNo, Integer roundid) {
        columnNames = new ArrayList<>(Arrays.asList("Quest.", "Ans.", "Time", "Correct", "Tries"));
        _roundScoreLabel = roundScore;
        _scoreMessageLabel = scoreMessageLabel;
        _statLabelAverage = statLabelAverage;
        _statLabelAverageNo = statLabelAverageNo;
        _statLabelOverall = statLabelOverall;
        _statLabelOverallNo = statLabelOverallNo;
        tableView = tableViewRound;

        _score = 0;
        _numberOfQuestions = 0;
        _totalTimeToAnswer = 0;
        _roundTimeLength = 0;
        _numberOfAttempts = 0;

        _longestStreak = 0;
        _currentStreak =0;
        _previousAnsCorrect = false;
        SQLQuery = "SELECT question, answer, timeToAnswer, correct, attempts FROM questions WHERE username = '"+Main.currentUser+"' AND roundid = " + roundid;
    }

    /**
     * Executes the Query in a background thread. setOnFinished listeners will be informed when this is complete.
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

                        //System.out.println("Row [1] added "+row );
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
            if (_score < 8) {
                _scoreMessageLabel.setText("Ma te wa, karawhiua");
            } else {
                _scoreMessageLabel.setText("Ka Pai!");
            }

            _roundScoreLabel.setText(_score + "/10");
            columnGenerator();
            tableView.setItems(data);
            completeQuery();
        }); // Allow Query's listeners to be triggered once we're done
        new Thread(task).start();
    }

    private void columnProcess(ObservableList<String> row, ResultSet rs) throws SQLException {
        // Process each column
        row.add(rs.getString(1)); // Question
        row.add(Translator.toDisplayable(rs.getString(2))); // Answer

        _totalTimeToAnswer += rs.getInt(3);
        row.add(rs.getString(3)); // AnswerTime

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


    private void setAverageTimeToAnswer() {
        int averageTimeToAnswer = _totalTimeToAnswer / _numberOfQuestions;
        _statLabelAverage.setText("Average Time To Answer");
        _statLabelAverageNo.setText(Integer.toString(averageTimeToAnswer));
    }

    private void setAverageNumberOfAttempts() {
        int averageNumberOfAttempts = _numberOfAttempts / _numberOfQuestions;
        _statLabelAverage.setText("Average Number of Attempts");
        _statLabelAverageNo.setText(Integer.toString(averageNumberOfAttempts));
    }

    private void setOverallRoundTime() {
        _statLabelOverall.setText("Overall Round Time");
        _statLabelOverallNo.setText(Integer.toString(_totalTimeToAnswer));
    }

    private void setOverallLongestStreak() {
        _statLabelOverall.setText("Your Longest Streak This Round");
        _statLabelOverallNo.setText(Integer.toString(_longestStreak));
    }

    private void setOverallQuickestAnswer() {
        _statLabelOverall.setText("Your Quickest Answer This Round");
        _statLabelOverallNo.setText(Integer.toString(_longestStreak));
    }

}
