package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import tatai.app.util.achievements.Achievement;
import tatai.app.util.achievements.AchievementCell;
import tatai.app.util.store.StoreCell;
import tatai.app.util.store.StoreItem;

public class AchievementsController extends ToolbarController {
    @FXML
    private Pane controls;
    @FXML private JFXListView<Achievement> storeItemList;
    @FXML private Pane sidePane;
    @FXML private Label balanceLabel;
    @FXML private JFXButton purchaseBtn;
    @FXML private JFXButton applyBtn;

    public void initialize() {
        super.initialize();
        storeItemList.setCellFactory(param -> new AchievementCell());
        storeItemList.setItems(FXCollections.observableArrayList(Main.achievementManager.getAchievementsArrayList()));
        storeItemList.getSelectionModel().selectFirst();
        update();
        storeItemList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        });
        
        String completedAchievements = Integer.toString(Main.achievementManager.numberOfCompletedAchievements());
        String totalAchievements = Integer.toString(Main.achievementManager.numberOfAchievements());
        balanceLabel.setText(completedAchievements + "/" + totalAchievements);
    }

    private void update() {
        storeItemList.refresh();
        balanceLabel.setText(Integer.toString(Main.store.getBalance()));
    }
}
