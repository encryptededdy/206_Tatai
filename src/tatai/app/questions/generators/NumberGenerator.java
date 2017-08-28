package tatai.app.questions.generators;

import java.util.Random;

public class NumberGenerator implements QuestionGenerator {
    String _answer;

    public String generateQuestion() {
        Random rng = new Random();
        Integer number = rng.nextInt(9);
        _answer = number.toString();
        return number.toString();
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return "Numbers";
    }
}
