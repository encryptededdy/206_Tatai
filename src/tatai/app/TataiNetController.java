package tatai.app;

import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tatai.app.util.TransitionFactory;
import tatai.app.util.net.LeaderboardEntry;
import tatai.app.util.net.LeaderboardViewCell;

import java.io.IOException;

public class TataiNetController {

    @FXML private ImageView backgroundImage;

    @FXML private Pane backgroundPane, dataPane, controls, backBtn, signUpPane;

    @FXML private JFXListView<LeaderboardEntry> leaderboardList;

    @FXML private JFXComboBox<String> scoreboardComboGameMode;

    @FXML private Label usernameInstructions, usernameLabel;

    @FXML private ProgressIndicator leaderboardProgress;

    @FXML private JFXTextField usernameField;

    @FXML private JFXButton registerBtn;

    @FXML private JFXProgressBar registerProgress;


    private ObservableList<LeaderboardEntry> leaderboard;

    /**
     * Populates the gamemode selector ComboBox
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);
        scoreboardComboGameMode.getItems().addAll(Main.questionGenerators.keySet());
        scoreboardComboGameMode.setValue(Main.questionGenerators.keySet().iterator().next()); // Automatically selects the first object.
        scoreboardComboGameMode.valueProperty().addListener((observable, oldValue, newValue) -> populateLeaderboard());
        // Sets up the listview's cellfactory
        leaderboardList.setCellFactory(param -> new LeaderboardViewCell());
        populateLeaderboard();
        if (Main.netConnection.getUsername() == null) {
            // User isn't registered, show registration screen
            usernameLabel.setText("Unregistered");
            signUpPane.setVisible(true);
            // Setup usernamechecker
            usernameField.textProperty().addListener((observable, oldValue, newValue) -> usernameChecker());
        } else {
            usernameLabel.setText("Logged in: "+Main.netConnection.getUsername());
        }
    }

    /**
     * Fades in the screen (to animate in)
     */
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

    private void populateLeaderboard() {
        Task<Void> populateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                leaderboard = FXCollections.observableArrayList(Main.netConnection.getLeaderboard(scoreboardComboGameMode.getValue()));
                return null;
            }
        };
        populateTask.setOnSucceeded(event -> {leaderboardList.setItems(leaderboard); leaderboardProgress.setVisible(false);});
        leaderboardProgress.setVisible(true);
        new Thread(populateTask).start();
    }

    /**
     * Triggered when username is entered. Checks if it's valid
     */
    private void usernameChecker() {
        String name = usernameField.getText();
        if (name.length() > 12) {
            usernameInstructions.setTextFill(Color.RED);
            registerBtn.setDisable(true);
        } else {
            usernameInstructions.setTextFill(Color.WHITE);
            registerBtn.setDisable(false);
        }
    }

    /**
     * Register a new user
     */
    @FXML private void registerBtnPressed() {
        // register the user
        registerProgress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        Main.netConnection.registerUser(usernameField.getText(), event -> {signUpPane.setVisible(false); registerProgress.setProgress(0); usernameLabel.setText("Logged in: "+usernameField.getText());}, event -> {usernameInstructions.setText("User already exists"); registerProgress.setProgress(0);}, event -> {usernameInstructions.setText("Network Error"); registerProgress.setProgress(0);});
    }

}
