package tatai.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tatai.app.questions.generators.NumberGenerator;
import tatai.app.questions.generators.NumberGenerator99;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Database;

import javax.xml.crypto.Data;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;

/**
 * Entry point to the application. Loads resources (fonts, fxml) into fields
 * Has fields for constants too (transition lengths, question generators)
 *
 * @author Edward
 */

public class Main extends Application {

    public static Database database;
    public static String currentUser;
    public static int currentSession;
    static URL mainMenuLayout;
    static URL questionLayout;
    static URL statisticsLayout;
    static URL completeLayout;
    static URL loginLayout;
    public static boolean showTutorial = true; //TODO: Change this to be optional
    final public static int transitionDuration = 300;
    final static LinkedHashMap<String, QuestionGenerator> questionGenerators = new LinkedHashMap<>(); // Questions.Generators to be used

    static { // Static initializer
        // Load fonts
        Font.loadFont(Main.class.getResource("resources/Roboto-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("resources/Roboto-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("resources/Roboto-Medium.ttf").toExternalForm(), 10);
        database = Database.getInstance();
    }

    /**
     * JavaFX start method
     * @param primaryStage Provided by JFX start method
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // Load the FXMLs for our various layouts
        loginLayout = getClass().getResource("resources/loginscreen.fxml");
        questionLayout = getClass().getResource("resources/questionscreen.fxml");
        statisticsLayout = getClass().getResource("resources/statisticsscreen.fxml");
        mainMenuLayout = getClass().getResource("resources/mainmenu.fxml");
        completeLayout = getClass().getResource("resources/completescreen.fxml");
        Parent root = FXMLLoader.load(loginLayout);
        primaryStage.setTitle("Tatai");
        primaryStage.setResizable(false); // please don't resize
        primaryStage.sizeToScene(); // for some reason setresizable expands the window???
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> onClose());
        primaryStage.show();
        // Start the session
    }

    static void onClose() {
        //TODO: Add cleanup code here to cleanup
        database.stopSession();
        database.close();
        System.out.println("Application closing!");
    }

    private static void startupCheck() {
        //TODO: Have a nicer error dialog or whatever
        if (!new File("HTK/MaoriNumbers").exists()) {
            throw new RuntimeException("HTK files missing!");
        };
        File tempDir = new File(".tmp");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
    }


    public static void main(String[] args) {
        populateGenerators();
        startupCheck();
        launch(args);
    }

    private static void populateGenerators() {
        // define generators to be used
        questionGenerators.put("Numbers", new NumberGenerator());
        questionGenerators.put("Tens Numbers", new NumberGenerator99());
    }
}
