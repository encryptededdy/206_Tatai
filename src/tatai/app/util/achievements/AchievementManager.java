package tatai.app.util.achievements;

import tatai.app.questions.generators.GeneratorManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class AchievementManager {
    private LinkedHashMap<String, Achievement> achievements;

    /**
     * Populates the achievements based on the available built in generators and a list of hardcoded ones
     * @param generators
     */
    public AchievementManager(GeneratorManager generators) {
        achievements = new LinkedHashMap<String, Achievement>();
        achievements.put("Bargain Hunter", new NormalAchievement("Bargain Hunter", "Purchase an item from the store.",
                200, 0, "CREDIT_CARD_ALT", "#FFFFFF", "Purchase an item from the store."));
        achievements.put("Shopaholic", new NormalAchievement("Shopaholic","Purchase everything in the store.",
                10000, 0, "CREDIT_CARD_ALT", "#c98910", "Purchase everything in the store."));

        achievements.put("Modder", new NormalAchievement("Modder", "Create your own custom question set.",
                300, 0, "PENCIL", "#FFFFFF", "Create your own custom question set."));
        achievements.put("Roots In The Community", new NormalAchievement("Roots In The Community", "Upload your own custom question generator to the online community.",
                300, 0, "CLOUD_UPLOAD", "#FFFFFF", "Upload your own custom question generator to the online community."));
        achievements.put("Bootleg Questions", new NormalAchievement("Bootleg Questions", "Download a custom question generator from the online community.",
                300, 0, "CLOUD_DOWNLOAD", "#FFFFFF", "Download a custom question generator from the online community."));

        achievements.put("Off To A Good Start", new NormalAchievement("Off To A Good Start", "Complete a round of any game type.",
                100, 0, "THUMBS_UP", "#FFFFFF", "Complete a round of any game type."));
        achievements.put("Look At You Go!", new NormalAchievement("Look At You Go!", "Complete a round of any game type with 100% accuracy and a streak of 10.",
                500, 0, "THUMBS_UP", "#c98910", "Complete a round of any game type with 100% accuracy and a streak of 10."));

        achievements.put("Hello World!", new NormalAchievement("Hello World!", "Create an online account on TataiNet for challenging other players.",
                100, 0, "USERS", "#FFFFFF", "Create an online account on TataiNet for challenging other players."));

        achievements.put("Piggy Bank", new NormalAchievement("Piggy Bank", "Get more than 1000 of in game currency.",
                100, 0, "BTC", "#FC8B9F", "Get more than 1000 of in game currency."));
        achievements.put("Savings Account", new NormalAchievement("Savings Account", "Get more than 5000 of in game currency.",
                500, 0, "BTC", "#965a38", "Get more than 5000 of in game currency."));
        achievements.put("Ballin'", new NormalAchievement("Ballin'", "Get more than 15000 of in game currency.",
                1500, 0, "BTC", "#a8a8a8", "Get more than 15000 of in game currency."));
        achievements.put("You Know This Isn't Real Money Right?", new NormalAchievement("You Know This Isn't Real Money Right?", "Get more than 50000 of in game currency.",
                5000, 0, "BTC", "#c98910", "Get more than 50000 of in game currency."));

        for (int i = 0; i < generators.getGenerators().size(); i++) {
            if (!generators.getGenerators().get(i).isCustom()) {
                String name = generators.getGenerators().get(i).getGeneratorName();
                String bronzeName = name + " - Bronze";
                String silverName = name + " - Silver";
                String goldName = name + " - Gold";
                achievements.put(bronzeName, new TrophyAchievement(name, "Bronze",
                        "Answered 15 correct questions for the " + name + " questions.", TrophyAchievement.bronzeReward, TrophyAchievement.bronzeColor));
                achievements.put(silverName, new TrophyAchievement(name, "Silver",
                        "Answered 30 correct questions for the " + name + " questions.", TrophyAchievement.silverReward, TrophyAchievement.silverColor));
                achievements.put(goldName, new TrophyAchievement(name, "Gold",
                        "Answered 60 correct questions for the " + name + " questions.", TrophyAchievement.goldReward, TrophyAchievement.goldColor));
            }
        }
    }

    /**
     * returns a LinkedHashMap of achievements so that an order is preserved but also so that an achievement can be fetched simply using it's name
     * @return
     */
    public LinkedHashMap<String, Achievement> getAchievements() {return achievements;}

    /**
     * returns the number of achievements in total
     * @return
     */
    public int numberOfAchievements() {return achievements.size(); }

    /**
     * returns the number of achievements that are completed
     * @return
     */
    public int numberOfCompletedAchievements() {
        int count = 0;
        for (Achievement achievement : getAchievementsArrayList()) {
            if (achievement.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    /**
     * returns an arraylist of achievements to have some kind of order. pretty sure this is deprecated but I'm not going
     * to delete it since it might be used somewhere.
     * @return
     */
    public ArrayList<Achievement> getAchievementsArrayList() {
        ArrayList<Achievement> arrayList = new ArrayList<Achievement>();
        Set<String> keys = achievements.keySet();
        for (String s : keys) {
            arrayList.add(achievements.get(s));
        }
        return arrayList;
    }
}
