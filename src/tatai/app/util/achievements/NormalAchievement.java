package tatai.app.util.achievements;

public class NormalAchievement extends Achievement {

    public NormalAchievement(String name, String description, int reward, int completed, String iconName, String iconColor, String completionMessage) {
        super(name, description, reward, completed, iconName, iconColor, completionMessage);
    }

    public NormalAchievement(String name, String description) {
        super(name, description);
    }
}
