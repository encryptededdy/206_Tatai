package tatai.app;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleButton;
import eu.hansolo.tilesfx.Tile;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.practice.PracticeModeCell;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

public class PracticeModeController {

    @FXML private ImageView backgroundImage;
    @FXML private Pane dataPane, controls, backgroundPane, backBtn;
    @FXML private JFXListView<Integer> practiceList;
    @FXML private JFXToggleButton showAllToggle, bargraphToggle, statisticsToggle;
    @FXML private Tile accuracyTile;

    private int totalAnswered = 0;
    private int totalCorrect = 0;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        practiceList.setCellFactory(param -> new PracticeModeCell(this));
        setItems(false);
        showAllToggle.setOnAction(event -> setItems(showAllToggle.isSelected()));
        bargraphToggle.setOnAction(event -> changeTile());

        bargraphToggle.disableProperty().bind(statisticsToggle.selectedProperty().not());
        accuracyTile.visibleProperty().bind(statisticsToggle.selectedProperty());
    }

    private void setItems(boolean showAll) {
        Integer[] questions;
        if (showAll) {
            questions = new Integer[99];
            for (int i = 1; i < 100; i++)
                questions[i - 1] = i;
        } else {
            questions = new Integer[9];
            for (int i = 1; i < 10; i++)
                questions[i - 1] = i;
        }
        // Set these as the list items
        practiceList.getItems().clear();
        practiceList.setItems(FXCollections.observableArrayList(questions));
    }

    private void changeTile() {
        if (bargraphToggle.isSelected()) {
            accuracyTile.setSkinType(Tile.SkinType.SPARK_LINE);
        } else {
            accuracyTile.setSkinType(Tile.SkinType.CIRCULAR_PROGRESS);
        }
    }

    public void questionAnswered(boolean correct) {
        totalAnswered++;
        if (correct)
            totalCorrect++;

        // Update dial
        if (totalAnswered == 0) {
            accuracyTile.setValue(0);
        } else {
            double accuracy = totalCorrect / (double) totalAnswered;
            accuracyTile.setValue(accuracy*100);
        }
    }

    /**
     * Fades in the screen (to animate in)
     */
    void fadeIn() {
        TransitionFactory.fadeIn(dataPane).play();
    }

    /**
     * Animate out the screen then switch to the main menu
     * @throws IOException Exception can be thrown when loading FXML
     */
    @FXML void backBtnPressed() throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        loader.<MainMenuController>getController().setupFade(false);
        // Fade out items
        FadeTransition ft = TransitionFactory.fadeOut(dataPane, Main.transitionDuration/2);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), backgroundPane);
        st.setToX(0.5);
        st.setOnFinished(event -> {scene.setRoot(root); loader.<MainMenuController>getController().fadeIn();});
        ft.setOnFinished(event -> st.play());
        // animate
        ft.play();
    }

}
