package tatai.app.questions.generators;

import tatai.app.util.Translator;

import java.util.Random;

/**
 * A QuestionGenerator that generates random numbers between 0 and 9
 * @author Edward
 */
public class NumberGenerator implements QuestionGenerator {
    String _answer;

    public String generateQuestion() {
        Random rng = new Random();
        Integer number = rng.nextInt(9);
        _answer = Translator.toMaori(number);
        return number.toString();
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return "Numbers";
    }
}
