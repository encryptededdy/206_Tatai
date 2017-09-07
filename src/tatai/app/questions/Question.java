package tatai.app.questions;

import tatai.app.questions.generators.QuestionGenerator;

public class Question {
    /**
     * Represents a question asked to a user
     *
     * Only basic functionality is implemented so far
     */
    QuestionGenerator _generator;
    String _question;
    String _answer;
    int _attempts = 0;

    public Question(QuestionGenerator generator) {
        _generator = generator;
        _question = _generator.generateQuestion();
        _answer = _generator.getAnswer();
    }

    public String toString() {
        return _question;
    }

    boolean checkAnswer(String answer) {
        // TODO: record statistics
        if (answer.contains(_answer)) {
            return true;
        } else {
            _attempts++;
            return false;
        }
    }

    boolean isLastAttempt() {
        if (_attempts == 2) {
            return true;
        } else if (_attempts < 2) {
            return false;
        } else {
            throw new RuntimeException("User has made too many attempts");
        }
    }

    String getAnswer() {
        return _answer;
    }
}
