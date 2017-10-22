package tatai.app.util.achievements;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
    protected FontAwesomeIcon _icon;
    public Color _iconColor;
    private String _completionMessage;

    public Achievement(String name, String description, int reward, int completed, String iconName, Color iconColor, String completionMessage) {
        _name = name;
        _description = description;
        _reward = reward;
        _completed = completed;
        _icon = FontAwesomeIcon.valueOf(iconName);
        _iconColor = iconColor;
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
        this(name, description, 0, 0, "TROPHY", Color.WHITE, description);
    }

    public boolean isCompleted() {
        return _completed == 1;
    }

    private void updateDB() {
        PreparedStatement ps1 = Main.database.getPreparedStatement("INSERT OR IGNORE INTO achievements VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        try {
            ps1.setString(1, _name);
            ps1.setString(2, Main.currentUser);
            ps1.setString(3, _description);
            ps1.setInt(4, _completed);
            ps1.setLong(5, Instant.now().getEpochSecond());
            ps1.setInt(6, _reward);
            ps1.setString(7, _icon.name());
            ps1.setString(8, _completionMessage);

            ps1.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        PreparedStatement ps2 = Main.database.getPreparedStatement("UPDATE achievements SET completed = ?, date = ? WHERE name LIKE ? AND username LIKE ?");
        try {
            ps2.setInt(1, _completed);
            ps2.setLong(2, Instant.now().getEpochSecond());

            ps2.setString(3, _name);
            ps2.setString(4, Main.currentUser);
            ps2.executeUpdate();
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

    public String getName() {
        return _name;
    }

    public FontAwesomeIcon getIcon() {
        return _icon;
    }

    public String getDescription() {return  _description; }

    public int getReward() {
        return _reward;
    }

    public Color getColor() {
        return _iconColor;
    }
}
