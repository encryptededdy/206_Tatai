package tatai.app.util.achievements;

import javafx.scene.paint.Color;
import tatai.app.Main;
import tatai.app.questions.generators.QuestionGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TrophyAchievement extends Achievement {
    // SCORE THRESHOLDS FOR GETTNG THE TROPHY ACHIEVEMENTS;
    private static int bronzeScore = 15;
    private static int silverScore = 30;
    private static int goldScore = 60;

    private static int bronzeReward = 300;
    private static int silverReward = 400;
    private static int goldReward = 500;

    private static Color bronzeColor = Color.web("#965a38");
    private static Color silverColor = Color.web("#a8a8a8");
    private static Color goldColor = Color.web("#c98910");

    private String _rank;

    public TrophyAchievement(String name, String rank, String description, int reward, int completed, Color iconColor, String completionMessage) {
        super(name + " - " + rank, description, reward, completed, "TROPHY", iconColor, completionMessage);
        _rank = rank;
    }

    public TrophyAchievement(String name, String rank, String description, int reward, Color iconColor) {
        super(name + " - " + rank, description, reward, 0, "TROPHY", iconColor, description);
        _rank = rank;
    }

    public static int getBronzeScore() {
        return bronzeScore;
    }
    public static int getBronzeReward() { return  bronzeReward; }
    public static Color getBronzeColor() { return bronzeColor; }

    public static int getSilverScore() {
        return silverScore;
    }
    public static int getSilverReward() { return silverReward; }
    public static Color getSilverColor() { return  silverColor; }

    public static int getGoldScore() {
        return goldScore;
    }
    public static int getGoldReward() { return goldReward; }
    public static Color getGoldColor() { return goldColor; }

    public static int getCorrectAnswers(QuestionGenerator qg) throws SQLException {
        String qgName = qg.getGeneratorName();
        String SQLQuery = "SELECT COUNT(questionID) FROM questions WHERE username = '" + Main.currentUser + "' AND questionSet = '" +
                qgName + "' AND correct = '1'" ;
        ResultSet rs = Main.database.returnOp(SQLQuery);
        return rs.getInt(1);
    }

    public String getRank() { return _rank; }

    /*
    public static boolean isBronzeInDB(QuestionGenerator qg) throws SQLException {
        String qgName = qg.getGeneratorName();
        String achievementName = qgName + " - Bronze";
        String SQLQuery = "SELECT completed FROM achievements WHERE username = '" + Main.currentUser + "' AND name = '" +
                achievementName + "'";
        ResultSet rs = Main.database.returnOp(SQLQuery);
        System.out.println(rs.getInt(1));
        return rs.getInt(1) == 1;
    }

    public static boolean isSilverInDB(QuestionGenerator qg) throws SQLException {
        String qgName = qg.getGeneratorName();
        String achievementName = qgName + " - Silver";
        String SQLQuery = "SELECT completed FROM achievements WHERE username = '" + Main.currentUser + "' AND name = '" +
                achievementName + "'";
        ResultSet rs = Main.database.returnOp(SQLQuery);
        System.out.println(rs.getInt(1));
        return rs.getInt(1) == 1;
    }

    public static boolean isGoldInDB(QuestionGenerator qg) throws SQLException {
        String qgName = qg.getGeneratorName();
        String achievementName = qgName + " - Gold";
        String SQLQuery = "SELECT completed FROM achievements WHERE username = '" + Main.currentUser + "' AND name = '" +
                achievementName + "'";
        ResultSet rs = Main.database.returnOp(SQLQuery);
        System.out.println(rs.getInt(1));
        return rs.getInt(1) == 1;
    } */
}
