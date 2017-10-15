package tatai.app.questions.generators;

import java.util.ArrayList;

public class GeneratorManager {
    private ArrayList<QuestionGenerator> generators = new ArrayList<>();

    public GeneratorManager() {
        generators.add(new NumberGenerator());
        generators.add(new NumberGenerator99());
        generators.add(new MathGenerator(9, 8, MathOperator.ADD, "Easy Addition", false, false, false, 250,
                "Addition for numbers from 1 through 9"));
        generators.add(new MathGenerator(99, 99, MathOperator.ADD, "Addition", false, false, false, 250,
                "Addition for all numbers, up to 99"));
        generators.add(new MathGenerator(99, 99, MathOperator.SUBTRACT, "Subtraction", false, false, false, 250,
                "Subtraction for all numbers"));
        generators.add(new MathGenerator(99, 12, MathOperator.MULTIPLY, "Times Tables", true, false, false, 500, "" +
                "Multiplication of numbers up to 12 together"));
        generators.add(new MathGenerator(99, 24, MathOperator.MULTIPLY, "Advanced Multiplication", false, false, false, 500,
                "Multiplication of numbers up to 24 together"));
        generators.add(new MathGenerator(20, 12, MathOperator.DIVIDE, "Division", false, false, false,750,
                "Division with denominators up to 12"));
    }

    /**
     * Gets the array of QuestionGenerators avaliable
     * @return ArrayList of QuestionGenerator
     */
    public ArrayList<QuestionGenerator> getGenerators() {
        return generators;
    }

    /**
     * Gets an array of QuestionGenerators avaliable as a string
     * @return ArrayList of String
     */
    public ArrayList<String> getGeneratorsString() {
        ArrayList<String> out = new ArrayList<>();
        for (QuestionGenerator qg : generators) {
            out.add(qg.getGeneratorName());
        }
        return out;
    }

    /**
     * Get a QuestionGenerator by name
     * @param name String: the name of the generator
     * @return The QuestionGenerator object
     */
    public QuestionGenerator getGeneratorFromName(String name) {
        int index = getGeneratorsString().indexOf(name);
        return generators.get(index);
    }

    /**
     * Gets the next, non-locked, non-custom generator avaliable
     * @param currentGen The current generator (object)
     * @return The next generator, or null if no such generator exists
     */
    public QuestionGenerator getNextGenerator(QuestionGenerator currentGen) {
        boolean foundCurrent = false;
        for (QuestionGenerator qg : generators) {
            if (foundCurrent && qg.isUnlocked() && !qg.isCustom()) {
                return qg;
            }
            if (qg.getGeneratorName().equals(currentGen.getGeneratorName())) foundCurrent = true;
        }
        return null;
    }

    /**
     * Add a QuestionGenerator
     * @param qg The QuestionGenerator object to add
     */
    public void add(QuestionGenerator qg) {
        generators.add(qg);
    }

    /**
     * Deletes a QuestionGenerator
     * @param qg The QuestionGenerator object to remove
     */
    public void remove(QuestionGenerator qg) {
        generators.remove(qg);
    }

    /**
     * Deletes a QuestionGenerator
     * @param index The index of the QuestionGenerator to remove
     */
    public void remove(int index) {
        generators.remove(index);
    }

    /**
     * Gets a QuestionGenerator
     * @param index The index of the QuestionGenerator object to get
     */
    public QuestionGenerator get(int index) {
        return generators.get(index);
    }
}
