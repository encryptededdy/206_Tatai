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
    private ArrayList<Question> questions = new ArrayList<>();
    private int currentQuestion = -1;
    private int roundID;
    private long startTime;
    private QuestionGenerator roundQuestionGenerator;
    private int _numQuestions;
    private Integer score;
    private boolean isCustom;

    private int currentStreak = 0;

    /**
     * Constructs a Round
     * @param generator The QuestionGenerator to be used to generate the questions
     * @param numQuestions Number of questions in the round
     */
    public Round(QuestionGenerator generator, int numQuestions) {
        roundQuestionGenerator = generator;

        // Check if this is a custom round
        isCustom = generator.isCustom();

        System.out.println("Starting round: "+Main.database.getNextID("roundID", "rounds"));
        roundID = Main.database.getNextID("roundID", "rounds"); // Store ID of current round

        // Write the initial entry in the database for this round
        String query = "INSERT INTO rounds (roundid, username, date, questionSet, noquestions, nocorrect, isComplete, sessionID) VALUES ("+ roundID +", '"+Main.currentUser+"', "+ Instant.now().getEpochSecond()+", '"+generator.getGeneratorName()+"', "+numQuestions+", 0, 0, "+Main.currentSession+")";
        Main.database.insertOp(query);

        // Generates the questions
        for (int i = 0; i < numQuestions; i++) {
            questions.add(new Question(generator, roundID));
        }

        _numQuestions = numQuestions;

        // Start the clock
        startTime = Instant.now().getEpochSecond();
    }

    /**
     * Checks whether there are any questions left in the round
     * @return boolean representing if there are any questions left
     */
    public boolean hasNext() {
        return questions.size() > currentQuestion + 1;
    }

    /**
     * Called when the round is over to finish writing data to the db
     */
    public void finish() {
        // Round is complete. Write data.
        Main.database.insertOp("UPDATE rounds SET isComplete = 1 WHERE roundid = "+ roundID);
        Main.database.insertOp("UPDATE rounds SET roundlength = "+(Instant.now().getEpochSecond() - startTime)+" WHERE roundid = "+ roundID);
        Main.database.insertOp("UPDATE rounds SET score = "+getScore()+" WHERE roundid = "+ roundID);
        System.out.println("Calculated score: "+getScore());

        // Upload the score
        if (!isCustom) Main.netConnection.uploadScore(roundQuestionGenerator.getGeneratorName(), getScore());
    }

    /**
     * checkAnswer is used instead of simply matching currentAnswer in order to allow statistics
     * about whether the answer was correct or incorrect to be recorded.
     * @param answer The answer to be checked
     */
    public boolean checkAnswer(String answer) {
        // If the answer was right...
        if (currentQuestion().checkAnswer(answer)) {
            currentStreak++; // Increase the streak
            return true;
        } else {
            currentStreak = 0; // Reset the streak
            return false;
        }

    }

    /**
     * Gets the length of the current correct questions streak.
     * @return The length
     */
    public int getStreak() {
        return currentStreak;
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
            currentQuestion++;
            currentQuestion().startClock(); // start the clock!
            return currentQuestion().toString();
        } else {
            throw new RuntimeException("No more Questions");
        }
    }

    /**
     * Calculates the score for this round using the scoring algorithm
     * @return The score for this round
     */
    public int getScore() {
        // If score is uncalculated, then calculate score.
        if (score == null) {
            // Scoring algorithm: accuracy(%) * (20 - avgQuestionLength) * ((noQuestions + 90)/100)
            ResultSet correctRS = Main.database.returnOp("SELECT COUNT(*) FROM questions WHERE correct = 1 AND roundID = "+ roundID);
            ResultSet qLengthRS = Main.database.returnOp("SELECT AVG(timeToAnswer) FROM questions WHERE roundID = "+ roundID);
            try {
                correctRS.next();
                qLengthRS.next();
                double accuracy = (correctRS.getDouble(1)/_numQuestions)*100; // accuracy in %
                double lengthScore = Math.max(20 - qLengthRS.getDouble(1), 1); // 20 - avg time or 1
                // Calculate the score
                score = (int)(accuracy * lengthScore * ((_numQuestions + 90)/100));
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
            return score;
        } else {
            return score;
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
        return currentQuestion +1;
    }

    /**
     * Gets the current question object
     * @return The current question object
     */
    private Question currentQuestion() {
        return questions.get(currentQuestion);
    }

    /**
     * Gets the identifier of the current round (as stored in the database)
     * @return The current round ID
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Get the the generator used for this round
     * @return The generator
     */
    public QuestionGenerator getGenerator() {
        return roundQuestionGenerator;
    }
}
