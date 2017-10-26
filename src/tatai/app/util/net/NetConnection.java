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
 * A class that exchanges data with TataiNet over an HTTPS connection
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

    /**
     * Starts a challenge mode round
     * @param json JSON string of the FixedGenerator to be used
     * @return The RoundID once the round has started. -1 returned if failure
     */
    public int startRound(String json) {
        try {
            String result = Request.Post(host+"startRound.php")
                    .bodyForm(Form.form().add("authID", onlineAuthID)
                            .add("username", onlineName)
                            .add("questionset", json)
                            .build())
                    .execute()
                    .returnContent().toString();
            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
            return jsonObject.get("id").getAsInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Long poll for the challenge mode round to start. Returns once it's started
     * @param id The roundID to wait for
     * @return True once started. False if an error was encountered
     */
    public boolean waitRound(int id) {
        try {
            System.out.println("Start wait");
            String result = Request.Post(host+"waitRound.php")
                    .bodyForm(Form.form().add("authID", onlineAuthID)
                            .add("username", onlineName)
                            .add("roundID", Integer.toString(id))
                            .build())
                    .socketTimeout(90000)
                    .execute()
                    .returnContent().toString();
            System.out.println("Wait done");
            return new JsonParser().parse(result).getAsJsonObject().get("started").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finish a challenge mode round
     * @param id The roundID of the round
     * @param score The score attained in this round
     * @return a JSONObject with the other user and their score
     */
    public JsonObject finishRound(int id, int score) {
        try {
            System.out.println("Upload score; waiting for round finish");
            String result = Request.Post(host+"finishRound.php")
                    .bodyForm(Form.form().add("authID", onlineAuthID)
                            .add("username", onlineName)
                            .add("roundID", Integer.toString(id))
                            .add("score", Integer.toString(score))
                            .build())
                    .socketTimeout(90000)
                    .execute()
                    .returnContent().toString();
            System.out.println("Got result!");
            return new JsonParser().parse(result).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Joins a challenge mode round
     * @param id The roundID to attempt to join
     * @return An object with the FixedGenerator to use
     */
    public JsonObject joinRound(int id) {
        try {
            String result = Request.Post(host+"joinRound.php")
                    .bodyForm(Form.form().add("authID", onlineAuthID)
                            .add("username", onlineName)
                            .add("roundID", Integer.toString(id))
                            .build())
                    .execute()
                    .returnContent().toString();
            return new JsonParser().parse(result).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the current leaderboard for a gamemode from the server
     * @param gamemode The gamemode to get the leaderboard for
     * @return The leaderboard, as an ArrayList of LeaderboardEntries
     */
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

    /**
     * Register an user with the TataiNet server
     * @param username The username to register
     * @param onSuccess The EventHandler for if the user is created successfully
     * @param onDuplicate The EventHandler if this user is duplicate
     * @param onError The EventHandler for if there was an error creating the user
     */
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

    /**
     * Upload arbitary JSON data to the server
     * @param json The JSON data to upload
     * @param version The version data to attach to the JSON
     * @param onSuccess The EventHandler for if the JSON is uploaded successfully
     * @param onFailure The EventHandler for if there is a failure
     */
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

    /**
     * Gets a list of TataiWorkshop items
     * @return An ArrayList of MathGenerators
     */
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

    /**
     * Gets the current online username. Null if unregistered.
     */
    public String getUsername() {
        return onlineName;
    }
}
