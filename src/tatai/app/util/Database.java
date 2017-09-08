package tatai.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;

/**
 * The singleton Database class maintains a connection to the SQLite database. It also handles configuration and setup
 * of the database.
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

    private void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:tataiData.sqlite");
        } catch ( Exception e ) {
            System.out.println("Oh no");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    /**
     * Closes the internal connection to the SQLite database
     */
    public void close() {
        try {
            connection.close();
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return 1;
    }

    private void createTables() {
        ArrayList<String> queries = new ArrayList<>();
        // Create the users table
        queries.add("CREATE TABLE IF NOT EXISTS users " +
                "(username TEXT PRIMARY KEY     NOT NULL," +
                " creationdate       INTEGER     NOT NULL, " +
                " playtime        INTEGER)");
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
                " isComplete    INTEGER NOT NULL)");
        // Create the sessions table
        queries.add("CREATE TABLE IF NOT EXISTS sessions " +
                "(sessionID INTEGER PRIMARY KEY    NOT NULL," +
                " username      TEXT    NOT NULL, " +
                " date          INTEGER NOT NULL, " +
                " sessionlength INTEGER)");
        // Add the default user if it doesn't exist
        queries.add("INSERT OR IGNORE INTO users (username, creationdate) VALUES ('default', "+Instant.now().getEpochSecond()+")");
        try {
            Statement statement = connection.createStatement();
            for (String query : queries) {
                statement.execute(query);
            }
            statement.close();
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
