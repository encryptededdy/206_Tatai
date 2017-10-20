package tatai.app.util.achievements;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import tatai.app.util.Layout;

import java.io.IOException;

/**
 * Creates achievement objects (JFX Nodes) for display on screen
 *
 * @author Edward
 */
public class AchievementView {
    @FXML private Pane mainPane;
    @FXML private Label text, bigtext;
    @FXML private FontAwesomeIconView mainIcon, coinIcon;
    @FXML private Label currencyText;

    private FXMLLoader loader;

    /**
     * Constructor for an Achievement with a currency value
     * @param mainText The main text of the achievement
     * @param icon The icon to use
     * @param value The currency earned by the achievement
     */
    public AchievementView(String mainText, FontAwesomeIcon icon, String value) {
        if (loader == null) loadFXML();
        mainIcon.setIcon(icon);
        // Hide the big text
        bigtext.setVisible(false);
        // Show the other elements
        text.setVisible(true);
        currencyText.setVisible(true);
        coinIcon.setVisible(true);
        // populate
        text.setText(mainText);
        currencyText.setText(value);
    }

    /**
     * Constructor for an Achievement with no currency value
     * @param mainText The main text of the achievement
     * @param icon The icon to use
     */
    public AchievementView(String mainText, FontAwesomeIcon icon) {
        if (loader == null) loadFXML();
        mainIcon.setIcon(icon);
        // Hide the big text
        bigtext.setVisible(true);
        // Show the other elements
        text.setVisible(false);
        currencyText.setVisible(false);
        coinIcon.setVisible(false);
        // populate
        bigtext.setText(mainText);
    }

    private void loadFXML() {
        loader = Layout.ACHIEVEMENT.loader();
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pane getNode() {
        return mainPane;
    }
}
