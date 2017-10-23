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
    private String _iconColor;
    private String _completionMessage;

    public Achievement(String name, String description, int reward, int completed, String iconName, String iconColor, String completionMessage) {
        _name = name;
        _description = description;
        _reward = reward;
        _completed = completed;
        _icon = FontAwesomeIcon.valueOf(iconName);
        _iconColor = iconColor;
        _completionMessage = completionMessage;
    }

    public Achievement(String name, String description) {
        this(name, description, 0, 0, "TROPHY", "#FFFFFF", description);
    }

    public boolean isCompleted() {
        return _completed == 1;
    }

    /**
     * updates the database with the achievement being completed and displays
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

    public String getName() {
        return _name;
    }

    public FontAwesomeIcon getIcon() {
        return _icon;
    }

    public String getDescription() {return  _description; }

    public int getReward() {
        return _reward;
    }

    public Color getColor() {
        return Color.web(_iconColor);
    }
}
