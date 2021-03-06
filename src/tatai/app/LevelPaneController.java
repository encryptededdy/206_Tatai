package tatai.app;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.achievements.Achievement;
import tatai.app.util.achievements.AchievementManager;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

/**
 * Controller for the Level panes within the level select screen
 *
 * @author Edward
 * @author Zach
 */
public class LevelPaneController {
    @FXML JFXButton playBtn, bigPlayBtn, unlockBtn, playMaoriBtn;
    @FXML Label captionLabel;
    @FXML Label levelNameLabel;
    @FXML FontAwesomeIconView bronze;
    @FXML FontAwesomeIconView silver;
    @FXML FontAwesomeIconView gold;

    @FXML Pane lockOverlay, innerData, pane;

    private LevelSelectorController parentController;

    private int locX, locY;

    private QuestionGenerator generator;

    /**
     * Sets the X/Y location of this pane on the grid (used for animations)
     */
    void setLocation(int x, int y) {
        locX = x;
        locY = y;
    }

    /**
     * Defines the questionGenerator for this pane
     * @param generator The QuestionGenerator this pane represents
     * @param parentController The controller for the level select screen that this is in
     */
    void setQuestionGenerators(QuestionGenerator generator, LevelSelectorController parentController) {
        this.parentController = parentController;
        this.generator = generator;
        if (!generator.supportsMaori()) { // If Maori is not supported
            bigPlayBtn.setVisible(true);
        }
        levelNameLabel.setText(generator.getGeneratorName());
        captionLabel.setText(generator.getDescription());

        // Check if the generator is locked
        if (generator.isUnlocked()) {
            lockOverlay.setVisible(false);
            innerData.setEffect(null);
        } else { // If it's locked, show the unlock button and cost
            unlockBtn.setText("Unlock for ฿"+generator.getCost());
            if (Main.store.getBalance() < generator.getCost()) {
                unlockBtn.setText("Need ฿"+(generator.getCost()-Main.store.getBalance())+" more");
                unlockBtn.setDisable(true);
            }
        }

        // Get the bronze/silver/gold achievements for this level
        AchievementManager achievementManager = Main.store.achievements;
        String generatorName = generator.getGeneratorName();
        Achievement bronzeAchievement = achievementManager.getAchievements().get(generatorName + " - Bronze");
        Achievement silverAchievement = achievementManager.getAchievements().get(generatorName + " - Silver");
        Achievement goldAchievement = achievementManager.getAchievements().get(generatorName + " - Gold");

        // Disable all the trophies
        bronze.setOpacity(0.2);
        silver.setOpacity(0.2);
        gold.setOpacity(0.2);

        // Enable the trophies if they've been achieved
        if (bronzeAchievement.isCompleted()) {
            bronze.setOpacity(1);
        }
        if (silverAchievement.isCompleted()) {
            silver.setOpacity(1);
        }
        if (goldAchievement.isCompleted()) {
            gold.setOpacity(1);
        }
    }

    /**
     * Handle unlocking a level if it's locked
     */
    @FXML
    public void unlockBtnPressed() {
        if (generator.unlock()) {
            System.out.println("Unlocked!");
            parentController.recreatePanes();
        }
    }

    /**
     * Play the game
     */
    public void playBtnPressed() throws IOException {
        switchScene(false);
    }

    /**
     * Play the game, but in Maori
     */
    public void playMaoriBtnPressed() throws IOException {
        switchScene(true);
    }

    /**
     * Switch to the question screen, with animation
     * @param maori Whether to start the game in maori
     */
    private void switchScene(boolean maori) throws IOException {
        pane.toFront();
        generator.setMaori(maori);
        ParallelTransition pt = fadeAnimate();
        Scene scene = pane.getScene();
        FXMLLoader loader = Layout.QUESTION.loader();
        Parent root = loader.load();
        loader.<QuestionController>getController().setQuestionSet(generator);
        pt.setOnFinished(event -> {scene.setRoot(root); loader.<QuestionController>getController().fadeIn();});
        pt.play();
    }

    /**
     * Animate the pane into the center for transition
     * @return Transition of the pane moving into the center
     */
    private ParallelTransition fadeAnimate() {
        // Fade in the dropshadow
        DropShadow ds = (DropShadow) pane.getEffect();
        Timeline fadeDropShadow = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(ds.colorProperty(), new Color(0, 0, 0, 0))),
                new KeyFrame(Duration.millis(Main.transitionDuration*2), new KeyValue(ds.colorProperty(), new Color(0, 0, 0, 1)))
        );
        // Fade out the data in this LevelPane
        FadeTransition ft = TransitionFactory.fadeOut(innerData, Main.transitionDuration);
        // Move + Scale this LevelPane
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration*2), pane);
        st.setToX(1.28125);
        st.setToY(1.2952381);
        TranslateTransition tt;
        if (locX == 0 && locY == 0) { // top left
            tt = TransitionFactory.move(pane, 166, 39, Main.transitionDuration*2);
        } else if (locX == 1 && locY == 0) { // top right
            tt = TransitionFactory.move(pane, -164, 39, Main.transitionDuration*2);
        } else if (locX == 0 && locY == 1) { // bottom left
            tt = TransitionFactory.move(pane, 166, -181, Main.transitionDuration*2);
        } else { // bottom right
            tt = TransitionFactory.move(pane, -164, -181, Main.transitionDuration*2);
        }
        // Fade out everything else
        ParallelTransition otherNodes = parentController.fadeOutOtherCards(pane);
        return new ParallelTransition(ft, tt, st, otherNodes, fadeDropShadow);
    }
}
