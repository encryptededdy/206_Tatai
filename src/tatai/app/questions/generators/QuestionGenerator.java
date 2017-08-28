package tatai.app.questions.generators;

/**
 * Generates Math questions
 */

public interface QuestionGenerator {
    String generateQuestion();
    String getAnswer();
    String getGeneratorName();
}
