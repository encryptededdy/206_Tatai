package tatai.app.util.queries;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import tatai.app.Main;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * A Query that gets a question log of all question attempts since a specified date and outputs to a TableView
 *
 * @author Edward
 */
public class NumberQuery extends Query {
    private ObservableList<ObservableList> data;
    /**
     * Constructs a QuestionLogQuery object with constraints
     * @param timeBound The oldest time (UNIX Time) to read
     * @param limitSet Whether we limit questionSets
     * @param questionSet If limitSet is true, which QuestionSet
     * @param table Which TableView to write the output to
     * @param round What round to read (if null then all rounds are read)
     */
    public NumberQuery(long timeBound, boolean limitSet, String questionSet, TableView<ObservableList> table, Integer round) {
        // Names of the output columns
        columnNames = new ArrayList<>(Arrays.asList("Answer", "Times Answered", "Average Time (s)", "% Correct", "Avg Attempts"));
        tableView = table;
        SQLQuery = "SELECT answer, timeToAnswer, correct, attempts FROM questions WHERE username = '"+Main.currentUser+"' AND date > " + timeBound;
        if (limitSet) {
            SQLQuery = SQLQuery + " AND questionSet = '" + questionSet + "'";
        }
        if (round != null) {
            SQLQuery = SQLQuery + " AND roundID = " + round;
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
                    LinkedHashMap<String, ObservableList<String>> numbers = new LinkedHashMap<>(); // Stores the statistics for each number queried
                    // Process data for each row
                    while(rs.next()){

                        // Add data to the numbers map to store data for each possible answer
                        String answer = rs.getString(1);
                        if (numbers.containsKey(answer)) {
                            // Answer has already been added, update counts
                            ObservableList<String> row = numbers.get(answer);

                            // when you have an integer stored as a string but you need to increment it you get this mess
                            row.set(1, Integer.toString(Integer.parseInt(row.get(1))+1)); // increment the number of answers
                            row.set(2, Integer.toString(Integer.parseInt(row.get(2))+rs.getInt(2))); // add answertime
                            row.set(3, Integer.toString(Integer.parseInt(row.get(3))+rs.getInt(3))); // add correct count
                            row.set(4, Integer.toString(Integer.parseInt(row.get(4))+rs.getInt(4))); // add attempt count
                        } else {
                            ObservableList<String> row = FXCollections.observableArrayList();
                            row.add(rs.getString(1)); // Answer
                            row.add("1"); // Times Answered
                            row.add(rs.getString(2)); // AnswerTime
                            row.add((rs.getInt(3) == 1) ? "1" : "0"); // Correct
                            row.add(rs.getString(4)); // Attempts
                            numbers.put(answer, row);
                        }

                        //System.out.println("Row [1] added "+row );
                    }

                    // Set the rounding mode for the calculations below
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.HALF_UP);

                    // Now that we have populated the hashmap, we need to actually calculate the statistics, then store this into our ObservableList (data)
                    numbers.forEach((number, list) -> {
                        double answers = Double.parseDouble(list.get(1));

                        list.set(2, df.format(Integer.parseInt(list.get(2))/answers)); // calculate average answer time
                        list.set(3, df.format((Integer.parseInt(list.get(3))/answers)*100)); // calculate correct percentage
                        list.set(4, df.format(Integer.parseInt(list.get(4))/answers)); // calculate average attempts

                        data.add(list);
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            columnGenerator();
            tableView.setItems(data);
            completeQuery();

        }); // Allow Query's listeners to be triggered once we're done
        new Thread(task).start();
    }
}
