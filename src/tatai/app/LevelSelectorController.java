package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

public class LevelSelectorController {
    @FXML private ImageView backgroundImage, frontImg, backImg;
    @FXML private GridPane levelsGridPane1;
    @FXML private GridPane levelsGridPane2;
    @FXML private AnchorPane customLevelPane;
    @FXML private Pane mainPane, animInPane;
    @FXML private JFXButton prevBtn;
    @FXML private JFXButton nextBtn;

    private int prevPaneState;
    private int paneState;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        frontImg.setImage(Main.parralaxFront);
        backImg.setImage(Main.parralaxBack);

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
    }

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

    public void fadeIn() {
        Transition mt = TransitionFactory.move(animInPane, 0, -446, Main.transitionDuration*2);
        ScaleTransition st = new ScaleTransition(Duration.millis(Main.transitionDuration*2), animInPane);
        st.setToX(2);
        ParallelTransition pt = new ParallelTransition(mt, st);
        Transition ft = TransitionFactory.fadeIn(mainPane);
        pt.setOnFinished(event -> ft.play());
        ft.setOnFinished(event -> animInPane.setVisible(false));
        pt.play();
    }

    public void prevBtnClicked() throws IOException {
        prevPaneState = paneState;
        if (paneState > 0) {
            paneState--;
        } else {
            // Switch back to the main Menu
            FadeTransition ft = TransitionFactory.fadeOut(mainPane);
            Scene scene = prevBtn.getScene();
            FXMLLoader loader = Layout.MAINMENU.loader();
            Parent root = loader.load();
            loader.<MainMenuController>getController().setupFade(false);
            ft.setOnFinished(event -> {scene.setRoot(root); loader.<MainMenuController>getController().fadeIn();});
            ft.play();
        }
        updatePanesLocation();
    }

    public void nextBtnClicked() {
        prevPaneState = paneState;
        if (paneState < 2) {
            paneState++;
        }
        updatePanesLocation();
    }

    private void createLevelPane(QuestionGenerator questionGenerator, GridPane gridPane, int x, int y) throws IOException {
        FXMLLoader loader = Layout.LEVELPANE.loader();
        Parent pane = loader.load();
        loader.<LevelPaneController>getController().setQuestionGenerators(questionGenerator, this);
        loader.<LevelPaneController>getController().setParentNode(mainPane);
        GridPane.setConstraints(pane, x, y);
        GridPane.setMargin(pane, new Insets(5, 5, 5, 5));
        gridPane.getChildren().add(pane);
    }

    private void createCustomLevelPane() throws IOException {
        FXMLLoader loader = Layout.CUSTOMLEVELPANE.loader();
        Parent pane = loader.load();
        loader.<CustomLevelPaneController>getController().setParentNode(mainPane);
        AnchorPane.setTopAnchor(pane, 5.0);
        AnchorPane.setLeftAnchor(pane, 5.0);
        customLevelPane.getChildren().add(pane);
    }

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
