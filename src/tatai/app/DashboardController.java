package tatai.app;

import eu.hansolo.tilesfx.Tile;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import tatai.app.util.TransitionFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private Tile accuracyTile;

    @FXML
    private Pane dataPane;

    public void initialize() {
        dataPane.setOpacity(0);
        populateAccuracyTile();
    }

    public void fadeIn() {
        TransitionFactory.fadeIn(dataPane).play();
    }

    private void populateAccuracyTile() {
        ResultSet total = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"'");
        ResultSet correct = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+Main.currentUser+"' AND correct = 1");
        try {
            total.next();
            correct.next();
            accuracyTile.setMaxValue(total.getInt(1));
            accuracyTile.setValue(correct.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
