package tatai.app.util.store;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tatai.app.Main;
import tatai.app.util.Layout;

import java.io.IOException;

public class BalanceView {
    @FXML Label balanceLabel;
    @FXML Pane mainPane;

    private int currentBalance;

    private int targetBalance;

    private Timeline counter;

    public BalanceView() {
        FXMLLoader loader = Layout.BALANCESTUB.loader();
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentBalance = Main.store.getBalance();
        // Setup counter
        counter = new Timeline(new KeyFrame(Duration.millis(0), event -> count()), new KeyFrame(Duration.seconds(1.0/60.0)));
        counter.setCycleCount(Animation.INDEFINITE);
        updateFromBalance();
    }

    public Pane getPane() {
        return mainPane;
    }

    public void updateBalance() {
        targetBalance = Main.store.getBalance();
        counter.play();
    }

    private void count() {
        if (targetBalance > currentBalance) {
            if (targetBalance - currentBalance > 100) {
                currentBalance+=10; // go fast
            }
            currentBalance++;
        } else if (targetBalance < currentBalance) {
            currentBalance--;
        } else {
            counter.stop();
        }
        updateFromBalance();
    }

    private void updateFromBalance() {
        balanceLabel.setText(Integer.toString(currentBalance));
    }
}
