package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
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
    @FXML private JFXTextField operandMaxField;
    @FXML private JFXTextField highBoundField;
    @FXML private JFXCheckBox maoriCheckbox;
    @FXML private JFXButton saveSetBtn;
    @FXML private JFXButton shareBtn;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        // Setup the math operator combobox
        operatorCombo.getItems().addAll("+", "-", "x", "÷");
        operatorCombo.setValue("+");
        // Populate the list of question sets
        populateQuestionSets();

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

    }

    @FXML void shareBtnPressed() {

    }

}
