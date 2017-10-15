package tatai.app;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

public class LevelPaneController {
    @FXML JFXButton playBtn, bigPlayBtn;
    @FXML JFXButton playMaoriBtn;
    @FXML Label captionLabel;
    @FXML Label levelNameLabel;
    @FXML FontAwesomeIconView bronze;
    @FXML FontAwesomeIconView silver;
    @FXML FontAwesomeIconView gold;

    private Node levelSelectorParent;

    private QuestionGenerator generator;

    public void initialize() {

    }

    void setParentNode(Node parent) {
        levelSelectorParent = parent;
    }

    void setQuestionGenerators(QuestionGenerator generator) {
        this.generator = generator;
        if (!generator.supportsMaori()) { // If Maori is not supported
            bigPlayBtn.setVisible(true);
        }
        levelNameLabel.setText(generator.getGeneratorName());
        captionLabel.setText(generator.getDescription());

        // Disable the trophys until we know what to do with them
        bronze.setOpacity(0.2);
        silver.setOpacity(0.2);
        gold.setOpacity(0.2);
    }

    public void playBtnPressed() throws IOException {
        Scene scene = playMaoriBtn.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        generator.setMaori(false);
        loader.<QuestionController>getController().setQuestionSet(generator);

        FadeTransition ft = TransitionFactory.fadeOut(levelSelectorParent);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        ft.play();
    }

    public void playMaoriBtnPressed() throws IOException {
        Scene scene = playMaoriBtn.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        generator.setMaori(true);
        loader.<QuestionController>getController().setQuestionSet(generator);

        FadeTransition ft = TransitionFactory.fadeOut(levelSelectorParent);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        ft.play();
    }
}
