package tatai.app;

import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.time.ZoneId;

import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import tatai.app.util.queries.NumberQuery;
import tatai.app.util.queries.QuestionLogQuery;
import tatai.app.util.queries.RoundsQuery;

public class StatisticsController {

    @FXML
    protected void initialize() {
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
        questionSetCombo.getItems().addAll(Main.questionGenerators.keySet());
        questionSetCombo.setValue(Main.questionGenerators.keySet().iterator().next()); // Automatically selects the first object.
    }

    @FXML
    private JFXButton backBtn;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXCheckBox allDateCheckbox;

    @FXML
    private JFXCheckBox allQuestionSets;

    @FXML
    private JFXComboBox<String> questionSetCombo;

    @FXML
    private JFXRadioButton questionLogToggle;

    @FXML
    private ToggleGroup showType;

    @FXML
    private JFXRadioButton numbersToggle;

    @FXML
    private JFXRadioButton roundsToggle;

    @FXML
    private JFXButton loadBtn;

    @FXML
    private JFXCheckBox unfinishedRounds;

    @FXML
    private TableView<ObservableList> dataTable;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    void backBtnPressed(ActionEvent event) throws IOException {
        Scene scene = backBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        scene.setRoot(root);
    }

    /**
     * Gets the date selected in the DatePicker at the beginning of that day in the current timezone, converted to
     * UNIX time
     * @return The time in UNIX time
     */
    private long getUnixtimeSelected() {
        if (!allDateCheckbox.isSelected()) {
            return datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        } else {
            return 0;
        }
    }

    @FXML
    void loadBtnPressed(ActionEvent event) {
        switch ((String)showType.getSelectedToggle().getUserData()) {
            case "questionLog":
                progressBar.setProgress(JFXProgressBar.INDETERMINATE_PROGRESS);
                QuestionLogQuery query = new QuestionLogQuery(getUnixtimeSelected(), !allQuestionSets.isSelected(), questionSetCombo.getValue(), dataTable, null);
                query.setOnFinished(event1 -> progressBar.setProgress(0));
                query.execute();
                break;
            case "numbersPronounced":
                progressBar.setProgress(JFXProgressBar.INDETERMINATE_PROGRESS);
                NumberQuery nquery = new NumberQuery(getUnixtimeSelected(), !allQuestionSets.isSelected(), questionSetCombo.getValue(), dataTable, null);
                nquery.setOnFinished(event1 -> progressBar.setProgress(0));
                nquery.execute();
                break;
            case "rounds":
                progressBar.setProgress(JFXProgressBar.INDETERMINATE_PROGRESS);
                RoundsQuery rquery = new RoundsQuery(getUnixtimeSelected(), !allQuestionSets.isSelected(), questionSetCombo.getValue(), dataTable, unfinishedRounds.isSelected());
                rquery.setOnFinished(event1 -> progressBar.setProgress(0));
                rquery.execute();
                break;
        }

    }

}
