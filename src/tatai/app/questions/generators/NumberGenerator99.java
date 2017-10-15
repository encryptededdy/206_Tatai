package tatai.app.questions.generators;

import tatai.app.util.Translator;

import java.util.Random;

/**
 * A QuestionGenerator that generates random numbers between 0 and 99
 * @author Edward
 */
public class NumberGenerator99 implements QuestionGenerator {
    private String _answer;

    public String generateQuestion() {
        Random rng = new Random();
        Integer number = rng.nextInt(98)+1;
        _answer = Translator.toMaori(number);
        return number.toString();
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return "Tens Numbers";
    }

    // Not a custom level
    public boolean isCustom() { return false; };

    // Doesn't support maori
    public boolean supportsMaori() {return false; }

    // Setting Maori does nothing, as we don't support it
    public void setMaori(boolean maori) {};

    public boolean isUnlocked() {return true; }

    public boolean unlock() {return false; }

    public int getCost() {return 0; }
}
