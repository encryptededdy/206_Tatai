package tatai.app.util.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.concurrent.Task;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import tatai.app.Main;
import tatai.app.util.DialogFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that exchanges data with TataiNet over an HTTP connection
 */
public class NetConnection {
    private static NetConnection instance = null;
    private static final String host = "https://zhang.nz/tatai/"; // The server where the php is hosted

    private boolean registered = false;

    private String onlineName;
    private String onlineAuthID;

    /**
     *  Constructs the Network Connection, and checks if the current user is registered
     */
    public NetConnection() {
        // Check if the current user has credentials
        ResultSet rs = Main.database.returnOp("SELECT onlineName, onlineAuthID FROM users WHERE username = '"+Main.currentUser+"' AND onlineName IS NOT NULL AND onlineAuthID IS NOT NULL");
        try {
            if (rs.next()) {
                onlineName = rs.getString(1);
                onlineAuthID = rs.getString(2);
                Task<Void> serverCheck = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (userAuthCheck()) {
                            registered = true;
                            System.out.println("User is registered: " + onlineName + ", " + onlineAuthID);
                        } else {
                            System.err.println("Server rejected auth attempt");
                        }
                        return null;
                    }
                };
                System.out.println("Checking user with server...");
                new Thread(serverCheck).start();
            } else {
                System.out.println("User has no online credentials");
            }
        } catch (SQLException e) {
            DialogFactory.exception("Internal database error. Database is inconsistent", "Database Error", e);
        }
    }

    /**
     * Checks to make sure the user exists on the server and the authID is correct
     * Note this isn't multithreaded (due to the return) and will hang the thread it is executed on
     * @return Whether the server has the user
     */
    private boolean userAuthCheck() {
        try {
            String result = Request.Post(host+"checkUser.php")
                    .bodyForm(Form.form().add("authID", onlineAuthID)
                            .add("username", onlineName)
                            .build())
                    .execute()
                    .returnContent().toString();
            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
            // The server accepted the highscore, so we're good.
            return jsonObject.get("authOK").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Uploads the specified highscore
     * @param gamemode The gamemode the highscore was generated in
     * @param score The score achieved
     */
    public void uploadScore(String gamemode, int score) {
        if (!registered) {
            System.err.println("User unregistered, cannot upload score");
            return;
        }
        Task<Void> uploadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String result = Request.Post(host+"uploadHighScore.php")
                            .bodyForm(Form.form().add("authID", onlineAuthID)
                                    .add("username", onlineName)
                                    .add("highscore", Integer.toString(score))
                                    .add("gamemode", gamemode)
                                    .build())
                            .execute()
                            .returnContent().toString();
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    if (jsonObject.get("saved").getAsBoolean()) {
                        System.out.println("Highscore uploaded to server");
                        // The server accepted the highscore, so we're good.
                    } else {
                        System.err.println("Server rejected highscore. Status: "+jsonObject.get("status").getAsString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(uploadTask).start();
    }

    public ArrayList<LeaderboardEntry> getLeaderboard(String gamemode) {
        try {
            String result = Request.Post(host + "getHighScores.php")
                    .bodyForm(Form.form().add("gamemode", gamemode)
                            .build())
                    .execute()
                    .returnContent().toString();
            JsonElement scoresArray = new JsonParser().parse(result).getAsJsonObject().get("scores");
            Gson gson = new Gson();
            if (!scoresArray.isJsonNull()) {
                LeaderboardEntry[] leaderboardArray = gson.fromJson(scoresArray, LeaderboardEntry[].class);
                return new ArrayList<>(Arrays.asList(leaderboardArray));
            } else {
                // return an empty set
                return new ArrayList<LeaderboardEntry>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
