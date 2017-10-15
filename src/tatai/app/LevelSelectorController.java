package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

public class LevelSelectorController {
    @FXML ImageView backgroundImage;
    @FXML GridPane levelsGridPane1;
    @FXML GridPane levelsGridPane2;
    @FXML AnchorPane customLevelPane;
    @FXML Pane mainPane;
    @FXML JFXButton prevBtn;
    @FXML JFXButton nextBtn;

    private int prevPaneState;
    private int paneState;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        mainPane.setOpacity(0.0);
        paneState = 0;
        prevPaneState = 0;

        try {
            createLevelPane("Numbers", levelsGridPane1, 0, 0);
            createLevelPane("Tens Numbers", levelsGridPane1, 1, 0);
            createLevelPane("Easy Addition", levelsGridPane1, 0, 1);
            createLevelPane("Addition", levelsGridPane1, 1, 1);

            createLevelPane("Subtraction", levelsGridPane2, 0, 0);
            createLevelPane("Times Tables", levelsGridPane2, 1, 0);
            createLevelPane("Multiplication", levelsGridPane2, 0, 1);
            createLevelPane("Division", levelsGridPane2, 1, 1);

            createCustomLevelPane();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    public void fadeIn() {
        TransitionFactory.fadeIn(mainPane).play();
    }

    public void prevBtnClicked() {
        prevPaneState = paneState;
        if (paneState > 0) {
            paneState--;
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

    private void createLevelPane(String questionGenerator, GridPane gridPane, int x, int y) throws IOException {
        FXMLLoader loader = Layout.LEVELPANE.loader();
        Parent pane = loader.load();
        loader.<LevelPaneController>getController().setQuestionGenerators(questionGenerator);
        System.out.println(questionGenerator);
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
