package tatai.app.util.achievements;

import tatai.app.questions.generators.GeneratorManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Set;

public class AchievementManager {
    private LinkedHashMap<String, Achievement> achievements;

    public AchievementManager(GeneratorManager generators) {
        achievements = new LinkedHashMap<String, Achievement>();
        for (int i = 0; i < generators.getGenerators().size(); i++) {
            String name = generators.getGenerators().get(i).getGeneratorName();
            String bronzeName = name + " - Bronze";
            String silverName = name + " - Silver";
            String goldName = name + " - Gold";
            achievements.put(bronzeName, new TrophyAchievement(name, "Bronze",
                    "Answered 15 correct questions for the " + name + " questions.", TrophyAchievement.getBronzeReward(), TrophyAchievement.getBronzeColor()));
            achievements.put(silverName, new TrophyAchievement(name, "Silver",
                    "Answered 30 correct questions for the " + name + " questions.", TrophyAchievement.getSilverReward(), TrophyAchievement.getSilverColor()));
            achievements.put(goldName, new TrophyAchievement(name, "Gold",
                    "Answered 60 correct questions for the " + name + " questions.", TrophyAchievement.getGoldReward(), TrophyAchievement.getGoldColor()));
        }
    }

    public LinkedHashMap<String, Achievement> getAchievements() {return achievements;}

    public int numberOfAchievements() {return achievements.size(); }

    public int numberOfCompletedAchievements() {
        int count = 0;
        for (Achievement achievement : getAchievementsArrayList()) {
            if (achievement.isCompleted()) {
                count++;
            }
        }
        return count;
    }


    public ArrayList<Achievement> getAchievementsArrayList() {
        ArrayList<Achievement> arrayList = new ArrayList<Achievement>();
        Set<String> keys = achievements.keySet();
        for (String s : keys) {
            arrayList.add(achievements.get(s));
        }
        return arrayList;
    }
}
