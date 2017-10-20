package tatai.app.util.Achievements;

import tatai.app.Main;
import tatai.app.questions.generators.QuestionGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TrophyAchievement extends Achievement {
    // SCORE THRESHOLDS FOR GETTNG THE TROPHY ACHIEVEMENTS;
    private static int bronzeScore = 5;
    private static int silverScore = 10;
    private static int goldScore = 15;

    public TrophyAchievement(String name, String rank, String description, int reward, int completed, String completionMessage) {
        super(name + " - " + rank, description, reward, completed, "TROPHY", completionMessage);
    }

    public TrophyAchievement(String name, String rank, String description) {
        super(name + " - " + rank, description, 0, 0, "TROPHY", description);
    }

    public static int getBronzeScore() {
        return bronzeScore;
    }

    public static int getSilverScore() {
        return silverScore;
    }

    public static int getGoldScore() {
        return goldScore;
    }

    public static int getCorrectAnswers(QuestionGenerator qg) throws SQLException {
        String qgName = qg.getGeneratorName();
        String SQLQuery = "SELECT COUNT(questionID) FROM questions WHERE username = '" + Main.currentUser + "' AND questionSet = '" +
                qgName + "' AND correct = '1'" ;
        ResultSet rs = Main.database.returnOp(SQLQuery);
        return rs.getInt(1);
    }

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
    }
}
