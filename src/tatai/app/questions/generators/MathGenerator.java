package tatai.app.questions.generators;

import tatai.app.util.Translator;

import java.util.Random;

public class MathGenerator implements QuestionGenerator {
    private String _answer;

    // The high and low bounds of the RNG
    private int highBound;
    private int firstOperandMax;
    private int secondOperandMax;
    private MathOperator operator;
    private String name;
    private boolean allowMultiplyByOne;

    public MathGenerator(int highBound, int firstOperandMax, int secondOperandMax, MathOperator operator, String name, boolean allowMultiplyByOne) {
        this.highBound=highBound;
        this.firstOperandMax=firstOperandMax;
        this.secondOperandMax=secondOperandMax;
        this.operator=operator;
        this.name=name;
        this.allowMultiplyByOne = allowMultiplyByOne;
    }

    public MathGenerator(int highBound, int firstOperandMax, int secondOperandMax, MathOperator operator, String name) {
        this(highBound, firstOperandMax, secondOperandMax, operator, name, false);
    }

    public String generateQuestion() {
        Random rng = new Random();
        Integer firstNumber = 0;
        Integer secondNumber = 0;
        int number = 0;
        // TODO: Make the generation algorithm less shitty
        // While our generated number doesn't meet the specification...
        while (!((firstNumber * secondNumber) <= highBound && (firstNumber + secondNumber) >= 1)) {
            switch (operator) {
                case ADD:
                    firstNumber = rng.nextInt(firstOperandMax+1);
                    secondNumber = rng.nextInt(firstOperandMax+1);
                    number = firstNumber + secondNumber;
                    break;
                case SUBTRACT:
                    break;
                case MULTIPLY:
                    if (allowMultiplyByOne) {
                        firstNumber = rng.nextInt(firstOperandMax) + 1;
                        secondNumber = rng.nextInt(secondOperandMax) + 1;
                    } else {
                        firstNumber = rng.nextInt(firstOperandMax) + 2;
                        secondNumber = rng.nextInt(secondOperandMax) + 2;
                    }
                    number = firstNumber * secondNumber;
                    break;
                case DIVIDE:
                    break;
            }
        }
        // Our generated number now meets the specs
        _answer = Translator.toMaori(number);
        switch (operator) {
            case ADD:
                return firstNumber+" + "+secondNumber;
            case SUBTRACT:
                return firstNumber+" - "+secondNumber;
            case MULTIPLY:
                return firstNumber+" x "+secondNumber;
            case DIVIDE:
                return firstNumber+" / "+secondNumber;
            default:
                throw new UnsupportedOperationException("Unknown operator: "+operator);
        }
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return name;
    }

}
