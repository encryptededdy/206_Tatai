package tatai.app.questions.generators;

import java.util.ArrayList;

/**
 * QuestionGenerator that generates a fixed series of numbers (for TataiNet use)
 *
 * @author Edward
 */
public class FixedGenerator implements QuestionGenerator {
    private ArrayList<String> questions = new ArrayList<>();
    private ArrayList<String> answers = new ArrayList<>();

    private String name;

    private int currentPlace = 0;

    public FixedGenerator(QuestionGenerator gen, int length) {
        for (int i = 0; i < length; i++) {
            String question = gen.generateQuestion();
            String answer = gen.getAnswer();
            questions.add(question);
            answers.add(answer);
        }
        name = gen.getGeneratorName();
    }

    public String generateQuestion() {
        currentPlace++;
        return questions.get(currentPlace-1);
    }

    public String getAnswer() {
        return answers.get(currentPlace-1);
    }

    public String getGeneratorName() {
        return name;
    }

    // Custom level
    public boolean isCustom() { return true; };

    // Doesn't support maori
    public boolean supportsMaori() {return false; }

    // Setting Maori does nothing, as we don't support it
    public void setMaori(boolean maori) {};

    public String getDescription() {return "Generator for Online Mode";}

    public boolean isUnlocked() {return true; }

    public boolean unlock() {return false; }

    public int getCost() {return 0; }
}
