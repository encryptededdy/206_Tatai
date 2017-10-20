package tatai.app.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tatai.app.Main;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.factories.DialogFactory;
import tatai.app.util.store.SerializationAdapter;
import tatai.app.util.store.StoreItem;
import tatai.app.util.store.StoreManager;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

/**
 * The singleton Database class maintains a connection to the SQLite database. It also handles configuration and setup
 * of the database.
 *
 * @author Edward
 */
public class Database {
    private static Database instance = null;
    private Connection connection;


    /**
     * Configures the database state
     */
    private Database() {
        openConnection();
        createTables();
    }

    /**
     * Singleton method for getting the current instance of Database
     * @return The current Database instance
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Opens the SQL connection
     */
    private void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:tataiData.sqlite");
        } catch ( Exception e ) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
    }

    /**
     * Closes the internal connection to the SQLite database
     */
    public void close() {
        try {
            connection.close();
        } catch ( Exception e ) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
    }

    /**
     * Perform a query (INSERT) where you don't need the return
     * @param query SQL query as string
     */
    public void insertOp(String query) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
    }

    /**
     * Returns a PreparedStatement for custom use
     * @param statement The prepared statement to use
     * @return The corresponding PreparedStatement object
     */
    public PreparedStatement getPreparedStatement(String statement) {
        try {
            return connection.prepareStatement(statement);
        } catch ( Exception e ) {
            e.printStackTrace();
            DialogFactory.exception("Internal Database error.", "Database Error", e);
            return null;
        }
    }

    /**
     * Performs a SQL query where you are expecting a return
     * @param query SQL query as string
     * @return returned ResultSet from the query
     */
    public ResultSet returnOp(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch ( Exception e ) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
        return null;
    }

    /**
     * Get the next ID for a particular column in a table. Essentially finding the current max in that col, plus one.
     * @param column The column to use
     * @param table The table to use
     * @return The next max value in said column/table
     */
    public int getNextID(String column, String table) {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT max("+column+") FROM "+table);
            if (rs.next()) {
                return rs.getInt(1) + 1;
            } else {
                return 1;
            }
        } catch (Exception e) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
        return 1;
    }

    /**
     * Starts the Session and writes initial data to the database
     * @return The ID of the current session
     */
    public int startSession() {
        int ID = getNextID("sessionID", "sessions");
        insertOp("INSERT INTO sessions (sessionID, username, date) VALUES ("+ID+", '"+Main.currentUser+"', "+ Instant.now().getEpochSecond()+")");
        // Checks if the user has completed a round before. If so, disable the tutorial.
        ResultSet completeOp = returnOp("SELECT COUNT(*) FROM rounds WHERE username = '"+Main.currentUser+"' AND isComplete = 1");
        try {
            completeOp.next();
            Main.showTutorial = completeOp.getInt(1) == 0;
        } catch (Exception e) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
        return ID;
    }

    /**
     * Stops the session, and writes the time into the database
     */
    public void stopSession() {
        // Record the length of the current session
        insertOp("UPDATE sessions SET sessionlength = "+Instant.now().getEpochSecond()+" - date WHERE sessionID = "+Main.currentSession);
    }

    /**
     * Creates a new user in the database. If the user already exists nothing will be created.
     * @param username The username of the new user
     */
    public void newUser(String username) {
        insertOp("INSERT OR IGNORE INTO users (username, creationdate) VALUES ('"+username+"', "+Instant.now().getEpochSecond()+")");
    }

    /**
     * Gets a list of currently registered users in the database
     * @return An ArrayList of the currently registered users
     */
    public ArrayList<String> getUsers() {
        ArrayList<String> output = new ArrayList<>();
        ResultSet rs = returnOp("SELECT username FROM users");
        try {
            while (rs.next()) {
                output.add(rs.getString(1));
            }
        } catch (Exception e) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
        return output;
    }

    public void storeStore() {
        if (Main.currentUser != null) {
            Gson gson = new GsonBuilder().registerTypeAdapter(StoreItem.class, new SerializationAdapter()).registerTypeAdapter(QuestionGenerator.class, new SerializationAdapter()).create();
            String serialized = gson.toJson(Main.store);
            PreparedStatement ps = getPreparedStatement("INSERT OR REPLACE INTO tataistore (username, json) VALUES (?, ?)");
            try {
                ps.setString(1, Main.currentUser);
                ps.setString(2, serialized);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                DialogFactory.exception("Internal Database error.", "Database Error", e);
            }
            System.out.println("Stored as JSON: " + serialized);
        } else {
            System.out.println("No login, didn't store anything.");
        }
    }

    public StoreManager getStore() {
        Gson gson = new GsonBuilder().registerTypeAdapter(StoreItem.class, new SerializationAdapter()).registerTypeAdapter(QuestionGenerator.class, new SerializationAdapter()).create();
        ResultSet rs = returnOp("SELECT json FROM tataistore WHERE username = '"+Main.currentUser+"'");
        try {
            if (rs.next()) { // If there is a store
                return gson.fromJson(rs.getString(1), StoreManager.class);
            }
        } catch ( Exception e ) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
        return null;
    }

    /**
     * Creates the tables for the database structure, if they don't already exist.
     */
    private void createTables() {
        ArrayList<String> queries = new ArrayList<>();
        // Create the users table
        queries.add("CREATE TABLE IF NOT EXISTS users " +
                "(username TEXT PRIMARY KEY     NOT NULL," +
                " creationdate       INTEGER     NOT NULL," +
                " onlineName    TEXT," +
                " onlineAuthID  TEXT)");
        // Create the questions table
        queries.add("CREATE TABLE IF NOT EXISTS questions " +
                "(questionID INTEGER PRIMARY KEY AUTOINCREMENT    NOT NULL," +
                " username      TEXT    NOT NULL, " +
                " date          INTEGER NOT NULL, " +
                " sessionID     INTEGER," +
                " roundID       INTEGER," +
                " questionSet   TEXT    NOT NULL," +
                " question      TEXT    NOT NULL," +
                " timeToAnswer  INTEGER," +
                " answer        TEXT    NOT NULL," +
                " correct       INTEGER NOT NULL," +
                " attempts      INTEGER NOT NULL)");
        // Create the rounds table
        queries.add("CREATE TABLE IF NOT EXISTS rounds " +
                "(roundID INTEGER PRIMARY KEY    NOT NULL," +
                " username      TEXT    NOT NULL, " +
                " date          INTEGER NOT NULL, " +
                " sessionID     INTEGER," +
                " questionSet   TEXT    NOT NULL," +
                " roundlength   INTEGER," +
                " noquestions   INTEGER    NOT NULL," +
                " nocorrect     INTEGER NOT NULL," +
                " score         INTEGER," +
                " isComplete    INTEGER NOT NULL)");
        // Create the sessions table
        queries.add("CREATE TABLE IF NOT EXISTS sessions " +
                "(sessionID INTEGER PRIMARY KEY    NOT NULL," +
                " username      TEXT    NOT NULL, " +
                " date          INTEGER NOT NULL, " +
                " sessionlength INTEGER)");
        // Create the tataistore table
        queries.add("CREATE TABLE IF NOT EXISTS tataistore " +
                "(username TEXT PRIMARY KEY     NOT NULL," +
                " json          TEXT     NOT NULL)");
        // Create the achievements table
        queries.add("CREATE TABLE IF NOT EXISTS achievements " +
                "(name          TEXT NOT NULL, " +
                "username       TEXT NOT NULL, " +
                "description    TEXT, " +
                "completed      INTEGER NOT NULL, " +
                "date           INTEGER," +
                "reward         INTEGER, " +
                "iconname       TEXT, " +
                "message        TEXT)");
        // Add the default user if it doesn't exist
        queries.add("INSERT OR IGNORE INTO users (username, creationdate) VALUES ('default', "+Instant.now().getEpochSecond()+")");
        try {
            Statement statement = connection.createStatement();
            for (String query : queries) {
                statement.execute(query);
            }
            statement.close();
        } catch ( Exception e ) {
            DialogFactory.exception("Unable to connect to database. Close any other instances of the application and try again.", "Database Error", e);
        }
    }
}
