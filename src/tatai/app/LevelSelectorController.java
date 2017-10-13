package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.Layout;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

public class LevelSelectorController {
    @FXML JFXButton prevBtn;
    @FXML JFXButton nextBtn;
    @FXML ImageView backgroundImage;
    @FXML HBox levelSelectorHbox;
    @FXML GridPane levelsGridPane1;
    @FXML GridPane levelsGridPane2;

    private int prevPaneState;
    private int paneState;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        levelSelectorHbox.setOpacity(0.0);
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
        } catch (IOException iox) {
            iox.printStackTrace();
        }

    }

    public void fadeIn() {
        TransitionFactory.fadeIn(levelSelectorHbox).play();
    }

    public void togglePrevHover() {
        //Background defaultBackground = new Background(new BackgroundFill(Color.web("rgba(33,33,33)", 0.2), null, null));
        Background defaultBackground = new Background(new BackgroundFill(Color.GREEN, null, null));
        prevBtn.setBackground(defaultBackground);

        //Background hoverBackground = new Background(new BackgroundFill(Color.web("rgba(200,200,200)", 0.2), null, null));
        Background hoverBackground = new Background(new BackgroundFill(Color.BLUE, null, null));
        if (prevBtn.getBackground().equals(defaultBackground)) {
            prevBtn.setBackground(hoverBackground);
        } else {
            prevBtn.setBackground(defaultBackground);
        }
    }

    public void toggleNextHover() {
        /*
        Background defaultBackground = new Background(new BackgroundFill(Color.web("rgba(33,33,33)", 0.2), null, null));
        Background hoverBackground = new Background(new BackgroundFill(Color.web("rgba(200,200,200)", 0.2), null, null));
        if (nextBtn.getBackground().equals(defaultBackground)) {
            nextBtn.setBackground(hoverBackground);
        } else {
            nextBtn.setBackground(defaultBackground);
        }
        */
    }

    public void prevBtnClicked() {
        prevPaneState = paneState;
        if (paneState > 0) {
            paneState--;
        }
        updatePanesLocation();
        /*
        TranslateTransition tt1 = TransitionFactory.move(levelsGridPane1, -800, 0, 500);
        TranslateTransition tt2 = TransitionFactory.move(levelsGridPane2, -750, 0, 500);
        FadeTransition ft1 = TransitionFactory.fadeOut(levelsGridPane1, Main.transitionDuration);
        ScaleTransition st1 = new ScaleTransition(Duration.millis(Main.transitionDuration),levelsGridPane1);
        st1.setToX(0.5);
        st1.setToY(0.5);
        ParallelTransition pt = new ParallelTransition(tt1, tt2, ft1, st1);
        pt.play();
        */
    }

    public void nextBtnClicked() {
        prevPaneState = paneState;
        if (paneState < 2) {
            paneState++;
        }
        updatePanesLocation();
        /*
        TranslateTransition tt1 = TransitionFactory.move(levelsGridPane1, 800, 0, 500);
        TranslateTransition tt2 = TransitionFactory.move(levelsGridPane2, 750, 0, 500);
        FadeTransition ft1 = TransitionFactory.fadeIn(levelsGridPane1, Main.transitionDuration);
        ScaleTransition st1 = new ScaleTransition(Duration.millis(Main.transitionDuration),levelsGridPane1);
        st1.setToX(1);
        st1.setToY(1);
        ParallelTransition pt = new ParallelTransition(tt1, tt2, ft1, st1);
        pt.play();
        */
    }

    private void createLevelPane(String questionGenerator, GridPane gridPane, int x, int y) throws IOException {
        FXMLLoader loader = Layout.LEVELPANE.loader();
        Parent pane = loader.load();
        loader.<LevelPaneController>getController().setQuestionGenerators(questionGenerator);
        System.out.println(questionGenerator);
        loader.<LevelPaneController>getController().setParentNode(levelSelectorHbox);
        GridPane.setConstraints(pane, x, y);
        GridPane.setMargin(pane, new Insets(0, 10, 10, 10));
        gridPane.getChildren().add(pane);
    }

    private void updatePanesLocation() {
        System.out.println(paneState);
        System.out.println(prevPaneState);
        if (paneState == 0 && prevPaneState == 1) {
            TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), levelsGridPane1);
            if (prevPaneState == 0) {
                tt1.setToX(0);
            } else if (prevPaneState == 1) {
                tt1.setToX(850);
            }
            TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), levelsGridPane2);
            if (prevPaneState == 0) {
                tt2.setToX(0);
            } else if (prevPaneState == 1) {
                tt2.setToX(750);
            }
            ParallelTransition pt = new ParallelTransition(tt1, tt2);
            pt.setOnFinished(event -> {
                levelsGridPane1.setLayoutX(50);
                levelsGridPane1.setTranslateX(0);
                levelsGridPane2.setLayoutX(800);
                levelsGridPane2.setTranslateX(0);
            });
            pt.play();
        } else if (paneState == 1) {
            TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), levelsGridPane1);
            tt1.setToX(-800);
            TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), levelsGridPane2);
            if (prevPaneState == 0) {
                tt2.setToX(-750);
            } else if ( prevPaneState == 2) {
                tt2.setToX(850);
            }
            ParallelTransition pt = new ParallelTransition(tt1, tt2);
            pt.setOnFinished(event -> {
                levelsGridPane1.setLayoutX(-800);
                levelsGridPane1.setTranslateX(0);
                levelsGridPane2.setLayoutX(50);
                levelsGridPane2.setTranslateX(0);
            });
            pt.play();

        } else if (paneState == 2) {
            TranslateTransition tt1 = new TranslateTransition(Duration.millis(500), levelsGridPane1);
            tt1.setToX(-800);
            TranslateTransition tt2 = new TranslateTransition(Duration.millis(500), levelsGridPane2);
            tt2.setToX(-800);
            ParallelTransition pt = new ParallelTransition(tt1, tt2);
            pt.setOnFinished(event -> {
                levelsGridPane1.setLayoutX(-800);
                levelsGridPane1.setTranslateX(0);
                levelsGridPane2.setLayoutX(-800);
                levelsGridPane2.setTranslateX(0);
            });
            pt.play();
        }

    }

}
