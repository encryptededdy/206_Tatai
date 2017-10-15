package tatai.app.questions.generators;

import tatai.app.util.Translator;

import java.util.Random;

/**
 * A QuestionGenerator that generates random numbers between 0 and 9
 * @author Edward
 */
public class NumberGenerator implements QuestionGenerator {
    private String _answer;

    public String generateQuestion() {
        Random rng = new Random();
        Integer number = rng.nextInt(9)+1;
        _answer = Translator.toMaori(number);
        return number.toString();
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return "Numbers";
    }

    // Not a custom level
    public boolean isCustom() { return false; };

    // Doesn't support maori
    public boolean supportsMaori() {return false; }

    // Setting Maori does nothing, as we don't support it
    public void setMaori(boolean maori) {};

    public String getDescription() {return "Random numbers from tahi to iwa";}

    public boolean isUnlocked() {return true; }

    public boolean unlock() {return false; }

    public int getCost() {return 0; }
}
