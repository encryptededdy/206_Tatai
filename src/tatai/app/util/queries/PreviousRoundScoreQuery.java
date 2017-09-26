package tatai.app.util.queries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tatai.app.Main;

import java.sql.ResultSet;

public class PreviousRoundScoreQuery extends Query {
    BarChart _recentRoundScoresBarChart;
    public PreviousRoundScoreQuery(BarChart recentRoundScoresBarChart) {
        _recentRoundScoresBarChart = recentRoundScoresBarChart;
        SQLQuery = "SELECT noquestions, nocorrect FROM (SELECT noquestions, nocorrect, roundID FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 10) as output ORDER BY output.roundID ASC";
    }

    public void execute() {
        XYChart.Series<String, Integer> recentScoresSeries = new XYChart.Series();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ResultSet rs = Main.database.returnOp(SQLQuery);
                    int count = 10;
                    while (rs.next()) {
                        XYChart.Data data;
                        if (count == 1) {
                            data = createData(Integer.toString(count), rs.getInt(2), Color.web("0x3F51B5"));
                        } else {
                            data = createData(Integer.toString(count), rs.getInt(2),Color.web( "0x455864"));
                        }

                        recentScoresSeries.getData().add(data);
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

    private XYChart.Data createData(String axisLabel, int value, Color color) {
        XYChart.Data data =  new XYChart.Data(axisLabel, value);

        String text = Integer.toString(value);

        StackPane node = new StackPane();
        Label label = new Label(text);
        label.setTextFill(new Color(1, 1, 1, 1));
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);
        StackPane.setMargin(group, new Insets(5, 0, 0, 0));

        node.setBackground(new Background(new BackgroundFill(color, new CornerRadii(4.0), null)));
        node.getChildren().add(group);
        data.setNode(node);

        return data;
    }
}
