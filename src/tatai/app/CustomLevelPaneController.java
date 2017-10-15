package tatai.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;
import java.util.ArrayList;

public class CustomLevelPaneController {
    @FXML JFXListView<String> customLevelsListView;
    @FXML JFXButton playBtn;
    @FXML JFXButton lvlWorkshopBtn;

    private Node levelSelectorParent;

    public void initialize() {
        populateCustomLevelsList();
    }

    private void populateCustomLevelsList() {
        ArrayList<QuestionGenerator> questionGeneratorNames = Main.store.generators.getGenerators();
        for (QuestionGenerator gen : questionGeneratorNames) {
            if (gen.isCustom()) {
                customLevelsListView.getItems().add(gen.getGeneratorName());
            }
        }
        customLevelsListView.getSelectionModel().selectFirst();
    }

    public void setParentNode(Node parent) {
        levelSelectorParent = parent;
    }

    @FXML public void playBtnPressed() throws IOException {
        System.out.println("Please work");
        String customLevelName = customLevelsListView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(Main.store.generators.getGeneratorFromName(customLevelName));

        Scene scene = playBtn.getScene();
        FadeTransition ft = TransitionFactory.fadeOut(levelSelectorParent);
        ft.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        ft.play();
    }

    @FXML public void lvlWorkshopBtnPressed() throws IOException {
        switchToToolbar(Layout.CUSTOMGENERATOR);
    }

    private void switchToToolbar(Layout target) throws IOException {
        // Load the new scene
        Scene scene = lvlWorkshopBtn.getScene();
        FXMLLoader loader = target.loader();
        Parent root = loader.load();
        // Fade out
        FadeTransition ft = TransitionFactory.fadeOut(levelSelectorParent, (int) (Main.transitionDuration * 0.5));
        ft.setOnFinished(event -> {
            scene.setRoot(root);
            loader.<ToolbarController>getController().fadeIn();
        });
        ft.play();
    }
}
