package tatai.app.questions;

import tatai.app.questions.generators.QuestionGenerator;

import java.util.ArrayList;

/**
 *  Represents a game Round. Stores a list of questions.
 */
public class Round {
    private ArrayList<Question> _questions = new ArrayList<>();
    private int _currentQuestion = -1;

    /**
     * Constructs a Round
     * @param generator The QuestionGenerator to be used to generate the questions
     * @param numQuestions Number of questions in the round
     */
    public Round(QuestionGenerator generator, int numQuestions) {
        for (int i = 0; i < numQuestions; i++) {
            _questions.add(new Question(generator));
        }
    }

    /**
     * Checks whether there are any questions left in the round
     * @return boolean representing if there are any questions left
     */
    public boolean hasNext() {
        return _questions.size() > _currentQuestion+1;
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
            return _questions.get(_currentQuestion).toString();
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
