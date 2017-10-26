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

    /**
     * Gets the description of this generator
     * @return String description
     */
    String getDescription();

    /**
     * Checks whether this generator is user created. If so, high scores cannot be shared
     * @return Whether the generator is custom
     */
    boolean isCustom();

    /**
     * Check whether this generator supports Maori question generation
     * @return Whether Maori is supported
     */
    boolean supportsMaori();

    /**
     * Set the generator to generate maori questions... or not
     * @param maori Whether to generate maori questions
     */
    void setMaori(boolean maori);

    /**
     * Checks to see if the user has unlocked this generator
     * @return If the generator is unlocked
     */
    boolean isUnlocked();

    /**
     * Unlocks the generator (and subtracts the cost)
     * @return Whether the unlock was successful (failure could be already unlocked or insufficient funds)
     */
    boolean unlock();

    /**
     * Gets the cost of this level
     * @return The cost
     */
    int getCost();
}
