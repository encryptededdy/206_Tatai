package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.util.DisplaysAchievements;
import tatai.app.util.achievements.AchievementView;
import tatai.app.util.factories.TransitionFactory;
import tatai.app.util.store.StoreCell;
import tatai.app.util.store.StoreItem;
import tatai.app.util.store.StoreManager;

/**
 * Controller for the TataiStore screen
 *
 * @author Edward
 */
public class StoreController extends ToolbarController implements DisplaysAchievements {
    @FXML private Pane controls, achievementPane;
    @FXML private JFXListView<StoreItem> storeItemList;
    @FXML private Pane sidePane;
    @FXML private Label balanceLabel;
    @FXML private JFXButton purchaseBtn;
    @FXML private JFXButton applyBtn;

    private StoreManager store = Main.store;

    private TranslateTransition achievementTransition;

    /**
     * Setup transitions and animations used, and set bindings for list selections
     */
    public void initialize() {
        super.initialize();
        storeItemList.setCellFactory(param -> new StoreCell());
        storeItemList.setItems(FXCollections.observableArrayList(Main.store.getItems()));
        storeItemList.getSelectionModel().selectFirst();
        update();
        storeItemList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            checkApplyable();
            checkPurchasable();
        });

        // Achievement transition
        achievementTransition = TransitionFactory.move(achievementPane, 0, -60);
        achievementTransition.setFromY(0);
        achievementTransition.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft2 = TransitionFactory.fadeOut(achievementPane);
        PauseTransition pt2 = new PauseTransition(Duration.seconds(1.5));
        pt2.setOnFinished(event -> ft2.play());
        achievementTransition.setOnFinished(event -> pt2.play());
    }

    /**
     * Updates the list of store items, and sets the purchase and apply buttons
     */
    private void update() {
        storeItemList.refresh();
        checkPurchasable();
        checkApplyable();
        balanceLabel.setText(Integer.toString(Main.store.getBalance()));
    }

    /**
     * Applies the changes for the current selected store item
     */
    @FXML void applyBtnPressed() {
        storeItemList.getSelectionModel().getSelectedItem().applyChanges();
        Main.store.lastApplied = storeItemList.getSelectionModel().getSelectedItem();
        update();
        applyBtn.setDisable(true);
    }

    /**
     * Purchase the item selected
     */
    @FXML void purchaseBtnPressed() {
        storeItemList.getSelectionModel().getSelectedItem().purchase();
        // Give achievements
        if (store.numberPurchased() >= 1 && !Main.store.achievements.getAchievements().get("Bargain Hunter").isCompleted()) {
            Main.store.achievements.getAchievements().get("Bargain Hunter").setCompleted(this, achievementPane);
        }
        if (store.numberPurchased() == store.numberItems() && !Main.store.achievements.getAchievements().get("Shopaholic").isCompleted()) {
            Main.store.achievements.getAchievements().get("Shopaholic").setCompleted(this, achievementPane);
        }
        update();
    }

    // check if the selected item can be purchased, and, if not, disable the purchase button
    private void checkPurchasable() {
        purchaseBtn.setDisable(!storeItemList.getSelectionModel().getSelectedItem().isPurchaseable());
    }

    // check if the selected item can be applied, and, if not, disable the apply button
    private void checkApplyable() {
        applyBtn.setDisable(!storeItemList.getSelectionModel().getSelectedItem().isPurchased());
    }

    /**
     * Handles the animations for the animation notification
     * @param achievement The achievement to animate
     */
    public void animateAchievement(AchievementView achievement) {
        achievementPane.setOpacity(1);
        achievementPane.getChildren().clear();
        achievementPane.getChildren().setAll(achievement.getNode());
        achievementPane.setVisible(true);
        achievementTransition.play();
    }
}
