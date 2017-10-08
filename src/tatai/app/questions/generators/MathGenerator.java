package tatai.app.questions.generators;

import tatai.app.util.Translator;

import java.util.Random;

/**
 * Generates math questions according to specified parameters.
 *
 * @author Edward
 */
public class MathGenerator implements QuestionGenerator {
    private String _answer;

    private int highBound;
    private int operandMax;
    private MathOperator operator;
    private String name;
    private boolean allowMultiplyByOne;
    private boolean inMaori;

    private boolean custom;

    private final int generatorMax = 99; // The biggest number that is allowed to appear in an equation

    /**
     * Instantiate a MathGenerator with specifications governing the questions to be generated
     * @param highBound The highest number (answer) to be generated
     * @param operandMax The maximum value of any operand
     * @param operator The operator to use (ADD, SUBTRACT, DIVIDE, MULTIPLY)
     * @param name The name to name this generator
     */
    public MathGenerator(int highBound, int operandMax, MathOperator operator, String name) {
        this(highBound, operandMax, operator, name, true, false, false);
    }

    /**
     * Instantiate a MathGenerator with specifications governing the questions to be generated
     * @param highBound The highest number (answer) to be generated
     * @param operandMax The maximum value of any operand
     * @param operator The operator to use (ADD, SUBTRACT, DIVIDE, MULTIPLY)
     * @param name The name to name this generator
     * @param allowMultiplyByOne Whether to allow multiply by 1 questions (optional)
     */
    public MathGenerator(int highBound, int operandMax, MathOperator operator, String name, boolean allowMultiplyByOne) {
        this(highBound, operandMax, operator, name, allowMultiplyByOne, false, false);
    }

    /**
     * Instantiate a MathGenerator with specifications governing the questions to be generated
     * @param highBound The highest number (answer) to be generated
     * @param operandMax The maximum value of any operand
     * @param operator The operator to use (ADD, SUBTRACT, DIVIDE, MULTIPLY)
     * @param name The name to name this generator
     * @param allowMultiplyByOne Whether to allow multiply by 1 questions (optional)
     * @param inMaori Whether to generate the question in Maori
     */
    public MathGenerator(int highBound, int operandMax, MathOperator operator, String name, boolean allowMultiplyByOne, boolean inMaori) {
        this(highBound, operandMax, operator, name, allowMultiplyByOne, inMaori, false);
    }

    /**
     * Instantiate a MathGenerator with specifications governing the questions to be generated
     * @param highBound The highest number (answer) to be generated
     * @param operandMax The maximum value of any operand
     * @param operator The operator to use (ADD, SUBTRACT, DIVIDE, MULTIPLY)
     * @param name The name to name this generator
     * @param allowMultiplyByOne Whether to allow multiply by 1 questions (optional)
     * @param inMaori Whether to generate the question in Maori
     * @param custom Whether this generator is user-created (custom) or built in (not custom)
     */
    public MathGenerator(int highBound, int operandMax, MathOperator operator, String name, boolean allowMultiplyByOne, boolean inMaori, boolean custom) {
        this.highBound = highBound;
        this.operandMax = operandMax;
        this.operator = operator;
        this.name = name;
        this.allowMultiplyByOne = allowMultiplyByOne;
        this.inMaori = inMaori;
        this.custom = custom;
        // A few checks to prevent against bad inputs
        if (operator == MathOperator.SUBTRACT && operandMax < highBound) System.err.println("Warning: Range unreachable");
    }

