package tatai.app.questions;

import tatai.app.questions.generators.QuestionGenerator;

/**
 * Represents a question presented during a Round. Requires a QuestionGenerator to generate questions.
 */
public class Question {
    private String _question;
    private String _answer;
    private int _attempts = 0;

    /**
     * Constructs a Question using a specific QuestionGenerator
     * @param generator The QuestionGenerator to be used to generate this Question
     */
    public Question(QuestionGenerator generator) {
        QuestionGenerator _generator = generator;
        _question = _generator.generateQuestion();
        _answer = _generator.getAnswer();
    }

    /**
     * Converts the question to a string. Represents the question asked
     * @return  A string representing the question asked
     */
    public String toString() {
        return _question;
    }

    /**
     * Check whether an answer is correct. Question internally records statistics depending on whether the answer was correct
     * @param answer The answer to be checked
     * @return Boolean representing whether the answer is correct
     */
    boolean checkAnswer(String answer) {
        // TODO: record statistics
        if (_answer.contains(answer)) {
            return true;
        } else {
            _attempts++;
            return false;
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
