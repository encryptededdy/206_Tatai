package tatai.app.util.achievements;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.paint.Color;
import tatai.app.questions.generators.GeneratorManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Set;

public class AchievementManager {
    private LinkedHashMap<String, Achievement> achievements;

    public AchievementManager(GeneratorManager generators) {
        achievements = new LinkedHashMap<String, Achievement>();

        achievements.put("Bargain Hunter", new Achievement("Bargain Hunter", "Purchase an item from the store.",
                200, 0, "CREDIT_CARD_ALT", Color.WHITE, "Purchase an item from the store."));
        achievements.put("Shopaholic", new Achievement("Shopaholic","Purchase everything in the store.",
                10000, 0, "CREDIT_CARD_ALT", Color.web("#c98910"), "Purchase everything in the store."));

        achievements.put("Modder", new Achievement("Modder", "Create your own custom question set.",
                300, 0, "PENCIL", Color.WHITE, "Create your own custom question set."));
        achievements.put("Roots In The Community", new Achievement("Roots In The Community", "Upload your own custom question generator to the online community.",
                300, 0, "CLOUD_UPLOAD", Color.WHITE, "Upload your own custom question generator to the online community."));
        achievements.put("Bootleg Questions", new Achievement("Bootleg Questions", "Download a custom question generator from the online community.",
                300, 0, "CLOUD_DOWNLOAD", Color.WHITE, "Download a custom question generator from the online community."));

        achievements.put("Off To A Good Start", new Achievement("Off To A Good Start", "Complete a round of any game type.",
                100, 0, "THUMBS_UP", Color.WHITE, "Complete a round of any game type."));
        achievements.put("Look At You Go!", new Achievement("Look At You Go!", "Complete a round of any game type with 100% accuracy and a streak of 10.",
                500, 0, "THUMBS_UP", Color.web("#c98910"), "Complete a round of any game type with 100% accuracy and a streak of 10."));

        achievements.put("Hello World!", new Achievement("Hello World!", "Create an online account on TataiNet for challenging other players.",
                100, 0, "USERS", Color.WHITE, "Create an online account on TataiNet for challenging other players."));

        achievements.put("Piggy Bank", new Achievement("Piggy Bank", "Get more than 1000 of in game currency.",
                100, 0, "BTC", Color.web("#FC8B9F"), "Get more than 1000 of in game currency."));
        achievements.put("Savings Account", new Achievement("Savings Account", "Get more than 5000 of in game currency.",
                500, 0, "BTC", Color.web("#965a38"), "Get more than 5000 of in game currency."));
        achievements.put("Ballin''", new Achievement("Ballin''", "Get more than 15000 of in game currency.",
                1500, 0, "BTC", Color.web("#a8a8a8"), "Get more than 15000 of in game currency."));
        achievements.put("You Know This Isn''t Real Money Right?", new Achievement("You Know This Isn''t Real Money Right?", "Get more than 50000 of in game currency.",
                5000, 0, "BTC", Color.web("#c98910"), "Get more than 50000 of in game currency."));

        for (int i = 0; i < generators.getGenerators().size(); i++) {
            if (!generators.getGenerators().get(i).isCustom()) {
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
