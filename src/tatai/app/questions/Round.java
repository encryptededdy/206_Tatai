package tatai.app.questions;

import tatai.app.questions.generators.QuestionGenerator;

import java.util.ArrayList;

public class Round {
    private ArrayList<Question> _questions = new ArrayList<>();
    private int _currentQuestion = -1;

    public Round(QuestionGenerator generator, int numQuestions) {
        for (int i = 0; i < numQuestions; i++) {
            _questions.add(new Question(generator));
        }
    }

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

    public String currentAnswer() {
        return currentQuestion().getAnswer();
    }

    public String next() {
        if (hasNext()) {
            _currentQuestion++;
            return _questions.get(_currentQuestion).toString();
        } else {
            throw new RuntimeException("No more Questions");
        }
    }

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
