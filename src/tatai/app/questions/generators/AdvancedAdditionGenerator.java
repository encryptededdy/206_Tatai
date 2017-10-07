package tatai.app.questions.generators;

import tatai.app.util.Translator;

import java.util.Random;

/**
 * A QuestionGenerator that generates random addition questions between 0 and 9
 * @author Edward
 */
public class AdvancedAdditionGenerator implements QuestionGenerator {
    private String _answer;

    // The high and low bounds of the RNG
    private final int highBound = 99;

    public String generateQuestion() {
        Random rng = new Random();
        Integer firstNumber = 0;
        Integer secondNumber = 0;
        // While our generated number doesn't meet the specification...
        while (!((firstNumber + secondNumber) <= highBound && (firstNumber + secondNumber) >= 1)) {
            firstNumber = rng.nextInt(highBound + 1);
            secondNumber = rng.nextInt(highBound + 1);
        }
        int number = firstNumber + secondNumber;
        // Our generated number now meets the specs
        _answer = Translator.toMaori(number);
        return firstNumber+" + "+secondNumber;
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return "Advanced Addition";
    }
}
