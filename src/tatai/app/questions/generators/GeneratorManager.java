package tatai.app.questions.generators;

import java.util.ArrayList;

public class GeneratorManager {
    private ArrayList<QuestionGenerator> generators = new ArrayList<>();

    public GeneratorManager() {
        generators.add(new NumberGenerator());
        generators.add(new NumberGenerator99());
        generators.add(new MathGenerator(9, 8, MathOperator.ADD, "Easy Addition", false, false, false, 250));
        generators.add(new MathGenerator(99, 99, MathOperator.ADD, "Addition", false, false, false, 250));
        generators.add(new MathGenerator(99, 99, MathOperator.SUBTRACT, "Subtraction", false, false, false, 250));
        generators.add(new MathGenerator(99, 12, MathOperator.MULTIPLY, "Times Tables", true, false, false, 500));
        generators.add(new MathGenerator(99, 24, MathOperator.MULTIPLY, "Advanced Multiplication", false, false, false, 500));
        generators.add(new MathGenerator(20, 12, MathOperator.DIVIDE, "Division", false, false, false,750));
    }

    public ArrayList<QuestionGenerator> getGenerators() {
        return generators;
    }

    public void add(QuestionGenerator qg) {
        generators.add(qg);
    }

    public void remove(QuestionGenerator qg) {
        generators.remove(qg);
    }

    public QuestionGenerator get(int index) {
        return generators.get(index);
    }
}
