package tatai.app.util.achievements;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import tatai.app.Main;
import tatai.app.util.DisplaysAchievements;

public abstract class Achievement {
    private String _name;
    private String _description;
    private int _reward;
    private int _completed;
    private FontAwesomeIcon _icon;
    private String _iconColor; // String used otherwise GSON will explode
    private String _completionMessage;

    /**
     * verbose constructor for an achievment object
     * @param name
     * @param description
     * @param reward
     * @param completed
     * @param iconName
     * @param iconColor
     * @param completionMessage
     */
    public Achievement(String name, String description, int reward, int completed, String iconName, String iconColor, String completionMessage) {
        _name = name;
        _description = description;
        _reward = reward;
        _completed = completed;
        _icon = FontAwesomeIcon.valueOf(iconName);
        _iconColor = iconColor;
        _completionMessage = completionMessage;
    }

    /**
     * lazy constructor for an achievement object
     * @param name
     * @param description
     */
    public Achievement(String name, String description) {
        this(name, description, 0, 0, "TROPHY", "#FFFFFF", description);
    }

    /**
     * reutrns whether or not the achievement is completed or not
     * @return
     */
    public boolean isCompleted() {
        return _completed == 1;
    }

    /**
     * Sets the achievement to complete and displays it in the appropriate manner
     * @param screen the screen controller class which implements the DisplayAchievements interface allowing for the
     *               controller class to determine how the achievementPane is animated in.
     * @param achievementPane the pane in the screen controller class which will be populated by the achievment view
     */
    public void setCompleted(DisplaysAchievements screen, Pane achievementPane) {
        _completed = 1;
        Main.store.credit(_reward, screen, achievementPane);

        AchievementView av = new AchievementView(_name, _icon, Integer.toString(_reward));
        achievementPane.getChildren().add(av.getNode());
        screen.animateAchievement(av);
    }

    /**
     * gets the name of the achievment for display purposes
     * @return
     */
    public String getName() {
        return _name;
    }

    /**
     * gets the icon of the achievement for display purposes
     * @return
     */
    public FontAwesomeIcon getIcon() {
        return _icon;
    }

    /**
     * gets the description of the achievement for display purposes
     * @return
     */
    public String getDescription() {return  _description; }

    /**
     * gets the reward of the achievement for display and crediting purposes
     * @return
     */
    public int getReward() {
        return _reward;
    }

    /**
     * gets the color of the icon meant to be used for the achievement
     * @return
     */
    public Color getColor() {
        return Color.web(_iconColor);
    }
}
