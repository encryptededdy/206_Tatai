package tatai.app.util.queries;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import tatai.app.Main;

import java.sql.ResultSet;

/**
 * A query that gets the scores of the most recent 10 completed rounds completed by the current user and plots them in a
 * bar chart which is passed to the constructor
 *
 * @author Zach
 */
public class PreviousRoundScoreQuery extends Query {
    BarChart _recentRoundScoresBarChart;
    public PreviousRoundScoreQuery(BarChart recentRoundScoresBarChart) {
        _recentRoundScoresBarChart = recentRoundScoresBarChart;
        SQLQuery = "SELECT noquestions, nocorrect FROM (SELECT noquestions, nocorrect, roundID FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 10) as output ORDER BY output.roundID ASC";
    }

    /**
     * Creates a Task to query the database off the event dispatch thread and generates XYChart.Data objects based on the
     * scores of the past rounds, executes the query and when completed updates the BarChart to contain the data from the
     * query.
     */
    public void execute() {
        XYChart.Series<String, Integer> recentScoresSeries = new XYChart.Series<>();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ResultSet rs = Main.database.returnOp(SQLQuery);
                    int count = 10;
                    while (rs.next()) {
                        XYChart.Data data;
                        if (false) {
                            data = createData(Integer.toString(count), rs.getInt(2), Color.web("0x3F51B5"));
                        } else {
                            data = createData(Integer.toString(count), rs.getInt(2),Color.web( "0x455864"));
                        }

                        recentScoresSeries.getData().add(data);
                        count--;
                    }
                    recentScoresSeries.getData().get(recentScoresSeries.getData().size() - 1).getNode().setStyle("-fx-background-color: #3F51B5; -fx-background-radius: 4px;");
                    while (count > 0) {
                        XYChart.Data data;
                        data = createData(Integer.toString(count), 0, Color.web("0x3F51B5"));
                        recentScoresSeries.getData().add(0, data);
                        count--;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            _recentRoundScoresBarChart.getData().add(recentScoresSeries);

            completeQuery();
        });

        new Thread(task).start();
    }

    /**
     * creates and styles an XYChart.Data object appropirately for the graph
     * @param axisLabel the score label to be put above the bar on the graph
     * @param value the value of the score for the given round
     * @param color the color that the bar will be shaded
     * @return the styled XYChart.Data object
     */
    private XYChart.Data createData(String axisLabel, int value, Color color) {
        XYChart.Data data =  new XYChart.Data(axisLabel, value);
        Label label = new Label();

        if (value > 0) {
            String text = Integer.toString(value);
            label.setText(text);
            label.setTextFill(new Color(1, 1, 1, 1));
            label.setFont(Main.currentFont);
        }

        StackPane node = new StackPane();
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);
        StackPane.setMargin(group, new Insets(-26, 0, 0, 0));

        node.setBackground(new Background(new BackgroundFill(color, new CornerRadii(4.0), null)));
        node.getChildren().add(group);
        node.setMaxWidth(1);
        data.setNode(node);

        return data;
    }
}