    /**
     * Generates the question
     * @return The math question generated
     */
    public String generateQuestion() {
        Random rng = new Random(); // random num generator
        Integer firstNumber = 0; // tracks the first num in the equation
        Integer secondNumber = 0; // tracks the second num in the equation
        int number = 0;

        // While our generated number doesn't meet the specification...
        while (!(number <= highBound && number >= 1 && firstNumber <= generatorMax && secondNumber <= generatorMax)) {
            switch (operator) {
                case ADD:
                    // Generate the first number in the addition. Needs to be less than the maximum number in the equation
                    // as "minus 0" questions are no fun.
                    firstNumber = rng.nextInt(Math.min(operandMax, generatorMax - 2)) + 1;
                    // Generate the other number, maintaining that the equation answer remains in the bound
                    secondNumber = rng.nextInt(highBound - firstNumber);
                    number = firstNumber + secondNumber;
                    break;
                case SUBTRACT:
                    firstNumber = rng.nextInt(operandMax + 1); // First number in the subtraction
                    secondNumber = rng.nextInt(operandMax) + (Math.max(firstNumber - operandMax, 0)); // second number (can't be less than 0)
                    number = firstNumber - secondNumber;
                    break;
                case MULTIPLY:
                    // Check if we allow multiplication by one
                    if (allowMultiplyByOne) {
                        firstNumber = rng.nextInt(operandMax) + 1;
                        secondNumber = rng.nextInt(operandMax) + 1;
                    } else {
                        // If the max operand is one and multiplication by one is disallowed.... yeah...
                        if (operandMax < 2) throw new RuntimeException("Invalid range with x1 disallowed");
                        firstNumber = rng.nextInt(operandMax - 1) + 2;
                        secondNumber = rng.nextInt(operandMax - 1) + 2;
                    }
                    number = firstNumber * secondNumber;
                    break;
                case DIVIDE:
                    // Generate a and b, then let the question be (a*b)/b = a
                    secondNumber = rng.nextInt(operandMax) + 1;
                    firstNumber = rng.nextInt(operandMax) + 1;
                    number = firstNumber;
                    firstNumber = firstNumber * secondNumber;
                    break;
            }
        }
        // Our generated number now meets the specs, so translate it.
        _answer = Translator.toMaori(number);

        // Generate the string for the question
        if (inMaori) { // Generate the string in Maori
            switch (operator) {
                // TODO: Check these translations...
                case ADD:
                    return "E " + Translator.toMaoriDisplayable(firstNumber) + ", tāpirihia te " + Translator.toMaoriDisplayable(secondNumber);
                    // SOURCE: https://maoridictionary.co.nz/search?idiom=&phrase=&proverb=&loan=&histLoanWords=&keywords=tapiri
                case SUBTRACT:
                    return "E " + Translator.toMaoriDisplayable(firstNumber) + " tangohia te " + Translator.toMaoriDisplayable(secondNumber);
                    // SOURCE: https://maoridictionary.co.nz/search?idiom=&phrase=&proverb=&loan=&histLoanWords=&keywords=tango
                case MULTIPLY:
                    return "Whakarea te " + Translator.toMaoriDisplayable(firstNumber) + " ki te " + Translator.toMaoriDisplayable(secondNumber);
                    // SOURCE: https://maoridictionary.co.nz/search?idiom=&phrase=&proverb=&loan=&histLoanWords=&keywords=whakarea
                case DIVIDE:
                    return Translator.toMaoriDisplayable(firstNumber) + ", whakawehea ki te " + Translator.toMaoriDisplayable(secondNumber);
                    // TODO Find an accurate translation for number dividee by number in Te Reo Maori
                default:
                    throw new UnsupportedOperationException("Unknown operator: " + operator);
            }
        } else { // Generate the string in arabic numerals / english
            switch (operator) {
                case ADD:
                    return firstNumber + " + " + secondNumber;
                case SUBTRACT:
                    return firstNumber + " - " + secondNumber;
                case MULTIPLY:
                    return firstNumber + " x " + secondNumber;
                case DIVIDE:
                    return firstNumber + " ÷ " + secondNumber;
                default:
                    throw new UnsupportedOperationException("Unknown operator: " + operator);
            }
        }
    }

    public String getAnswer() {
        return _answer;
    }

    public String getGeneratorName() {
        return name;
    }

    public boolean isCustom() { return custom; };

}
