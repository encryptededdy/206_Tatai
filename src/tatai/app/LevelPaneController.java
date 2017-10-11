package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

public class LevelPaneController {
    @FXML JFXButton playBtn;
    @FXML JFXButton playMaoriBtn;
    @FXML Label captionLabel;
    @FXML Label levelNameLabel;
    @FXML AnchorPane bronzePane;
    @FXML AnchorPane silverPane;
    @FXML AnchorPane goldPane;

    private Node levelSelectorParent;

    private QuestionGenerator _normalNumberGenerator;
    private QuestionGenerator _maoriNumberGenerator;

    public void initialize() {

    }

    public void setParentNode(Node parent) {
        levelSelectorParent = parent;
    }

    public void setQuestionGenerators(String generatorName) {
        _normalNumberGenerator = Main.questionGenerators.get(generatorName);
        _maoriNumberGenerator = Main.questionGenerators.get(generatorName + " (Maori)");

        levelNameLabel.setText(generatorName);
    }

    public void playBtnPressed() throws IOException {
        Scene scene = playMaoriBtn.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(_normalNumberGenerator.getGeneratorName());

        FadeTransition ft = TransitionFactory.fadeOut(levelSelectorParent);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        ft.play();
    }

    public void playMaoriBtnPressed() throws IOException {
        Scene scene = playMaoriBtn.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(_maoriNumberGenerator.getGeneratorName());

        FadeTransition ft = TransitionFactory.fadeOut(levelSelectorParent);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        ft.play();
    }
}
