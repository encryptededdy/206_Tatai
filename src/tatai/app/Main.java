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
import java.net.URL;
import java.util.LinkedHashMap;

/**
 * Entry point to the application. Loads resources (fonts, fxml) into fields
 * Has fields for constants too (transition lengths, question generators)
 *
 * @author Edward
 */

public class Main extends Application {

    static Database database;
    static URL mainMenuLayout;
    static URL questionLayout;
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
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // Load the FXMLs for our various layouts
        questionLayout = getClass().getResource("resources/questionscreen.fxml");
        mainMenuLayout = getClass().getResource("resources/mainmenu.fxml");
        Parent root = FXMLLoader.load(mainMenuLayout);
        primaryStage.setTitle("Tatai");
        primaryStage.setResizable(false); // please don't resize
        primaryStage.sizeToScene(); // for some reason setresizable expands the window???
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        populateGenerators();
        launch(args);
    }

    private static void populateGenerators() {
        // define generators to be used
        questionGenerators.put("Numbers", new NumberGenerator());
        questionGenerators.put("Tens Numbers", new NumberGenerator99());
    }
}
