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

    public boolean checkAnswer(String answer) {
        return _questions.get(_currentQuestion).checkAnswer(answer);
    }

    public Question next() {
        if (hasNext()) {
            _currentQuestion++;
            return _questions.get(_currentQuestion);
        } else {
            throw new RuntimeException("No more Questions");
        }
    }

    public int questionNumber() {
        return _currentQuestion+1;
    }

}
