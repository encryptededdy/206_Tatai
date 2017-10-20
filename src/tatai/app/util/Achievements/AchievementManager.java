package tatai.app.util.Achievements;

import tatai.app.Main;
import tatai.app.questions.generators.GeneratorManager;

import java.util.ArrayList;
import java.util.Hashtable;

public class AchievementManager {
    private Hashtable<String, Achievement> achievements;

    public AchievementManager(GeneratorManager generators) {
        achievements = new Hashtable<String, Achievement>();
        for (int i = 0; i < generators.getGenerators().size(); i++) {
            String name = generators.getGenerators().get(i).getGeneratorName();
            String bronzeName = name + " - Bronze";
            String silverName = name + " - Silver";
            String goldName = name + " - Gold";
            achievements.put(bronzeName, new TrophyAchievement(name, "Bronze", "yay bronze achieved!"));
            achievements.put(silverName, new TrophyAchievement(name, "Silver", "yay silver achieved!"));
            achievements.put(goldName, new TrophyAchievement(name, "Gold", "yay gold achieved!"));
        }
    }

    public Hashtable<String, Achievement> getAchievements() {return achievements;};
}
