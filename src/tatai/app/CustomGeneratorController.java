package tatai.app;

import com.google.gson.Gson;
import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.questions.generators.MathGenerator;
import tatai.app.questions.generators.MathOperator;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.factories.DialogFactory;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;
import java.sql.PreparedStatement;

/**
 * Controller for the Custom Question Set creation screen
 */
public class CustomGeneratorController {

    @FXML private ImageView backgroundImage;
    @FXML private Pane backgroundPane;
    @FXML private Pane dataPane;
    @FXML private Pane controls;
    @FXML private Pane backBtn;
    @FXML private JFXListView<String> qSetList;
    @FXML private JFXButton deleteBtn;
    @FXML private JFXComboBox<String> operatorCombo;
    @FXML private JFXCheckBox times1Checkbox;
    @FXML private JFXTextField operandMaxField, roundNameField;
    @FXML private JFXTextField highBoundField;
    @FXML private JFXCheckBox maoriCheckbox;
    @FXML private JFXButton saveSetBtn;
    @FXML private JFXButton shareBtn;
    @FXML private Label operandLabel;
    @FXML private Label boundLabel;
    @FXML private Label nameLabel;

    private boolean uploading;

    /**
     * Sets up initial values and bindings
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);
        // Setup the math operator combobox
        operatorCombo.getItems().addAll("+", "-", "x", "÷");
        operatorCombo.setValue("+");
        // Populate the list of question sets
        populateQuestionSets();
        // Setup restrictions on the text fields to be 2 numbers only
        operandMaxField.setTextFormatter(new TextFormatter<String>(change -> (change.getText().matches("[0-9]*")) ? change : null));
        highBoundField.setTextFormatter(new TextFormatter<String>(change -> (change.getText().matches("[0-9]*")) ? change : null));
        // Configure validation
        BooleanBinding checkOperand = Bindings.createBooleanBinding(this::checkOperandField, operandMaxField.textProperty(), operatorCombo.getSelectionModel().selectedItemProperty());
        BooleanBinding checkBound = Bindings.createBooleanBinding(this::checkBoundField, highBoundField.textProperty());
        BooleanBinding nameText = Bindings.createBooleanBinding(this::checkNameText, roundNameField.textProperty());
        saveSetBtn.disableProperty().bind(checkOperand.or(checkBound.or(nameText)));

        // Configure validation of delete/sharable
        BooleanBinding checkDeletable = Bindings.createBooleanBinding(this::checkisCustom, qSetList.getSelectionModel().selectedItemProperty());
        deleteBtn.disableProperty().bind(checkDeletable.not());
        shareBtn.disableProperty().bind(checkDeletable.not());

        // Prepare for animations
        dataPane.setOpacity(0);
    }

    /**
     * Populates the ListView of Question Sets
     */
    private void populateQuestionSets() {
        qSetList.setItems(FXCollections.observableArrayList(Main.questionGenerators.keySet()));
    }

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

    /**
     * Delete the selected item from the questionGenerators list and also from the database
     */
    @FXML void deleteBtnPressed() {
        String selected = qSetList.getSelectionModel().getSelectedItem();
        Main.questionGenerators.remove(selected);
        PreparedStatement ps = Main.database.getPreparedStatement("DELETE FROM savedSets WHERE username = ? AND setName = ?");
        try {
            ps.setString(1, Main.currentUser);
            ps.setString(2, selected);
            ps.executeUpdate();
        } catch ( Exception e ) {
            e.printStackTrace();
            DialogFactory.exception("Internal Database error.", "Database Error", e);
        }
        populateQuestionSets();
    }

