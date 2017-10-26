package tatai.app.util.achievements;

import tatai.app.Main;
import tatai.app.questions.generators.QuestionGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TrophyAchievement extends Achievement {
    // SCORE THRESHOLDS FOR GETTNG THE TROPHY ACHIEVEMENTS;
    public final static int bronzeScore = 15;
    public final static int silverScore = 30;
    public final static int goldScore = 60;

    // REWARDS FOR TROPHY ACHIEVEMENTS
    public final static int bronzeReward = 300;
    public final static int silverReward = 400;
    public final static int goldReward = 500;

    // ICON COLORS FOR TROPHY ACHIEVEMENTS
    public final static String bronzeColor = "#965a38";
    public final static String silverColor = "#a8a8a8";
    public final static String goldColor = "#c98910";

    private String _rank;

    /**
     * Verbose constructor for trophy achievements
     * @param name
     * @param rank
     * @param description
     * @param reward
     * @param completed
     * @param iconColor
     * @param completionMessage
     */
    public TrophyAchievement(String name, String rank, String description, int reward, int completed, String iconColor, String completionMessage) {
        super(name + " - " + rank, description, reward, completed, "TROPHY", iconColor, completionMessage);
        _rank = rank;
    }

    /**
     * Lazy constructor for trophy achievements
     * @param name
     * @param rank
     * @param description
     * @param reward
     * @param iconColor
     */
    public TrophyAchievement(String name, String rank, String description, int reward, String iconColor) {
        super(name + " - " + rank, description, reward, 0, "TROPHY", iconColor, description);
        _rank = rank;
    }

    /**
     * returns the number of times a question has been answered correctly in a specific question generator by the current
     * user
     * @param qg
     * @return
     * @throws SQLException
     */
    public static int getCorrectAnswers(QuestionGenerator qg) throws SQLException {
        String qgName = qg.getGeneratorName();
        String SQLQuery = "SELECT COUNT(questionID) FROM questions WHERE username = '" + Main.currentUser + "' AND questionSet = '" +
                qgName + "' AND correct = '1'" ;
        ResultSet rs = Main.database.returnOp(SQLQuery);
        return rs.getInt(1);
    }

    /**
     * returns the rank of the trophy achievement
     * @return
     */
    public String getRank() { return _rank; }
}
