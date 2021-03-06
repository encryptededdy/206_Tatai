package tatai.app;

import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;
import tatai.app.util.queries.NumberQuery;
import tatai.app.util.queries.QuestionLogQuery;
import tatai.app.util.queries.RoundsQuery;

import java.io.IOException;
import java.time.ZoneId;

/**
 * Controller for the advanced statistics screen
 *
 * @author Edward
 */
public class StatisticsController {

    /**
     * Sets up for animation and also makes all listeners and bindings
     */
    @FXML protected void initialize() {
        // Set opacity of UI to 0 to allow for fade in
        mainStats.setOpacity(0);

        // Set the user data for the radio buttons
        questionLogToggle.setUserData("questionLog");
        numbersToggle.setUserData("numbersPronounced");
        roundsToggle.setUserData("rounds");

        // Bind the "All" options to disable the pickers
        datePicker.disableProperty().bind(allDateCheckbox.selectedProperty());
        questionSetCombo.disableProperty().bind(allQuestionSets.selectedProperty());

        // unfinished rounds toggle is irrelevant when roundsToggle isn't selected
        unfinishedRounds.disableProperty().bind(roundsToggle.selectedProperty().not());

        // Populate question set picker
        questionSetCombo.getItems().addAll(Main.store.generators.getGeneratorsString());
        questionSetCombo.setValue(Main.store.generators.getGeneratorsString().iterator().next()); // Automatically selects the first object.

        // Add listeners to update the table when selections change
        showType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateTable());
        unfinishedRounds.selectedProperty().addListener((observable, oldValue, newValue) -> updateTable());
        thisSessionCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> updateTable());
        questionSetCombo.valueProperty().addListener((observable, oldValue, newValue) -> updateTable());
        allQuestionSets.selectedProperty().addListener((observable, oldValue, newValue) -> updateTable());
        allDateCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> updateTable());
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateTable());
    }

    @FXML private Pane backBtn;
    @FXML private JFXDatePicker datePicker;
    @FXML private JFXCheckBox allDateCheckbox;
    @FXML private JFXCheckBox allQuestionSets;
    @FXML private JFXComboBox<String> questionSetCombo;
    @FXML private JFXRadioButton questionLogToggle;
    @FXML private ToggleGroup showType;
    @FXML private JFXRadioButton numbersToggle;
    @FXML private JFXRadioButton roundsToggle;
    @FXML private JFXButton loadBtn;
    @FXML private JFXCheckBox unfinishedRounds;
    @FXML private TableView<ObservableList> dataTable;
    @FXML private JFXProgressBar progressBar;
    @FXML private JFXCheckBox thisSessionCheckbox;
    @FXML private HBox mainStats;

    /**
     * Return back to the main menu
     * @throws IOException Exception can be thrown when loading FXML
     */
    @FXML
    void backBtnPressed() throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = Layout.DASHBOARD.loader();
        Parent root = loader.load();
        FadeTransition ft = TransitionFactory.fadeOut(mainStats);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<DashboardController>getController().fadeIn();});
        ft.play();
    }

    void fadeIn() {
        TransitionFactory.fadeIn(mainStats).play();
        updateTable();
    }

    /**
     * Gets the date selected in the DatePicker at the beginning of that day in the current timezone, converted to
     * UNIX time
     * @return The time in UNIX time
     */
    private long getUnixtimeSelected() {
        if (!allDateCheckbox.isSelected() && datePicker.getValue() != null) {
            return datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        } else {
            return 0;
        }
    }

    /**
     * Gets the Session selected by the user
     * @return The session number, or null if unselected
     */
    private Integer getSession() {
        if (thisSessionCheckbox.isSelected()) {
            return Main.currentSession;
        } else {
            return null;
        }
    }

    /**
     * Triggers a update of the table with new filter parameters
     */
    private void updateTable() {
        switch ((String)showType.getSelectedToggle().getUserData()) {
            case "questionLog":
                progressBar.setProgress(JFXProgressBar.INDETERMINATE_PROGRESS);
                QuestionLogQuery query = new QuestionLogQuery(getUnixtimeSelected(), !allQuestionSets.isSelected(), questionSetCombo.getValue(), dataTable, null, getSession());
                query.setOnFinished(event1 -> progressBar.setProgress(0));
                query.execute();
                break;
            case "numbersPronounced":
                progressBar.setProgress(JFXProgressBar.INDETERMINATE_PROGRESS);
                NumberQuery nquery = new NumberQuery(getUnixtimeSelected(), !allQuestionSets.isSelected(), questionSetCombo.getValue(), dataTable, null, getSession());
                nquery.setOnFinished(event1 -> progressBar.setProgress(0));
                nquery.execute();
                break;
            case "rounds":
                progressBar.setProgress(JFXProgressBar.INDETERMINATE_PROGRESS);
                RoundsQuery rquery = new RoundsQuery(getUnixtimeSelected(), !allQuestionSets.isSelected(), questionSetCombo.getValue(), dataTable, unfinishedRounds.isSelected(), getSession());
                rquery.setOnFinished(event1 -> progressBar.setProgress(0));
                rquery.execute();
                break;
        }

    }

}
