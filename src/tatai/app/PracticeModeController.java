package tatai.app;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleButton;
import eu.hansolo.tilesfx.Tile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import tatai.app.practice.PracticeModeCell;

/**
 * Controller for the Game's practice mode
 *
 * @author Edward
 */
public class PracticeModeController extends ToolbarController{

    @FXML private Pane controls;
    @FXML private JFXListView<Integer> practiceList;
    @FXML private JFXToggleButton showAllToggle, bargraphToggle, statisticsToggle;
    @FXML private Tile accuracyTile;

    private int totalAnswered = 0;
    private int totalCorrect = 0;

    /**
     * Sets up the list of questions to practice, and all bindings
     */
    public void initialize() {
        super.initialize();
        practiceList.setCellFactory(param -> new PracticeModeCell(this));
        setItems(false);
        showAllToggle.setOnAction(event -> setItems(showAllToggle.isSelected()));
        bargraphToggle.setOnAction(event -> changeTile());

        bargraphToggle.disableProperty().bind(statisticsToggle.selectedProperty().not());
        accuracyTile.visibleProperty().bind(statisticsToggle.selectedProperty());
    }

    /**
     * Populate the list of practice questions
     * @param showAll Show all 99 instead of just 1-10
     */
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

    /**
     * Set the stats tile type
     */
    private void changeTile() {
        if (bargraphToggle.isSelected()) {
            accuracyTile.setSkinType(Tile.SkinType.SPARK_LINE);
        } else {
            accuracyTile.setSkinType(Tile.SkinType.CIRCULAR_PROGRESS);
        }
    }

    /**
     * Called when a question gets answered in a practice cell
     * @param correct Whether the answer was correct
     */
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

}
