package tatai.app.util.queries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import tatai.app.Main;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class QuestionLogQuery {
    private String SQLQuery;
    private TableView tableView;

    // Names of the output columns
    ArrayList<String> columnNames = new ArrayList<>(Arrays.asList("Date", "Question Set", "Question", "Answer", "Time (s)", "Correct", "Attempts"));

    // Date and time format for output
    DateTimeFormatter dformat =
            DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                    .withLocale( Locale.ENGLISH )
                    .withZone( ZoneId.systemDefault() );

    public QuestionLogQuery(long timeBound, boolean limitSet, String questionSet, TableView table) {
        tableView = table;
        if (!limitSet) {
            SQLQuery = "SELECT date, questionSet, question, answer, timeToAnswer, correct, attempts FROM questions WHERE date > " + timeBound;
        } else {
            SQLQuery = "SELECT date, questionSet, question, answer, timeToAnswer, correct, attempts FROM questions WHERE date > " + timeBound + " AND questionSet = '" + questionSet + "'";
        }
    }

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

                        // Process each column
                        row.add(dformat.format(Instant.ofEpochSecond(rs.getLong(1)))); // Date
                        row.add(rs.getString(2)); // QuestionSet
                        row.add(rs.getString(3)); // Question
                        row.add(rs.getString(4)); // Answer
                        row.add(rs.getString(5)); // AnswerTime
                        row.add((rs.getInt(6) == 1) ? "Yes" : "No"); // Correct
                        row.add(rs.getString(7)); // Attempts

                        System.out.println("Row [1] added "+row );
                        data.add(row);
                    }
                    tableView.setItems(data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.run();
    }

    private void columnGenerator() {

            /*
             * Based on code by Source: http://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
             *                  Author: Narayan G. Maharjan
             */

        // Populate output columns
        for(String colName : columnNames){
            final int i = columnNames.indexOf(colName);
            //We are using non property style for making dynamic table
            TableColumn col = new TableColumn(colName);
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(i).toString()));
            tableView.getColumns().addAll(col);
            System.out.println("Column ["+i+"]: "+colName);
        }
    }
}
