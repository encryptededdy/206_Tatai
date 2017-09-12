package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import tatai.app.util.TransitionFactory;

import java.io.IOException;
import java.sql.ResultSet;

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

    @FXML
    private Label questionsCounter;

    @FXML
    private Label playtimeCounter;

    /**
     * Get the users and fill in usernameSelector with users in the database
     */
    public void initialize() {
        usernameSelector.getItems().addAll(database.getUsers());
        usernameSelector.setValue(database.getUsers().iterator().next()); // Automatically selects the first object.
        getStatistics();
        usernameSelector.valueProperty().addListener((observable, oldValue, newValue) -> getStatistics());
    }

    private void getStatistics() {
        String newValue = usernameSelector.getValue();
        ResultSet ptrs = Main.database.returnOp("SELECT sum(sessionlength) FROM sessions WHERE username = '"+newValue+"'");
        ResultSet questionrs = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+newValue+"'");
        try {
            ptrs.next();
            playtimeCounter.setText(Integer.toString(ptrs.getInt(1)/60)); // get the playtime in minutes
            questionrs.next();
            questionsCounter.setText(questionrs.getString(1)); // get the number of questions returned
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @FXML
    void loginBtnPressed() throws IOException {
        Main.currentUser = usernameSelector.getValue(); // write the username
        Main.currentSession = database.startSession(); // start the session
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
