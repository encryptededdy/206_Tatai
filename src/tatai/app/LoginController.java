package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;
import tatai.app.util.net.NetConnection;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tatai.app.Main.database;

/**
 * Controller for the login screen.
 *
 * @author Edward
 */
public class LoginController {
    @FXML private Pane mainPane;
    @FXML private Pane loginPane;
    @FXML private JFXButton newBtn;
    @FXML private JFXButton loginBtn;
    @FXML private Pane newUserModalStart;
    @FXML private Pane newUserModal;
    @FXML private JFXTextField usernameField;
    @FXML private JFXButton createAccntBtn;
    @FXML private Label usernameInstructions;
    @FXML private ImageView backgroundImage, banner;
    @FXML private JFXListView<String> userList;

    private ParallelTransition expandModalTransition;

    /**
     * Get the users and fill in usernameSelector with users in the database
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);

        updateUsernameList();
        // Setup the expandModalTransition
        TranslateTransition tt = TransitionFactory.move(newUserModalStart, (-70), (-120));
        tt.setFromX(0);
        tt.setFromY(0);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration), newUserModalStart);
        st.setFromY(1);
        st.setFromX(1);
        st.setToX(3);
        st.setToY(6.73809524);
        st.setInterpolator(Interpolator.EASE_OUT);
        tt.setInterpolator(Interpolator.EASE_OUT);
        expandModalTransition = new ParallelTransition(tt, st);
        // Setup usernamechecker
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> usernameChecker());
        userList.getSelectionModel().selectFirst();
    }

    /**
     * Populates the list of Usernames from the database
     */
    private void updateUsernameList() {
        userList.setCellFactory(param -> new LoginScreenCell());
        userList.setItems(FXCollections.observableArrayList(Main.database.getUsers()));
    }

    /**
     * Handles login process. Writes in the current user and begins the session. Then fades out and switches to the main menu
     * @throws IOException Exception can be thrown when loading FXML
     */
    @FXML void loginBtnPressed() throws IOException {
        Main.currentUser = userList.getSelectionModel().getSelectedItem(); // write the username
        Main.currentSession = database.startSession(); // start the session
        Main.populateGenerators(); // populate questiongenerators
        Main.netConnection = new NetConnection(); // open a TataiNet session
        Scene scene = loginBtn.getScene();
        FXMLLoader loader = Layout.MAINMENU.loader();
        Parent root = loader.load();
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(loginPane);
        ft.setOnFinished(event1 -> scene.setRoot(root)); // switch scenes when fade complete
        ft.play();
    }

    /**
     * Closes the create user modal
     */
    @FXML void closeModalBtnPressed() {
        FadeTransition ft = TransitionFactory.fadeOut(newUserModal, Main.transitionDuration/2);
        expandModalTransition.setRate(-1);
        expandModalTransition.setOnFinished(event -> newUserModalStart.setVisible(false));
        ft.setOnFinished(event -> {newUserModal.setVisible(false); expandModalTransition.play();});
        ft.playFromStart();
    }

    /**
     * Handles creation of a new user
     */
    @FXML void newBtnPressed() {
        newUserModal.setOpacity(0);
        newUserModal.setVisible(true);
        newUserModalStart.setVisible(true);
        expandModalTransition.setOnFinished(event -> TransitionFactory.fadeIn(newUserModal, Main.transitionDuration/2).play());
        expandModalTransition.setRate(1);
        expandModalTransition.playFromStart();
    }

    /**
     * Opens the create user modal
     */
    @FXML void createAccntBtnPressed() {
        Main.database.newUser(usernameField.getText());
        updateUsernameList();
        //usernameSelector.setValue(usernameField.getText());
        closeModalBtnPressed();
    }

    /**
     * Triggered when username is entered. Checks if it's valid
     */
    private void usernameChecker() {
        String name = usernameField.getText();
        Pattern p = Pattern.compile("[^a-zA-Z-_.\\d\\s:]");
        Matcher m = p.matcher(name);
        if (name.contains("'); ")) { // Easter egg if the user attempts SQL injection
            usernameInstructions.setText("Oh wow, you tried to SQL inject a educational game. Congrats.");
            banner.setImage(new Image(getClass().getResourceAsStream("resources/hackermanbanner.jpg")));;
        }
        if (m.find() || name.length() > 12 || Main.database.getUsers().contains(name)) {
            usernameInstructions.setTextFill(Color.RED);
            createAccntBtn.setDisable(true);
        } else {
            usernameInstructions.setTextFill(Color.LIGHTGRAY);
            createAccntBtn.setDisable(false);
        }
    }

}
