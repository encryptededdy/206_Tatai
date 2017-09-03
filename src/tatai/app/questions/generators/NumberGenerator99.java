package tatai.app.questions.generators;

import tatai.app.questions.Translator;

import java.util.Random;

public class NumberGenerator99 implements QuestionGenerator {
    String _answer;

    public String generateQuestion() {
        Translator translator = new Translator();
        Random rng = new Random();
        Integer number = rng.nextInt(99);
        _answer = translator.toMaori(number);
        return number.toString();
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return "Numbers";
    }
}