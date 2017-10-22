package tatai.app.util.achievements;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import tatai.app.util.Layout;
import tatai.app.util.store.StoreItem;

import java.io.IOException;

public class AchievementCell extends ListCell<Achievement> {
    private FXMLLoader loader;

    @FXML
    private Pane dataPane;

    @FXML private Label nameLabel;

    @FXML private FontAwesomeIconView icon, ownedIcon;

    @FXML private Label descLabel;

    @FXML private Label costLabel;

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
            //icon.setFill(achievement.getColor());
            icon.setFill(achievement.getColor());
            if (!achievement.isCompleted()) {
                ownedIcon.setVisible(false);
                costLabel.setVisible(true);
                icon.setVisible(true);
                icon.setOpacity(0.2);
                costLabel.setText(Integer.toString(achievement.getReward()));
            } else {
                ownedIcon.setVisible(false);
                costLabel.setVisible(true);
                icon.setVisible(true);
            }

            setText(null);
            setGraphic(dataPane);

        }
    }

    private void loadFXML() {
        loader = Layout.STORECELL.loader();
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
