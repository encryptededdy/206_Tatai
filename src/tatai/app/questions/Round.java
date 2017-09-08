package tatai.app.questions;

import tatai.app.Main;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Database;

import java.time.Instant;
import java.util.ArrayList;

/**
 *  Represents a game Round. Stores a list of questions.
 */
public class Round {
    private ArrayList<Question> _questions = new ArrayList<>();
    private int _currentQuestion = -1;
    private int _roundID;
    private long _startTime;

    /**
     * Constructs a Round
     * @param generator The QuestionGenerator to be used to generate the questions
     * @param numQuestions Number of questions in the round
     */
    public Round(QuestionGenerator generator, int numQuestions) {
        System.out.println("Starting round: "+Main.database.getNextID("roundID", "rounds"));
        _roundID = Main.database.getNextID("roundID", "rounds"); // Store ID of current round

        // Write the initial entry in the database for this round
        String query = "INSERT INTO rounds (roundid, username, date, questionSet, noquestions, nocorrect, isComplete, sessionID) VALUES ("+_roundID+", '"+Main.currentUser+"', "+ Instant.now().getEpochSecond()+", '"+generator.getGeneratorName()+"', "+numQuestions+", 0, 0, "+Main.currentSession+")";
        Main.database.insertOp(query);

        // Generates the questions
        for (int i = 0; i < numQuestions; i++) {
            _questions.add(new Question(generator, _roundID));
        }

        // Start the clock
        _startTime = Instant.now().getEpochSecond();
    }

    /**
     * Checks whether there are any questions left in the round
     * @return boolean representing if there are any questions left
     */
    public boolean hasNext() {
        if (_questions.size() > _currentQuestion+1) {
            return true;
        } else {
            finish();
            return false;
        }
    }

    /**
     * Called when the round is over to finish writing data to the db
     */
    public void finish() {
        // Round is complete. Write data.
        Main.database.insertOp("UPDATE rounds SET isComplete = 1 WHERE roundid = "+_roundID);
        Main.database.insertOp("UPDATE rounds SET roundlength = "+(Instant.now().getEpochSecond() - _startTime)+" WHERE roundid = "+_roundID);
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

    /**
     * Whether this is the user's last allowed attempt at the current question
     * @return boolean representing whether this is the last question
     */
    public boolean isLastAttempt() {
        return currentQuestion().isLastAttempt();
    }

    public int questionNumber() {
        return _currentQuestion+1;
    }

    private Question currentQuestion() {
        return _questions.get(_currentQuestion);
    }
}
