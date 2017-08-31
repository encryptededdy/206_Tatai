package tatai.app.questions;

import tatai.app.questions.generators.NumberGenerator;
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

    public Question(QuestionGenerator generator) {
        _generator = generator;
        _question = _generator.generateQuestion();
        _answer = _generator.getAnswer();
    }

    public String toString() {
        return _question;
    }

    public boolean checkAnswer(String answer) {
        // TODO: record statistics
        return _answer.equals(answer);
    }

    public String getAnswer() {
        return _answer;
    }
}
