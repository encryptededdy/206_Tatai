package tatai.app.util;

import javafx.fxml.FXMLLoader;

import java.net.URL;

/**
 * Gets loaders for various FXML files to load
 *
 * @author Edward
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
    PRACTICECELL("tatai/app/resources/practicemodecell.fxml"),
    LEVEL("tatai/app/resources/levelscreen.fxml"),
    LEVELPANE("tatai/app/resources/levelpane.fxml"),
    LOGINCELL("tatai/app/resources/loginusercell.fxml"),
    LEADERBOARDCELL("tatai/app/resources/leaderboardcell.fxml"),
    STORE("tatai/app/resources/store.fxml"),
    STORECELL("tatai/app/resources/storecell.fxml"),
    ACHIEVEMENT("tatai/app/resources/achievementpopup.fxml"),
    CUSTOMLEVELPANE("tatai/app/resources/customlevelspane.fxml"),
    ACHIEVEMENTSCREEN("tatai/app/resources/achievementscreen.fxml"),
    ACHIEVEMENTCELL("tatai/app/resources/achievementcell.fxml"),
    BALANCESTUB("tatai/app/resources/balancestub.fxml");

    private URL url;

    Layout(String url) {
        this.url = getClass().getClassLoader().getResource(url);
    }

    /**
     * Get the FXML Loader for this layout
     * @return Loader for the layout
     */
    public FXMLLoader loader() {
        return new FXMLLoader(url);
    }
}
