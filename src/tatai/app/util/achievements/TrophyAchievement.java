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

    public final static int bronzeReward = 300;
    public final static int silverReward = 400;
    public final static int goldReward = 500;

    public final static String bronzeColor = "#965a38";
    public final static String silverColor = "#a8a8a8";
    public final static String goldColor = "#c98910";

    private String _rank;

    public TrophyAchievement(String name, String rank, String description, int reward, int completed, String iconColor, String completionMessage) {
        super(name + " - " + rank, description, reward, completed, "TROPHY", iconColor, completionMessage);
        _rank = rank;
    }

    public TrophyAchievement(String name, String rank, String description, int reward, String iconColor) {
        super(name + " - " + rank, description, reward, 0, "TROPHY", iconColor, description);
        _rank = rank;
    }

    public static int getCorrectAnswers(QuestionGenerator qg) throws SQLException {
        String qgName = qg.getGeneratorName();
        String SQLQuery = "SELECT COUNT(questionID) FROM questions WHERE username = '" + Main.currentUser + "' AND questionSet = '" +
                qgName + "' AND correct = '1'" ;
        ResultSet rs = Main.database.returnOp(SQLQuery);
        return rs.getInt(1);
    }

    public String getRank() { return _rank; }
}
