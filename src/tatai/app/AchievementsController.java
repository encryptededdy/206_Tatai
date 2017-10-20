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
        storeItemList.setItems(FXCollections.observableArrayList(Main.store.achievements.getAchievementsArrayList()));
        storeItemList.getSelectionModel().selectFirst();
        update();
        storeItemList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //checkApplyable();
            //checkPurchasable();
        });
    }

    private void update() {
        storeItemList.refresh();
        //checkPurchasable();
        //checkApplyable();
        balanceLabel.setText(Integer.toString(Main.store.getBalance()));
    }

    /*
    @FXML void applyBtnPressed() {
        storeItemList.getSelectionModel().getSelectedItem().applyChanges();
        update();
        applyBtn.setDisable(true);
    }

    @FXML void purchaseBtnPressed() {
        storeItemList.getSelectionModel().getSelectedItem().purchase();
        update();
    }

    private void checkPurchasable() {
        purchaseBtn.setDisable(!storeItemList.getSelectionModel().getSelectedItem().isPurchaseable());
    }

    private void checkApplyable() {
        applyBtn.setDisable(!storeItemList.getSelectionModel().getSelectedItem().isPurchased());
    }*/
}
