package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import tatai.app.questions.generators.QuestionGenerator;
import tatai.app.util.factories.TransitionFactory;

import java.io.IOException;

public class LevelSelectorController {
    @FXML JFXButton prevBtn;
    @FXML JFXButton nextBtn;
    @FXML ImageView backgroundImage;
    @FXML HBox levelSelectorHbox;
    @FXML GridPane levelsGridPane1;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        levelSelectorHbox.setOpacity(0.0);

        try {
            createLevelPane("Numbers", levelsGridPane1, 0, 0);
            createLevelPane("Tens Numbers", levelsGridPane1, 1, 0);
            createLevelPane("Easy Addition", levelsGridPane1, 0, 1);
            createLevelPane("Addition", levelsGridPane1, 1, 1);
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
        System.out.println("hurr duur ima sheep");
        //Background hoverBackground = new Background(new BackgroundFill(Color.web("rgba(200,200,200)", 0.2), null, null));
        Background hoverBackground = new Background(new BackgroundFill(Color.BLUE, null, null));
        if (prevBtn.getBackground().equals(defaultBackground)) {
            prevBtn.setBackground(hoverBackground);
        } else {
            prevBtn.setBackground(defaultBackground);
        }
    }

    public void toggleNextHover() {
        Background defaultBackground = new Background(new BackgroundFill(Color.web("rgba(33,33,33)", 0.2), null, null));
        Background hoverBackground = new Background(new BackgroundFill(Color.web("rgba(200,200,200)", 0.2), null, null));
        if (nextBtn.getBackground().equals(defaultBackground)) {
            nextBtn.setBackground(hoverBackground);
        } else {
            nextBtn.setBackground(defaultBackground);
        }
    }

    public void prevBtnClicked() {

    }

    public void nextBtnClicked() {

    }

    private void createLevelPane(String questionGenerator, GridPane gridPane, int x, int y) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/levelpane.fxml"));
        Parent pane = loader.load();
        loader.<LevelPaneController>getController().setQuestionGenerators(questionGenerator);
        loader.<LevelPaneController>getController().setParentNode(levelSelectorHbox);
        GridPane.setConstraints(pane, x, y);
        GridPane.setMargin(pane, new Insets(0, 10, 10, 10));
        gridPane.getChildren().add(pane);
    }

}