    /**
     * Saves a new Question Set
     */
    @FXML void saveSetBtnPressed() {
        Gson gson = new Gson();
        // Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MathGenerator generator = new MathGenerator(
                Integer.parseInt(highBoundField.getText()),
                Integer.parseInt(operandMaxField.getText()),
                getOperator(),
                roundNameField.getText(),
                times1Checkbox.isSelected(),
                maoriCheckbox.isSelected(),
                true);
        String generatorJSON = gson.toJson(generator);
        Main.questionGenerators.put(roundNameField.getText(), generator);
        populateQuestionSets();
        // Added, now we clear the fields
        highBoundField.setText("");
        operandMaxField.setText("");
        roundNameField.setText("");
        System.out.println("JSON Output;");
        System.out.println(generatorJSON);
        // Save it in the database
        PreparedStatement ps = Main.database.getPreparedStatement("INSERT INTO savedSets (username, json, setName, fromNet) VALUES (?, ?, ?, ?)");
        try {
            ps.setString(1, Main.currentUser);
            ps.setString(2, generatorJSON);
            ps.setString(3, generator.getGeneratorName());
            ps.setBoolean(4, false);
            ps.executeUpdate();
        } catch ( Exception e ) {
                e.printStackTrace();
                DialogFactory.exception("Internal Database error.", "Database Error", e);
        }
    }

    @FXML void shareBtnPressed() {
        //TODO: Implement online sharing
        if (!shareBtn.getText().equals("Uploaded")) {
            shareBtn.setText("Uploading");
            Gson gson = new Gson();
            String selected = qSetList.getSelectionModel().getSelectedItem();
            String generatorJSON = gson.toJson(Main.questionGenerators.get(selected));
            EventHandler<WorkerStateEvent> onSuccess = event -> shareBtn.setText("Uploaded");
            EventHandler<WorkerStateEvent> onFail = event -> shareBtn.setText("Error");
            Main.netConnection.uploadJSON(generatorJSON, "ezTatai_gen_1", onSuccess, onFail);
        }
    }

    /**
     * Checks whether the selected QuestionSet is custom
     * @return Whether it's custom
     */
    private boolean checkisCustom() {
        shareBtn.setText("Share");
        if (qSetList.getSelectionModel().getSelectedItem() != null) {
            String selected = qSetList.getSelectionModel().getSelectedItem();
            QuestionGenerator selectedGenerator = Main.questionGenerators.get(selected);
            return selectedGenerator.isCustom();
        } else {
            return false;
        }
    }

    /**
     * Gets the current operator selected in the ComboBox
     */
    private MathOperator getOperator() {
        switch (operatorCombo.getSelectionModel().getSelectedItem()) {
            case "+":
                return MathOperator.ADD;
            case "-":
                return MathOperator.SUBTRACT;
            case "x":
                return MathOperator.MULTIPLY;
            case "÷":
                return MathOperator.DIVIDE;
            default:
                return null;
        }
    }

    /**
     * Checks the numerical text fields to make sure they're valid.
     */
    private boolean checkOperandField() {
        String operand = operandMaxField.getText();
        // Check the operand field
        if (operand.equals("")) {
            operandLabel.setText("Enter a number");
            return true;
        } else if (Integer.parseInt(operand) > 12 && getOperator() == MathOperator.DIVIDE) {
            operandLabel.setText("Division Max: 12");
            return true;
        } else if (Integer.parseInt(operand) > 99) {
            operandLabel.setText("Max: 99");
            return true;
        } else {
            operandLabel.setText("OK");
            return false;
        }
    }

    /**
     * Checks the numerical text fields to make sure they're valid.
     */
    private boolean checkBoundField() {
        String bound = highBoundField.getText();
        // Check the bound field
        if (bound.equals("")) {
            boundLabel.setText("Enter a number");
            return true;
        } else if (Integer.parseInt(bound) > 99) {
            boundLabel.setText("Max: 99");
            return true;
        } else {
            boundLabel.setText("OK");
            return false;
        }
    }

    /**
     * Checks the question set name fields to make sure they're valid.
     */
    private boolean checkNameText() {
        String name = roundNameField.getText();
        if (name.equals("")) {
            nameLabel.setText("Please enter a name");
            return true;
        } else if (name.length() > 20) {
            nameLabel.setText("Name too long");
            return true;
        } else if (Main.questionGenerators.containsKey(name)) {
            nameLabel.setText("Already exists");
            return true;
        } else {
            nameLabel.setText("OK");
            return false;
        }
    }

}
