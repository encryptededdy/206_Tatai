package tatai.app.util.Achievements;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.layout.Pane;
import tatai.app.Main;
import tatai.app.util.DisplaysAchievements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class Achievement {
    private String _name;
    private String _description;
    private int _reward;
    private int _completed;
    private FontAwesomeIcon _icon;
    private String _completionMessage;

    public Achievement(String name, String description, int reward, int completed, String iconName, String completionMessage) {
        _name = name;
        _description = description;
        _reward = reward;
        _completed = completed;
        _icon = FontAwesomeIcon.valueOf(iconName);
        _completionMessage = completionMessage;
        try {
            if (inDB()) {
                updateObj();
            } else {
                updateDB();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public Achievement(String name, String description) {
        this(name, description, 0, 0, "TROPHY", description);
    }

    public boolean isCompleted() {
        return _completed == 1;
    }

    private void updateDB() {
        PreparedStatement ps = Main.database.getPreparedStatement("INSERT INTO achievements (name, username, description, completed, date, reward, iconname, message)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            ps.setString(1, _name);
            ps.setString(2, Main.currentUser);
            ps.setString(3, _description);
            ps.setInt(4, _completed);
            ps.setLong(5, Instant.now().getEpochSecond());
            ps.setInt(6, _reward);
            ps.setString(7, _icon.name());
            ps.setString(8, _completionMessage);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    private boolean inDB() throws SQLException {
        String SQLQuery = "SELECT COUNT(name) FROM achievements WHERE username = '" + Main.currentUser + "' AND name = '" + _name + "'";
        ResultSet rs = Main.database.returnOp(SQLQuery);
        return rs.getBoolean(1);
    }

    /**
     * Update the achievement object information as it may have changed.
     */
    private void updateObj() {
        String query = "SELECT description, completed, reward, iconname, message FROM achievements WHERE username = '" +
        Main.currentUser + "' AND name = '" + _name + "'";

        try {
            ResultSet rs = Main.database.returnOp(query);
            _description = rs.getString(1);
            _completed = rs.getInt(2);
            _reward = rs.getInt(3);
            _icon = FontAwesomeIcon.valueOf(rs.getString(4));
            _completionMessage = rs.getString(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * updates the database with the achievement being completed and displays
     * @param screen the screen controller class which implements the DisplayAchievements interface allowing for the
     *               controller class to determine how the achievementPane is animated in.
     * @param achievementPane the pane in the screen controller class which will be populated by the achievment view
     */
    public void setCompleted(DisplaysAchievements screen, Pane achievementPane) {
        _completed = 1;
        Main.store.credit(_reward);
        updateDB();

        AchievementView av = new AchievementView(_name, _icon, Integer.toString(_reward));
        achievementPane.getChildren().add(av.getNode());
        screen.animateAchievement(av);

    }
}
