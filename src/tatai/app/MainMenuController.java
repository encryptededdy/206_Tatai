package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private JFXButton practiceNumbersBtn;

    @FXML
    private JFXButton practiceSumsBtn;

    @FXML
    private JFXButton settingsBtn;

    @FXML
    private JFXButton micTestBtn;

    @FXML
    void practiceNumbersBtnPressed(ActionEvent event) {
        System.out.println("Time to practice numbers!");
    }

}
