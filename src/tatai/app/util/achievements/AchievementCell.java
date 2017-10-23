package tatai.app.util.achievements;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import tatai.app.util.Layout;

import java.io.IOException;

public class AchievementCell extends ListCell<Achievement> {
    private FXMLLoader loader;

    @FXML
    private Pane dataPane;

    @FXML private Label nameLabel;

    @FXML private FontAwesomeIconView icon;

    @FXML private Label descLabel;

    @FXML private Label rewardLabel;

    @Override
    protected void updateItem(Achievement achievement, boolean empty) {
        super.updateItem(achievement, empty);

        if (empty || achievement == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loadFXML();
            }

            nameLabel.setText(achievement.getName());
            icon.setIcon(achievement.getIcon());
            descLabel.setText(achievement.getDescription());
            icon.setFill(achievement.getColor());
            rewardLabel.setText(Integer.toString(achievement.getReward()));

            if (!achievement.isCompleted()) {
                icon.setOpacity(0.2);
            } else {
                icon.setOpacity(1);

            }

            setText(null);
            setGraphic(dataPane);

        }
    }

    private void loadFXML() {
        loader = Layout.ACHIEVEMENTCELL.loader();
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
