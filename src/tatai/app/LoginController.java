package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import tatai.app.util.TransitionFactory;

import java.io.IOException;

import static tatai.app.Main.database;

public class LoginController {

    @FXML
    private Pane mainPane;

    @FXML
    private Pane loginPane;

    @FXML
    private JFXComboBox<String> usernameSelector;

    @FXML
    private JFXButton newBtn;

    @FXML
    private JFXButton loginBtn;

    /**
     * Get the users and fill in usernameSelector with users in the database
     */
    public void initialize() {
        usernameSelector.getItems().addAll(database.getUsers());
        usernameSelector.setValue(database.getUsers().iterator().next()); // Automatically selects the first object.
    }

    @FXML
    void loginBtnPressed() throws IOException {
        Main.currentUser = usernameSelector.getValue();
        Main.currentSession = database.startSession();
        Scene scene = loginBtn.getScene();
        FXMLLoader loader = new FXMLLoader(Main.mainMenuLayout);
        Parent root = loader.load();
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(loginPane);
        ft.setOnFinished(event1 -> scene.setRoot(root)); // switch scenes when fade complete
        ft.play();
    }

    @FXML
    void newBtnPressed() {

    }

}
