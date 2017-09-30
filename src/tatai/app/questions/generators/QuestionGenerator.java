package tatai.app.questions.generators;

/**
 * A QuestionGenerator generates questions and their corresponding answers
 * @author Edward
 */

public interface QuestionGenerator {
    /**
     * Generates a new question
     * @return The question
     */
    String generateQuestion();

    /**
     * Gets the answer to said question
     * @return The answer
     */
    String getAnswer();

    /**
     * Gets a the full name of this generator
     * @return Full name
     */
    String getGeneratorName();
}
