package tatai.app.util.queries;

import javafx.concurrent.Task;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import tatai.app.Main;

import java.sql.ResultSet;

public class PreviousRoundScoreQuery extends Query {
    BarChart _recentRoundScoresBarChart;
    public PreviousRoundScoreQuery(BarChart recentRoundScoresBarChart) {
        _recentRoundScoresBarChart = recentRoundScoresBarChart;
        SQLQuery = "SELECT noquestions, nocorrect FROM (SELECT noquestions, nocorrect, roundID FROM rounds WHERE username = '"+ Main.currentUser+"' AND isComplete = 1 ORDER BY roundID DESC LIMIT 10) as output ORDER BY output.roundID ASC";
    }

    public void execute() {
        XYChart.Series recentScoresSeries = new XYChart.Series();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ResultSet rs = Main.database.returnOp(SQLQuery);
                    int count = rs.getInt(1);
                    while (rs.next()) {
                        recentScoresSeries.getData().add(new XYChart.Data(new Integer(count).toString(), rs.getInt(2)));
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
}
