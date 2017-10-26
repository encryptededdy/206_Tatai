package tatai.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import tatai.app.util.Layout;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * The cell for the list of usernames in the login screen
 *
 * @author Edward
 */
public class LoginScreenCell extends ListCell<String>{

    private FXMLLoader loader;

    @FXML private Pane dataPane;
    @FXML private Label playtimeLabel, usernameLabel, playtimeTextLabel, lastloginLabel;

    @Override
    protected void updateItem(String username, boolean empty) {
        super.updateItem(username, empty);

        if (empty || username == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loadFXML();
            }

            usernameLabel.setText(username);

            // Populate stats
            ResultSet ptrs = Main.database.returnOp("SELECT sum(roundlength) FROM rounds WHERE username = '"+username+"'");
            ResultSet lastrs = Main.database.returnOp("SELECT max(date) FROM sessions WHERE username = '"+username+"'");
            long lastLogin = 0;
            long playTime = 0;
            try {
                ptrs.next();
                playTime = ptrs.getLong(1);
                lastrs.next();
                lastLogin = lastrs.getLong(1);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            // Convert the login time
            if (lastLogin == 0) {
                lastloginLabel.setText("Last Login: Never");
            } else if (TimeUnit.SECONDS.toMinutes(Instant.now().getEpochSecond() - lastLogin) < 60) {
                lastloginLabel.setText("Last Login: "+TimeUnit.SECONDS.toMinutes(Instant.now().getEpochSecond() - lastLogin)+" minutes ago");
            } else if (TimeUnit.SECONDS.toHours(Instant.now().getEpochSecond() - lastLogin) < 48) {
                lastloginLabel.setText("Last Login: "+TimeUnit.SECONDS.toHours(Instant.now().getEpochSecond() - lastLogin)+" hours ago");
            } else {
                lastloginLabel.setText("Last Login: "+TimeUnit.SECONDS.toDays(Instant.now().getEpochSecond() - lastLogin)+" days ago");
            }
            // Convert playTime
            if (TimeUnit.SECONDS.toMinutes(playTime) < 60) {
                // Minutes
                playtimeTextLabel.setText("Mins played");
                playtimeLabel.setText(Long.toString(TimeUnit.SECONDS.toMinutes(playTime)));
            } else if (TimeUnit.SECONDS.toHours(playTime) < 48) {
                // Hours
                playtimeTextLabel.setText("Hours played");
                playtimeLabel.setText(Long.toString(TimeUnit.SECONDS.toHours(playTime)));
            } else {
                playtimeTextLabel.setText("Days Played");
                playtimeLabel.setText(Long.toString(TimeUnit.SECONDS.toDays(playTime)));
            }

            setText(null);
            setGraphic(dataPane);

        }
    }

    private void loadFXML() {
        loader = Layout.LOGINCELL.loader();
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
