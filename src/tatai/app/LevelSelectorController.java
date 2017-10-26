package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles the level selector screen
 *
 * @author Zach
 * @author Edward
 */
public class LevelSelectorController {
    @FXML private ImageView backgroundImage, frontImg, backImg;
    @FXML private GridPane levelsGridPane1;
    @FXML private GridPane levelsGridPane2;
    @FXML private AnchorPane customLevelPane;
    @FXML private Pane mainPane, animInPane, questionPaneclr;
    @FXML private Group mainControls;
    @FXML private JFXButton prevBtn;
    @FXML private JFXButton nextBtn;

    private int prevPaneState;
    private int paneState;

    /**
     * Setup the screen and populate the level panes
     */
    public void initialize() {
        backgroundImage.setImage(Main.background);
        frontImg.setImage(Main.parralaxFront);
        backImg.setImage(Main.parralaxBack);

        prevBtn.setDisable(true);
        mainPane.setOpacity(0);
        mainPane.setCache(true);
        paneState = 0;
        prevPaneState = 0;
        recreatePanes();

        // Configure parallax mode
        if (Main.parallaxMode) {
            frontImg.setVisible(true);
            backImg.setVisible(true);
            backgroundImage.getParent().addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
                //System.out.printf("x: %f, y: %f\n", event.getSceneX(), event.getSceneY());
                double scale = 0.015;
                double backScale = 0.004;
                frontImg.setTranslateX((-event.getSceneX() - 400) * scale);
                frontImg.setTranslateY((-event.getSceneY() - 250) * scale);
                backImg.setTranslateX((-event.getSceneX() - 400) * backScale);
                backImg.setTranslateY((-event.getSceneY() - 250) * backScale);
            });
        } else {
            frontImg.setVisible(false);
            backImg.setVisible(false);
        }

        // Setup KB shortcuts
        backgroundImage.getParent().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.LEFT) {
                if (!prevBtn.isDisabled()) {
                    prevBtnPressed();
                }
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                if (!nextBtn.isDisabled()) {
                    nextBtnPressed();
                }
                keyEvent.consume();
            }
        });
    }

    /**
     * Regenerates the panes for each level
     */
    void recreatePanes() {
        levelsGridPane1.getChildren().clear();
        levelsGridPane2.getChildren().clear();
        customLevelPane.getChildren().clear();
        try {
            createLevelPane(Main.store.generators.get(0), levelsGridPane1, 0, 0);
            createLevelPane(Main.store.generators.get(1), levelsGridPane1, 1, 0);
            createLevelPane(Main.store.generators.get(2), levelsGridPane1, 0, 1);
            createLevelPane(Main.store.generators.get(3), levelsGridPane1, 1, 1);
            createLevelPane(Main.store.generators.get(4), levelsGridPane2, 0, 0);
            createLevelPane(Main.store.generators.get(5), levelsGridPane2, 1, 0);
            createLevelPane(Main.store.generators.get(6), levelsGridPane2, 0, 1);
            createLevelPane(Main.store.generators.get(7), levelsGridPane2, 1, 1);

            createCustomLevelPane();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    /**
     * Animation for switching from Main Menu
     */
    public void fadeIn() {
        animInPane.setVisible(true);
        Transition mt = TransitionFactory.move(animInPane, 0, -446, Main.transitionDuration*2);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration*2), animInPane);
        st.setToX(2);
        ParallelTransition pt = new ParallelTransition(mt, st);
        Transition ft = TransitionFactory.fadeIn(mainPane);
        pt.setOnFinished(event -> ft.play());
        ft.setOnFinished(event -> animInPane.setVisible(false));
        pt.play();
    }

    /**
     * Get a transition for fading out all elements except the specified node. Used for levelPane game start anim
     * @param excludedNode The node to not fade out
     * @return The transition for the fade
     */
    ParallelTransition fadeOutOtherCards(Node excludedNode) {
        // Fade out the controls
        FadeTransition ctrl = TransitionFactory.fadeOut(mainControls, Main.transitionDuration*2);
        ParallelTransition pt = new ParallelTransition(ctrl);
        ArrayList<Node> nodes = new ArrayList<>();
        // Get all of the nodes inside the gridpanes
        nodes.addAll(levelsGridPane1.getChildren());
        nodes.addAll(levelsGridPane2.getChildren());
        for (Node node : nodes) {
            // If it isn't the excluded node, fade it out too
             if (node != excludedNode) {
                 pt.getChildren().add(TransitionFactory.fadeOut(node, Main.transitionDuration*2));
             }
        }
        return pt;
    }

    /**
     * Fade in without animating the main menu item (for Custom Level screen)
     */
    void fadeInWithoutMenu() {
        animInPane.setVisible(false);
        TransitionFactory.fadeIn(mainPane, Main.transitionDuration*2).play();
        // Setup the pane animInPane correctly for transition out
        Transition mt = TransitionFactory.move(animInPane, 0, -446, Main.transitionDuration*2);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration*2), animInPane);
        st.setToX(2);
        ParallelTransition pt = new ParallelTransition(mt, st);
        pt.play();
    }

    /**
     * Animate and return back to the main menu
     */
    @FXML private void backBtnPressed() throws IOException {
        // Switch back to the main Menu
        animInPane.setVisible(true);
        Transition mt = TransitionFactory.move(animInPane, 0, 446, Main.transitionDuration*2);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration*2), animInPane);
        st.setToX(1);
        ParallelTransition pt = new ParallelTransition(mt, st);
        Scene scene = prevBtn.getScene();
        FXMLLoader loader = Layout.MAINMENU.loader();
        Parent root = loader.load();
        loader.<MainMenuController>getController().setupFade(false);
        pt.setOnFinished(event -> {scene.setRoot(root); loader.<MainMenuController>getController().fadeIn();});
        FadeTransition ft = TransitionFactory.fadeOut(mainPane);
        ft.setOnFinished(event -> pt.play());
        ft.play();
    }

    /**
     * Switch to the last page
     */
    public void prevBtnPressed() {
        prevPaneState = paneState;
        if (paneState > 0) {
            paneState--;
            System.out.println(paneState);
            if (paneState == 0) {
                prevBtn.setDisable(true);
            }
            nextBtn.setDisable(false);
        }
        updatePanesLocation();
    }

    /**
     * Switch to the next page
     */
    public void nextBtnPressed() {
        prevPaneState = paneState;
        System.out.println(paneState);
        if (paneState == 1) {
            nextBtn.setDisable(true);
        }
        prevBtn.setDisable(false);
        if (paneState < 2) {
            paneState++;
        }
        updatePanesLocation();
    }

    /**
     * Generate a level pane in a specified location, with a specified generator
     * @param questionGenerator The generator for this level pane
     * @param gridPane The gridpane to put it in
     * @param x x location
     * @param y y location
     */
    private void createLevelPane(QuestionGenerator questionGenerator, GridPane gridPane, int x, int y) throws IOException {
        FXMLLoader loader = Layout.LEVELPANE.loader();
        Parent pane = loader.load();
        loader.<LevelPaneController>getController().setQuestionGenerators(questionGenerator, this);
        loader.<LevelPaneController>getController().setLocation(x, y);
        GridPane.setConstraints(pane, x, y);
        GridPane.setMargin(pane, new Insets(5, 5, 5, 5));
        gridPane.getChildren().add(pane);
    }

    /**
     * Generates the custom level pane
     */
    private void createCustomLevelPane() throws IOException {
        FXMLLoader loader = Layout.CUSTOMLEVELPANE.loader();
        Parent pane = loader.load();
        loader.<CustomLevelPaneController>getController().setParentNode(mainPane);
        AnchorPane.setTopAnchor(pane, 5.0);
        AnchorPane.setLeftAnchor(pane, 5.0);
        customLevelPane.getChildren().add(pane);
    }

    /**
     * Animates the pane to switch to the new paneState
     */
    private void updatePanesLocation() {
        if (paneState == 0 && prevPaneState == 1) {
            TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), levelsGridPane1);
            ScaleTransition st1 = new ScaleTransition(Duration.millis(500), levelsGridPane1);
            TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), levelsGridPane2);
            ScaleTransition st2 = new ScaleTransition(Duration.millis(500), levelsGridPane2);

            if (prevPaneState == 0) {  // Do nothing GP1 & GP2
                tt1.setToX(0);
                tt2.setToX(0);

            } else if (prevPaneState == 1) {
                tt1.setToX(870); // Animate in GP2 from left
                levelsGridPane1.setScaleY(0.5);
                levelsGridPane1.setScaleX(0.5);
                st1.setToY(1);
                st1.setToX(1);

                tt2.setToX(730); // Animate out GP2 to right
                st2.setToX(0.5);
                st2.setToY(0.5);
            }
            ParallelTransition pt = new ParallelTransition(tt1, tt2, st1, st2);
            pt.setOnFinished(event -> {
                levelsGridPane1.setLayoutX(70);
                levelsGridPane1.setTranslateX(0);
                levelsGridPane2.setLayoutX(800);
                levelsGridPane2.setTranslateX(0);
                customLevelPane.setLayoutX(800);
                customLevelPane.setTranslateX(0);
            });
            pt.play();
        } else if (paneState == 1) {
            TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), levelsGridPane1);
            ScaleTransition st1 = new ScaleTransition(Duration.millis(500), levelsGridPane1);
            TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), levelsGridPane2);
            ScaleTransition st2 = new ScaleTransition(Duration.millis(500), levelsGridPane2);
            TranslateTransition tt3 = new TranslateTransition(Duration.millis(500), customLevelPane);
            ScaleTransition st3 = new ScaleTransition(Duration.millis(500), customLevelPane);
            if (prevPaneState == 0) {
                tt1.setToX(-800); // Animate out GP1 to left
                st1.setToX(0.5);
                st1.setToY(0.5);

                tt2.setToX(-730); // Animate in GP2 from right
                levelsGridPane2.setScaleY(0.5);
                levelsGridPane2.setScaleX(0.5);
                st2.setToX(1);
                st2.setToY(1);
            } else if ( prevPaneState == 2) {
                tt2.setToX(870); // Animate in GP2 from left
                levelsGridPane2.setScaleX(0.5);
                levelsGridPane2.setScaleY(0.5);
                st2.setToY(1);
                st2.setToX(1);

                tt3.setToX(730); // Animate out CLP to right
                st3.setToX(0.5);
                st3.setToY(0.5);
            }
            ParallelTransition pt = new ParallelTransition(tt1, tt2, tt3, st1, st2, st3);
            pt.setOnFinished(event -> {
                levelsGridPane1.setLayoutX(-800);
                levelsGridPane1.setTranslateX(0);
                levelsGridPane2.setLayoutX(70);
                levelsGridPane2.setTranslateX(0);
                customLevelPane.setLayoutX(800);
                customLevelPane.setTranslateX(0);
            });
            pt.play();

        } else if (paneState == 2) {
            TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), levelsGridPane1);
            ScaleTransition st1 = new ScaleTransition(Duration.millis(500), levelsGridPane1);
            TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), levelsGridPane2);
            ScaleTransition st2 = new ScaleTransition(Duration.millis(500), levelsGridPane2);
            TranslateTransition tt3 = new TranslateTransition(Duration.millis(500), customLevelPane);
            ScaleTransition st3 = new ScaleTransition(Duration.millis(500), customLevelPane);

            if (prevPaneState == 2) {
                // Do nothing
            } else if (prevPaneState == 1) {
                tt2.setToX(-800); // Animate out GP2 to left
                st2.setToY(0.5);
                st2.setToX(0.5);

                tt3.setToX(-730); // Animate in CPL from right
                customLevelPane.setScaleX(0.5);
                customLevelPane.setScaleY(0.5);
                st3.setToX(1);
                st3.setToY(1);
            }
            ParallelTransition pt = new ParallelTransition(tt1, tt2, tt3, st1, st2, st3);
            pt.setOnFinished(event -> {
                levelsGridPane1.setLayoutX(-800);
                levelsGridPane1.setTranslateX(0);
                levelsGridPane2.setLayoutX(-800);
                levelsGridPane2.setTranslateX(0);
                customLevelPane.setLayoutX(70);
                customLevelPane.setTranslateX(0);
            });
            pt.play();
        }

    }

}
