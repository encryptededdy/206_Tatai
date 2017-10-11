package tatai.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tatai.app.questions.generators.*;
import tatai.app.util.Database;
import tatai.app.util.factories.DialogFactory;
import tatai.app.util.net.NetConnection;

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
    static Image background = new Image(Main.class.getResourceAsStream("resources/bkgndb1.jpg"));
    static URL mainMenuLayout;
    static URL questionLayout;
    static URL statisticsLayout;
    static URL completeLayout;
    static URL loginLayout;
    static URL settingsLayout;
    static URL dashboardLayout;
    static URL tatainetLayout;
    static URL customgeneratorLayout;
    static URL practiceLayout;
    static URL levelLayout;
    public static NetConnection netConnection;
    public static boolean showTutorial = true;
    public static int transitionDuration = 200;
    public static final boolean isWindows = System.getProperty("os.name").startsWith("Windows"); // Used to get the correct HTK command
    public static LinkedHashMap<String, QuestionGenerator> questionGenerators = new LinkedHashMap<>(); // Questions.Generators to be used
    public static Font currentFont;

    static { // Static initializer
        // Load fonts
        Font.loadFont(Main.class.getResource("resources/Roboto-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("resources/Roboto-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("resources/Roboto-Medium.ttf").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("resources/Roboto-Light.ttf").toExternalForm(), 10);
        currentFont = new Font("Roboto", 18);
        database = Database.getInstance();
    }

    /**
     * JavaFX start method
     * @param primaryStage Provided by JFX start method
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        startupCheck();
        // Load the FXMLs for our various layouts
        loginLayout = getClass().getResource("resources/loginscreen.fxml");
        questionLayout = getClass().getResource("resources/questionscreen.fxml");
        statisticsLayout = getClass().getResource("resources/statisticsscreen.fxml");
        mainMenuLayout = getClass().getResource("resources/mainmenu.fxml");
        completeLayout = getClass().getResource("resources/completescreen.fxml");
        settingsLayout = getClass().getResource("resources/settings.fxml");
        dashboardLayout = getClass().getResource("resources/statsdashboard.fxml");
        tatainetLayout = getClass().getResource("resources/tatainet.fxml");
        practiceLayout = getClass().getResource("resources/practicescreen.fxml");
        customgeneratorLayout = getClass().getResource("resources/customgenerator.fxml");
        levelLayout = getClass().getResource("resources/levelscreen.fxml");
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
        database.stopSession();
        database.close();
        System.out.println("Application closing!");
    }

    /**
     * Checks for required files on startup, and shows error if they are missing
     */
    private static void startupCheck() {
        if (!new File("HTK/MaoriNumbers").exists()) {
            RuntimeException e = new RuntimeException("Catherine HTK files missing!");
            DialogFactory.exception("Catherine HTK training Files are missing", "HTK Machine Broke", e);
            throw e;
        };
        if (!new File("HTK/MaoriNumbers/HVite.exe").exists() && isWindows) {
            RuntimeException e = new RuntimeException("Windows HTK files missing!");
            DialogFactory.exception("Windows HTK binary is missing", "HTK Machine Broke", e);
            throw e;
        };
        File tempDir = new File(".tmp");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Placeholder method to define the avaliable question generators
     */
    public static void populateGenerators() {
        // Clear existing ones
        questionGenerators.clear();
        // Hardcoded, built in ones
        questionGenerators.put("Numbers", new NumberGenerator());
        questionGenerators.put("Tens Numbers", new NumberGenerator99());
        questionGenerators.put("Easy Addition", new MathGenerator(9, 8, MathOperator.ADD, "Basic Addition"));
        questionGenerators.put("Addition", new MathGenerator(99, 99, MathOperator.ADD, "Addition"));
        questionGenerators.put("Subtraction", new MathGenerator(99, 99, MathOperator.SUBTRACT, "Subtraction"));
        questionGenerators.put("Times Tables", new MathGenerator(99, 12, MathOperator.MULTIPLY, "Times Tables", true));
        questionGenerators.put("Multiplication", new MathGenerator(99, 24, MathOperator.MULTIPLY, "Advanced Multiplication", false));
        questionGenerators.put("Division", new MathGenerator(20, 12, MathOperator.DIVIDE, "Division"));
        questionGenerators.put("Division (Maori)", new MathGenerator(20, 12, MathOperator.DIVIDE, "Division", false, true));
        // Custom ones
        database.populateGenerators();
    }
}
