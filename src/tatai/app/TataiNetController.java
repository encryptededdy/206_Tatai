package tatai.app;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import tatai.app.util.net.LeaderboardEntry;
import tatai.app.util.net.LeaderboardViewCell;

public class TataiNetController extends ToolbarController {

    @FXML private Pane controls, signUpPane;
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
        super.initialize();
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
        if (name.length() > 14) {
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
