package tatai.app.util.queries;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import tatai.app.Main;

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
    private int _score;
    /**
     * Constructs a MostRecentRoundQuery object with constraints
     * @param roundScore Which Label to write the score to
     * @param roundid What round to read
     */
    public MostRecentRoundQuery(Label roundScore, Label scoreMessageLabel, TableView tableViewRound, Integer roundid) {
        columnNames = new ArrayList<>(Arrays.asList("Question", "Answer", "Time (s)", "Correct", "Attempts"));
        _roundScoreLabel = roundScore;
        _scoreMessageLabel = scoreMessageLabel;
        tableView = tableViewRound;
        _score = 0;
        SQLQuery = "SELECT question, answer, timeToAnswer, correct, attempts FROM questions WHERE username = '"+Main.currentUser+"' AND roundid = " + roundid;
    }

    /**
     * Executes the Query in a background thread. setOnFinished listeners will be informed when this is complete.
     */
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                ObservableList<ObservableList> data = FXCollections.observableArrayList();
                try{
                    ResultSet rs = Main.database.returnOp(SQLQuery);
                    columnGenerator();

                    // Process data for each row
                    while(rs.next()){
                        ObservableList<String> row = FXCollections.observableArrayList();

                        columnProcess(row, rs);

                        //System.out.println("Row [1] added "+row );
                        data.add(row);
                    }
                    tableView.setItems(data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> completeQuery()); // Allow Query's listeners to be triggered once we're done
        task.run();
    }

    private void columnProcess(ObservableList<String> row, ResultSet rs) throws SQLException {
        // Process each column
        row.add(rs.getString(1)); // Question
        row.add(rs.getString(2)); // Answer
        row.add(rs.getString(3)); // AnswerTime
        row.add((rs.getInt(4) == 1) ? "Yes" : "No"); // Correct
        if (rs.getInt(4) == 1) {
            _score++;
        }
        row.add(rs.getString(5)); // Attempts
    }


}
