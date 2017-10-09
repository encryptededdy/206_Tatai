package tatai.app;

import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
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
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

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

    private MathOperator operator = MathOperator.ADD;

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

        // Configure updating the selected operator
        operatorCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateOperator());

        // Prepare for animations
        dataPane.setOpacity(0);
    }

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

    @FXML void deleteBtnPressed() {

    }

    @FXML void saveSetBtnPressed() {
        MathGenerator generator = new MathGenerator(
                Integer.parseInt(highBoundField.getText()),
                Integer.parseInt(operandMaxField.getText()),
                operator,
                roundNameField.getText(),
                times1Checkbox.isSelected(),
                maoriCheckbox.isSelected(),
                true);

        Main.questionGenerators.put(roundNameField.getText(), generator);
        populateQuestionSets();
        // Added, now we clear the fields
        highBoundField.setText("");
        operandMaxField.setText("");
        roundNameField.setText("");
    }

    @FXML void shareBtnPressed() {

    }

    private void updateOperator() {
        switch (operatorCombo.getSelectionModel().getSelectedItem()) {
            case "+":
                operator = MathOperator.ADD; break;
            case "-":
                operator = MathOperator.SUBTRACT; break;
            case "x":
                operator = MathOperator.MULTIPLY; break;
            case "÷":
                operator = MathOperator.DIVIDE; break;
        }
    }

    /**
     * Checks the numerical text fields to make sure they're valid.
     */
    private boolean checkOperandField() {
        updateOperator();
        String operand = operandMaxField.getText();
        // Check the operand field
        if (operand.equals("")) {
            operandLabel.setText("Enter a number");
            return true;
        } else if (Integer.parseInt(operand) > 12 && operator == MathOperator.DIVIDE) {
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
