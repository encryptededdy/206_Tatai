package tatai.app.util.achievements;

public class NormalAchievement extends Achievement {

    /**
     * verbose constructor for an NormalAchievment object
     * @param name
     * @param description
     * @param reward
     * @param completed
     * @param iconName
     * @param iconColor
     * @param completionMessage
     */
    public NormalAchievement(String name, String description, int reward, int completed, String iconName, String iconColor, String completionMessage) {
        super(name, description, reward, completed, iconName, iconColor, completionMessage);
    }

    /**
     * lazy constructor for an NormalAchievement object
     * @param name
     * @param description
     */
    public NormalAchievement(String name, String description) {
        super(name, description);
    }
}
