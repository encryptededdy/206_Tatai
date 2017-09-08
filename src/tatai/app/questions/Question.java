package tatai.app.questions;

import tatai.app.Main;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Database;

import java.time.Instant;

/**
 * Represents a question presented during a Round. Requires a QuestionGenerator to generate questions.
 */
public class Question {
    private String _question;
    private String _answer;
    private int _attempts = 0;
    private boolean _correct = false;
    private QuestionGenerator _generator;
    private long _startTime;
    private int _runTime;
    private int _roundID;

    /**
     * Constructs a Question using a specific QuestionGenerator
     * @param generator The QuestionGenerator to be used to generate this Question
     */
    public Question(QuestionGenerator generator, int roundID) {
        _generator = generator;
        _question = _generator.generateQuestion();
        _answer = _generator.getAnswer();
        _roundID = roundID;
    }

    /**
     * Converts the question to a string. Represents the question asked
     * @return  A string representing the question asked
     */
    public String toString() {
        return _question;
    }

    /**
     * Called when the question is first displayed - represents when the question was first asked, used for timing
     */
    public void startClock() {
        _startTime = Instant.now().getEpochSecond(); // Start timer (this is when the question is first displayed)
    }

    /**
     * Check whether an answer is correct. Question internally records statistics depending on whether the answer was correct
     * @param answer The answer to be checked
     * @return Boolean representing whether the answer is correct
     */
    boolean checkAnswer(String answer) {
        // TODO: record statistics
        if (answer.contains(_answer)) {
            _correct = true;
            recordData();
            return true;
        } else {
            _attempts++;
            if (_attempts == 2) {
                recordData();
            }
            return false;
        }
    }

    /**
     * Records the question data into the SQL database
     */
    private void recordData() {
        _runTime = (int)(Instant.now().getEpochSecond() - _startTime); // Record the running time for the application
        Database db = Main.database;
        String query = "INSERT INTO questions (username, date, questionSet, question, answer, correct, attempts, timeToAnswer) VALUES ('"+Main.currentUser+"', "+ Instant.now().getEpochSecond()+", '"+_generator.getGeneratorName()+"', '"+_question+"', '"+_answer+"', "+(_correct? 1 : 0)+", "+(_attempts+1)+", "+_runTime+")";
        db.insertOp(query);
        if (_correct) {
            String rndquery = "UPDATE rounds SET nocorrect = nocorrect + 1 WHERE roundid = "+_roundID;
            db.insertOp(rndquery);
        }
    }

    /**
     * Checks whether this is the user's last attempt at this question (users get 2 attempts)
     * @return Boolean representing whether this is the last attempt
     */
    boolean isLastAttempt() {
        if (_attempts == 2) {
            return true;
        } else if (_attempts < 2) {
            return false;
        } else {
            throw new RuntimeException("User has made too many attempts");
        }
    }

    /**
     * Retrieves the answer to this question for display
     * Note: Do not use this to check whether the answer is correct, as it will prevent statistics from being
     * recorded correctly.
     * @return String with the answer
     */
    String getAnswer() {
        return _answer;
    }
}
