package tatai.app;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.time.ZoneId;

import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import tatai.app.util.queries.QuestionLogQuery;

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
    private TextArea tempTextField;

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
                QuestionLogQuery query = new QuestionLogQuery(getUnixtimeSelected(), !allQuestionSets.isSelected(), questionSetCombo.getValue());
                query.execute();
                tempTextField.setText(query.tempOutput);
                //do something
                break;
            case "numbersPronounced":
                throw new UnsupportedOperationException("unimplemented");
            case "rounds":
                throw new UnsupportedOperationException("unimplemented");
        }

    }

}
