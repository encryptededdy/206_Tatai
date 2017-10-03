package tatai.app.util.net;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents a cell in the Leaderboard View
 */
public class LeaderboardViewCell extends ListCell<LeaderboardEntry>{

    FXMLLoader loader;

    @FXML private Label scoreLabel, usernameLabel, dateLabel, placeLabel;

    @FXML private Pane dataPane;

    // Date and time format for output
    private DateTimeFormatter dformat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone( ZoneId.systemDefault() );

    @Override
    protected void updateItem(LeaderboardEntry entry, boolean empty) {
        super.updateItem(entry, empty);

        if (empty || entry == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loadFXML();
            }
            scoreLabel.setText(entry.score);
            usernameLabel.setText(entry.username);
            dateLabel.setText(dformat.format(Instant.ofEpochSecond(entry.datetime)));
            placeLabel.setText(Integer.toString(entry.place));
            setText(null);
            setGraphic(dataPane);

        }
    }

    private void loadFXML() {
        loader = new FXMLLoader(getClass().getResource("../../resources/leaderboardcell.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
