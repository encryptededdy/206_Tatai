package tatai.app.util.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import tatai.app.Main;
import tatai.app.questions.generators.MathGenerator;
import tatai.app.util.factories.DialogFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that exchanges data with TataiNet over an HTTP connection
 *
 * @author Edward
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

    public void registerUser(String username, EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> onDuplicate, EventHandler<WorkerStateEvent> onError) {
        if (registered) {
            System.err.println("User already registered");
            return;
        }
        Task<String> registerTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                try {
                    String result = Request.Post(host+"createUser.php")
                            .bodyForm(Form.form().add("username", username)
                                    .build())
                            .execute()
                            .returnContent().toString();
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    if (jsonObject.get("created").getAsBoolean()) {
                        System.out.println("User created");
                        String authID = jsonObject.get("authID").getAsString();
                        System.out.println("Got authID: "+authID);
                        // Actually write the data into the db
                        Main.database.insertOp("UPDATE users SET onlineName = '"+username+"', onlineAuthID = '"+authID+"' WHERE username = '"+Main.currentUser+"'");
                        registered = true;
                        onlineName = username;
                        onlineAuthID = authID;
                        return "Success";
                    } else {
                        System.err.println("Unable to create user");
                        return "Duplicate";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Failure";
                }
            }
        };
        registerTask.setOnSucceeded(event -> {
            if (registerTask.getValue().equals("Success")) {
                onSuccess.handle(event);
            } else if (registerTask.getValue().equals("Duplicate")) {
                onDuplicate.handle(event);
            } else {
                onError.handle(event);
            }
        });
        new Thread(registerTask).start();
    }

    public void uploadJSON(String json, String version, EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> onFailure) {
        Task<Boolean> uploadTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    String result = Request.Post(host+"uploadData.php")
                            .bodyForm(Form.form().add("version", version)
                                    .add("data", json)
                                    .build())
                            .execute()
                            .returnContent().toString();
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    if (jsonObject.get("saved").getAsBoolean()) {
                        System.out.println("Server uploaded OK");
                        // The server accepted the highscore, so we're good.
                        return true;
                    } else {
                        System.err.println("Server rejected upload. Status: "+jsonObject.get("status").getAsString());
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        uploadTask.setOnSucceeded(event -> {
            if (uploadTask.getValue()) {
                onSuccess.handle(event);
            } else {
                onFailure.handle(event);
            }
        });
        new Thread(uploadTask).start();
    }

    public ArrayList<MathGenerator> getGenerators() {
        try {
            String result = Request.Post(host + "getData.php")
                    .bodyForm(Form.form().add("version", "ezTatai_gen_1")
                            .build())
                    .execute()
                    .returnContent().toString();
            JsonElement dataArray = new JsonParser().parse(result).getAsJsonObject().get("data");
            Gson gson = new Gson();
            if (!dataArray.isJsonNull()) {
                MathGenerator[] generatorArray = gson.fromJson(dataArray, MathGenerator[].class);
                return new ArrayList<>(Arrays.asList(generatorArray));
            } else {
                // return an empty set
                return new ArrayList<MathGenerator>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return onlineName;
    }
}
