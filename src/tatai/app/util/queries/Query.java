package tatai.app.util.queries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import tatai.app.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Queries the Database for information and populates a TableView.
 * Abstract Class generates columns for the TableView and also handles the ability to set a listener for when the query
 * is complete.
 *
 * Implementing/Extending classes must implement execute, which involves running the SQL query itself and filling out
 * the rows of the TableView
 *
 * @author Edward
 */
public abstract class Query {
    String SQLQuery;
    TableView<ObservableList> tableView;
    // Names of the output columns
    ArrayList<String> columnNames = new ArrayList<>();
    private ArrayList<EventHandler<ActionEvent>> eventHandlers = new ArrayList<>();

    // Date and time format for output
    DateTimeFormatter dformat =
            DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                    .withLocale( Locale.ENGLISH )
                    .withZone( ZoneId.systemDefault() );

    public abstract void execute();

    /**
     * Add an event handler to be called at the completion of a Query
     * @param handler   the EventHandler to be called
     */
    public void setOnFinished(EventHandler<ActionEvent> handler) {
        eventHandlers.add(handler);
    }

    /**
     * Called when query is finished, to activate the event handlers
     */
    void completeQuery() {
        for (EventHandler<ActionEvent> handler : eventHandlers) {
            handler.handle(new ActionEvent());
        }
    }

    void columnGenerator() {

            /*
             * Based on code by Source: http://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
             *                  Author: Narayan G. Maharjan
             */

        tableView.getColumns().clear(); // Clear existing cols first
        // Populate output columns
        for(String colName : columnNames){
            final int i = columnNames.indexOf(colName);
            //We are using non property style for making dynamic table
            TableColumn<ObservableList, String> col = new TableColumn<>(colName);
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(i).toString()));
            tableView.getColumns().addAll(col);
            //System.out.println("Column ["+i+"]: "+colName);
        }
    }
}
