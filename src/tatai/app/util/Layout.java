package tatai.app.util;

import javafx.fxml.FXMLLoader;

import java.net.URL;

/**
 * Gets loaders for various FXML files to load
 */
public enum Layout {

    LOGIN("tatai/app/resources/loginscreen.fxml"),
    QUESTION("tatai/app/resources/questionscreen.fxml"),
    STATISTICS("tatai/app/resources/statisticsscreen.fxml"),
    MAINMENU("tatai/app/resources/mainmenu.fxml"),
    COMPLETE("tatai/app/resources/completescreen.fxml"),
    SETTINGS("tatai/app/resources/settings.fxml"),
    DASHBOARD("tatai/app/resources/statsdashboard.fxml"),
    TATAINET("tatai/app/resources/tatainet.fxml"),
    PRACTICE("tatai/app/resources/practicescreen.fxml"),
    CUSTOMGENERATOR("tatai/app/resources/customgenerator.fxml"),
    LEVEL("tatai/app/resources/levelscreen.fxml"),
    LEVELPANE("tatai/app/resources/levelpane.fxml");

    private URL url;

    Layout(String url) {
        this.url = getClass().getClassLoader().getResource(url);
    }

    public FXMLLoader loader() {
        return new FXMLLoader(url);
    }
}
