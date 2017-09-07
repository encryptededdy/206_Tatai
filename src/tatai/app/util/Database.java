package tatai.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
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
    private Database() {
        openConnection();
        createTables();
    }

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

    private void createTables() {
        ArrayList<String> queries = new ArrayList<>();
        // Create the users table
        queries.add("CREATE TABLE IF NOT EXISTS users " +
                "(username TEXT PRIMARY KEY     NOT NULL," +
                " creationdate       INTEGER     NOT NULL, " +
                " playtime        INTEGER)");
        // Create the questions table
        queries.add("CREATE TABLE IF NOT EXISTS questions " +
                "(questionID INT PRIMARY KEY    NOT NULL," +
                " username      TEXT    NOT NULL, " +
                " date          INTEGER NOT NULL, " +
                " sessionID     INTEGER," +
                " roundID       INTEGER," +
                " questionSet   TEXT    NOT NULL," +
                " question      TEXT    NOT NULL," +
                " timeToAnswer  INTEGER," +
                " answer        TEXT    NOT NULL," +
                " correct       INTEGER NOT NULL," +
                " attempts      INTEGER)");
        // Create the rounds table
        queries.add("CREATE TABLE IF NOT EXISTS rounds " +
                "(roundID INT PRIMARY KEY    NOT NULL," +
                " username      TEXT    NOT NULL, " +
                " date          INTEGER NOT NULL, " +
                " sessionID     INTEGER," +
                " questionSet   TEXT    NOT NULL," +
                " roundlength   INTEGER," +
                " noquestions   INTEGER    NOT NULL," +
                " nocorrect     INTEGER NOT NULL)");
        // Create the sessions table
        queries.add("CREATE TABLE IF NOT EXISTS session " +
                "(sessionID INT PRIMARY KEY    NOT NULL," +
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
