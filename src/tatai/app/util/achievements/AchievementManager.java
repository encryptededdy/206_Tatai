package tatai.app.util.achievements;

import tatai.app.questions.generators.GeneratorManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class AchievementManager {
    private Hashtable<String, Achievement> achievements;

    public AchievementManager(GeneratorManager generators) {
        achievements = new Hashtable<String, Achievement>();
        for (int i = 0; i < generators.getGenerators().size(); i++) {
            String name = generators.getGenerators().get(i).getGeneratorName();
            String bronzeName = name + " - Bronze";
            String silverName = name + " - Silver";
            String goldName = name + " - Gold";
            achievements.put(bronzeName, new TrophyAchievement(name, "Bronze", "yay bronze achieved!", TrophyAchievement.getBronzeReward()));
            achievements.put(silverName, new TrophyAchievement(name, "Silver", "yay silver achieved!", TrophyAchievement.getSilverReward()));
            achievements.put(goldName, new TrophyAchievement(name, "Gold", "yay gold achieved!", TrophyAchievement.getGoldReward()));
        }
    }

    public Hashtable<String, Achievement> getAchievements() {return achievements;}


    public ArrayList<Achievement> getAchievementsArrayList() {
        ArrayList<Achievement> arrayList = new ArrayList<Achievement>();
        Set<String> keys = achievements.keySet();
        for (String s : keys) {
            arrayList.add(achievements.get(s));
        }
        return arrayList;
    }
}
