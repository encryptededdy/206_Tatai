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
    /**
     * Constructs a QuestionLogQuery object with constraints
     * @param timeBound The oldest time (UNIX Time) to read
     * @param limitSet Whether we limit questionSets
     * @param questionSet If limitSet is true, which QuestionSet
     * @param table Which TableView to write the output to
     * @param round What round to read (if null then all rounds are read)
     */
    public MostRecentRoundQuery(Label roundScore, Integer roundid) {

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
        row.add(dformat.format(Instant.ofEpochSecond(rs.getLong(1)))); // Date
        row.add(rs.getString(2)); // QuestionSet
        row.add(rs.getString(3)); // Question
        row.add(rs.getString(4)); // Answer
        row.add(rs.getString(5)); // AnswerTime
        row.add((rs.getInt(6) == 1) ? "Yes" : "No"); // Correct
        row.add(rs.getString(7)); // Attempts
    }


}
