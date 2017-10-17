package tatai.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import tatai.app.questions.generators.FixedGenerator;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.net.LeaderboardEntry;
import tatai.app.util.net.LeaderboardViewCell;
import tatai.app.util.store.SerializationAdapter;

import java.io.IOException;

public class TataiNetController extends ToolbarController {

    @FXML private Pane controls, signUpPane, gameIDPane;
    @FXML private JFXListView<LeaderboardEntry> leaderboardList;
    @FXML private JFXComboBox<String> scoreboardComboGameMode, newGameModeCombo;
    @FXML private Label usernameInstructions, usernameLabel, gameIDLabel;
    @FXML private ProgressIndicator leaderboardProgress;
    @FXML private JFXTextField usernameField, gameIDBox;
    @FXML private JFXButton registerBtn, joinGameBtn, newGameBtn;
    @FXML private JFXProgressBar registerProgress;


    private ObservableList<LeaderboardEntry> leaderboard;

    /**
     * Populates the gamemode selector ComboBox
     */
    public void initialize() {
        super.initialize();
        // Populate gamemodes
        scoreboardComboGameMode.getItems().addAll(Main.store.generators.getGeneratorsString(true));
        scoreboardComboGameMode.setValue(Main.store.generators.getGeneratorsString(true).iterator().next()); // Automatically selects the first object.

        // Populate the new game items too
        newGameModeCombo.getItems().addAll(Main.store.generators.getGeneratorsString(true));
        newGameModeCombo.setValue(Main.store.generators.getGeneratorsString(true).iterator().next());

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

    @FXML private void newGameBtnPressed() throws IOException {
        // TODO: Disable PrettyPrinting when done
        Gson gson = new GsonBuilder().registerTypeAdapter(QuestionGenerator.class, new SerializationAdapter()).setPrettyPrinting().create();
        QuestionGenerator selectedGenerator = Main.store.generators.getGeneratorFromName(newGameModeCombo.getValue());
        QuestionGenerator roundGenerator = new FixedGenerator(selectedGenerator, 10); // fixedgenerator to use
        String json = gson.toJson(roundGenerator);
        System.out.println(json);
        gameIDPane.setVisible(true);
        // Upload this to the JSON
        Task<Integer> roundUpload = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return Main.netConnection.startRound(json);
            }
        };

        roundUpload.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if (roundUpload.getValue() < 0) {
                    gameIDLabel.setText("Error");
                } else {
                    gameIDLabel.setText(roundUpload.getValue().toString());
                }
            }
        });

        new Thread(roundUpload).start();

        /*
        Scene scene = newGameBtn.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(roundGenerator);
        FadeTransition ft = TransitionFactory.fadeOut(dataPane);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        ft.play();
        */
    }

    @FXML private void joinGameBtnPressed() {
        // TODO
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
