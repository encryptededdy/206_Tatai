package tatai.app;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import tatai.app.util.TransitionFactory;

public class LevelSelectorController {
    @FXML JFXButton prevBtn;
    @FXML JFXButton nextBtn;
    @FXML ImageView backgroundImage;
    @FXML HBox levelSelectorHbox;

    public void initialize() {
        backgroundImage.setImage(Main.background);
        levelSelectorHbox.setOpacity(0.0);

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
}
