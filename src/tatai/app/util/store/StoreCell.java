package tatai.app.util.store;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import tatai.app.util.Layout;

import java.io.IOException;

public class StoreCell extends ListCell<StoreItem>{

    private FXMLLoader loader;

    @FXML private Pane dataPane;

    @FXML private Label nameLabel;

    @FXML private FontAwesomeIconView icon, ownedIcon;

    @FXML private Label descLabel;

    @FXML private Label costLabel;

    @Override
    protected void updateItem(StoreItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loadFXML();
            }

            nameLabel.setText(item.itemname);
            icon.setIcon(item.icon);
            descLabel.setText(item.itemdescription);
            if (!item.purchased) {
                ownedIcon.setVisible(false);
                costLabel.setVisible(true);
                icon.setVisible(true);
                costLabel.setText(Integer.toString(item.cost));
            } else {
                costLabel.setVisible(false);
                icon.setVisible(false);
                ownedIcon.setVisible(true);
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
