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
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static tatai.app.Main.database;

/**
 * Controller for the login screen.
 */
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

    @FXML
    private Label lastLog;

    /**
     * Get the users and fill in usernameSelector with users in the database
     */
    public void initialize() {
        usernameSelector.getItems().addAll(database.getUsers());
        usernameSelector.setValue(database.getUsers().iterator().next()); // Automatically selects the first object.
        // Display the statistics, and make sure it updates everytime usernameSelector changes
        getStatistics();
        usernameSelector.valueProperty().addListener((observable, oldValue, newValue) -> getStatistics());
    }

    /**
     * Gets the playtime and questions statistics from the database, and writes them to the appropriate labels.
     * Also gets the last login time
     */
    private void getStatistics() {
        String newValue = usernameSelector.getValue();
        ResultSet ptrs = Main.database.returnOp("SELECT sum(sessionlength) FROM sessions WHERE username = '"+newValue+"'");
        ResultSet lastrs = Main.database.returnOp("SELECT max(date) FROM sessions WHERE username = '"+newValue+"'");
        ResultSet questionrs = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE username = '"+newValue+"'");
        long lastLogin = 0;
        try {
            ptrs.next();
            playtimeCounter.setText(Integer.toString(ptrs.getInt(1)/60)); // get the playtime in minutes
            questionrs.next();
            questionsCounter.setText(questionrs.getString(1)); // get the number of questions returned
            lastrs.next();
            lastLogin = Instant.now().getEpochSecond() - lastrs.getLong(1);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        // Convert the login time
        if (TimeUnit.SECONDS.toMinutes(lastLogin) < 60) {
            lastLog.setText(TimeUnit.SECONDS.toMinutes(lastLogin)+" minutes ago");
        } else {
            lastLog.setText(TimeUnit.SECONDS.toHours(lastLogin)+" hours ago");
        }
    }

    /**
     * Handles login process. Writes in the current user and begins the session. Then fades out and switches to the main menu
     * @throws IOException
     */
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

    /**
     * Handles creation of a new user
     * TODO: Actually implement this
     */
    @FXML
    void newBtnPressed() {

    }

}
