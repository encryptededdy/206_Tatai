package tatai.app.questions;

import tatai.app.Main;
import tatai.app.questions.generators.QuestionGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

/**
 *  Represents a game Round. Stores a list of questions.
 *
 *  @author Edward
 */
public class Round {
    private ArrayList<Question> _questions = new ArrayList<>();
    private int _currentQuestion = -1;
    private int _roundID;
    private long _startTime;
    private QuestionGenerator _roundQuestionGenerator;
    private int _numQuestions;
    private Integer _score;

    /**
     * Constructs a Round
     * @param generator The QuestionGenerator to be used to generate the questions
     * @param numQuestions Number of questions in the round
     */
    public Round(QuestionGenerator generator, int numQuestions) {
        _roundQuestionGenerator = generator;

        System.out.println("Starting round: "+Main.database.getNextID("roundID", "rounds"));
        _roundID = Main.database.getNextID("roundID", "rounds"); // Store ID of current round

        // Write the initial entry in the database for this round
        String query = "INSERT INTO rounds (roundid, username, date, questionSet, noquestions, nocorrect, isComplete, sessionID) VALUES ("+_roundID+", '"+Main.currentUser+"', "+ Instant.now().getEpochSecond()+", '"+generator.getGeneratorName()+"', "+numQuestions+", 0, 0, "+Main.currentSession+")";
        Main.database.insertOp(query);

        // Generates the questions
        for (int i = 0; i < numQuestions; i++) {
            _questions.add(new Question(generator, _roundID));
        }

        _numQuestions = numQuestions;

        // Start the clock
        _startTime = Instant.now().getEpochSecond();
    }

    /**
     * Checks whether there are any questions left in the round
     * @return boolean representing if there are any questions left
     */
    public boolean hasNext() {
        return _questions.size() > _currentQuestion + 1;
    }

    /**
     * Called when the round is over to finish writing data to the db
     */
    public void finish() {
        // Round is complete. Write data.
        Main.database.insertOp("UPDATE rounds SET isComplete = 1 WHERE roundid = "+_roundID);
        Main.database.insertOp("UPDATE rounds SET roundlength = "+(Instant.now().getEpochSecond() - _startTime)+" WHERE roundid = "+_roundID);
        Main.database.insertOp("UPDATE rounds SET score = "+getScore()+" WHERE roundid = "+_roundID);
        System.out.println("Calculated score: "+getScore());

        // Upload the score
        Main.netConnection.uploadScore(_roundQuestionGenerator.getGeneratorName(), getScore());
    }

    /**
     * checkAnswer is used instead of simply matching currentAnswer in order to allow statistics
     * about whether the answer was correct or incorrect to be recorded.
     * @param answer The answer to be checked
     */
    public boolean checkAnswer(String answer) {
        // Records statistics
        return currentQuestion().checkAnswer(answer);
    }

    /**
     * Returns the correct answer to the current questions
     * Note: Do not use this to check answers. Use checkAnswer() instead.
     * @return String for the current answer
     */
    public String currentAnswer() {
        return currentQuestion().getAnswer();
    }

    /**
     * Move on to the next question in the round and get the next question
     * @return String with the next question in the round
     */
    public String next() {
        if (hasNext()) {
            _currentQuestion++;
            currentQuestion().startClock(); // start the clock!
            return currentQuestion().toString();
        } else {
            throw new RuntimeException("No more Questions");
        }
    }

    public int getScore() {
        // If score is uncalculated, then calculate score.
        if (_score == null) {
            // Scoring algorithm: accuracy(%) * (20 - avgQuestionLength) * ((noQuestions + 90)/100)
            ResultSet correctRS = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE correct = 1 AND roundID = "+_roundID);
            ResultSet qLengthRS = Main.database.returnOp("SELECT AVG(timeToAnswer) FROM questions WHERE roundID = "+_roundID);
            try {
                correctRS.next();
                qLengthRS.next();
                double accuracy = (correctRS.getDouble(1)/_numQuestions)*100; // accuracy in %
                double lengthScore = Math.max(20 - qLengthRS.getDouble(1), 1); // 20 - avg time or 1
                // Calculate the score
                _score = (int)(accuracy * lengthScore * ((_numQuestions + 90)/100));
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
            return _score;
        } else {
            return _score;
        }

    }

    /**
     * Whether this is the user's last allowed attempt at the current question
     * @return boolean representing whether this is the last question
     */
    public boolean isLastAttempt() {
        return currentQuestion().isLastAttempt();
    }

    /**
     * Gets the number of the current question (ie. the nth question)
     * @return The number of the current question
     */
    public int questionNumber() {
        return _currentQuestion+1;
    }

    /**
     * Gets the current question object
     * @return The current question object
     */
    private Question currentQuestion() {
        return _questions.get(_currentQuestion);
    }

    /**
     * Gets the identifier of the current round (as stored in the database)
     * @return The current round ID
     */
    public int getRoundID() {
        return _roundID;
    }

    /**
     * Get the name of the generator used for this round (eg. "Tens Questions"
     * @return The generator name
     */
    public String getGeneratorName() {
        return _roundQuestionGenerator.getGeneratorName();
    }
}
