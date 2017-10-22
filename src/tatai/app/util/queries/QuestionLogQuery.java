package tatai.app.util.queries;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
public class QuestionLogQuery extends Query {
    private ObservableList<ObservableList> data;
    /**
     * Constructs a QuestionLogQuery object with constraints
     * @param timeBound The oldest time (UNIX Time) to read
     * @param limitSet Whether we limit questionSets
     * @param questionSet If limitSet is true, which QuestionSet
     * @param table Which TableView to write the output to
     * @param round What round to read (if null then all rounds are read)
     */
    public QuestionLogQuery(long timeBound, boolean limitSet, String questionSet, TableView<ObservableList> table, Integer round, Integer session) {
        // Names of the output columns
        columnNames = new ArrayList<>(Arrays.asList("Date", "Set", "Question", "Answer", "Time (s)", "Correct", "Tries"));
        tableView = table;
        SQLQuery = "SELECT date, questionSet, question, answer, timeToAnswer, correct, attempts FROM questions WHERE username = '"+Main.currentUser+"' AND date > " + timeBound;
        if (limitSet) {
            SQLQuery = SQLQuery + " AND questionSet = '" + questionSet + "'";
        }
        if (round != null) {
            SQLQuery = SQLQuery + " AND roundID = " + round;
        }
        if (session != null) {
            SQLQuery = SQLQuery + " AND sessionID = " + session;
        }
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
                    while(rs.next()){
                        ObservableList<String> row = FXCollections.observableArrayList();

                        columnProcess(row, rs);

                        //System.out.println("Row [1] added "+row );
                        data.add(row);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            columnGenerator();
            if (tableView != null) {
                tableView.setItems(data);
            }
            completeQuery();
        }); // Allow Query's listeners to be triggered once we're done
        new Thread(task).start();
    }

    /**
     * Processes a column of data
     * @param row The row to add the output to
     * @param rs The resultset to get the data from
     * @throws SQLException Thrown when a bad request is made to the ResultSet
     */
    private void columnProcess(ObservableList<String> row, ResultSet rs) throws SQLException {
        // Process each column
        row.add(dformat.format(Instant.ofEpochSecond(rs.getLong(1)))); // Date
        row.add(rs.getString(2)); // QuestionSet
        row.add(rs.getString(3)); // Question
        row.add(Translator.toDisplayable(rs.getString(4))); // Answer
        row.add(rs.getString(5)); // AnswerTime
        row.add((rs.getInt(6) == 1) ? "Yes" : "No"); // Correct
        row.add(rs.getString(7)); // Attempts
    }


}
