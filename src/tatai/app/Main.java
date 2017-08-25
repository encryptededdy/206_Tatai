package tatai.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    static URL mainMenuLayout;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Load the FXMLs for our various layouts
        mainMenuLayout = getClass().getResource("resources/mainmenu.fxml");

        Parent root = FXMLLoader.load(mainMenuLayout);
        primaryStage.setTitle("Tatai");
        primaryStage.setResizable(false); // please don't resize
        primaryStage.sizeToScene(); // for some reason setresizable expands the window???
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
