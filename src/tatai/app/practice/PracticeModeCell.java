package tatai.app.practice;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tatai.app.PracticeModeController;
import tatai.app.util.Record;
import tatai.app.util.Translator;

import java.io.IOException;

public class PracticeModeCell extends ListCell<Integer> {

    @FXML private Pane dataPane;
    @FXML private Label numberLabel;
    @FXML private Label answerLabel;
    @FXML private MaterialDesignIconView correctIcon;
    @FXML private MaterialDesignIconView incorrectIcon;
    @FXML private JFXButton recordBtn;
    @FXML private ProgressIndicator recordingProgress;

    private int number = 0;
    private FXMLLoader loader;
    private String answer;
    private PracticeModeController controller;

    public PracticeModeCell(PracticeModeController controller) {
        this.controller = controller;
    }

    @Override
    protected void updateItem(Integer entry, boolean empty) {
        super.updateItem(entry, empty);

        if (empty || entry == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loadFXML();
            }

            if (entry != number) {
                correctIcon.setVisible(false);
                incorrectIcon.setVisible(false);
                recordingProgress.setVisible(false);
                number = entry;
            }

            numberLabel.setText(entry.toString());
            answer = Translator.toMaori(entry);
            // do things

            setText(null);
            setGraphic(dataPane);

        }
    }

    private void loadFXML() {
        loader = new FXMLLoader(getClass().getResource("../resources/practicemodecell.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML void answerLabelClickEnd() {
        answerLabel.setTextFill(Color.GRAY);
        answerLabel.setText("Hover for answer");
    }

    @FXML void answerLabelClickStart() {
        answerLabel.setTextFill(Color.WHITE);
        answerLabel.setText(Translator.toDisplayable(answer));
    }

    @FXML void recordBtnPressed() {
        Timeline recordingProgressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(recordingProgress.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(recordingProgress.progressProperty(), 1))
        );
        correctIcon.setVisible(false);
        incorrectIcon.setVisible(false);
        recordingProgress.setVisible(true);
        Record answerRecording = new Record();
        answerRecording.setOnFinished(event -> {
            recordBtn.setDisable(false);
            recordingProgress.setVisible(false);
            checkAnswer(answerRecording.speechToText());
        });
        recordBtn.setDisable(true);
        recordingProgressTimeline.play();
        answerRecording.record(2000);
    }

    private void checkAnswer(String recordedAnswer) {
        if (recordedAnswer.contains(answer)) {
            // correct
            controller.questionAnswered(true);
            correctIcon.setVisible(true);
            incorrectIcon.setVisible(false);
        } else {
            controller.questionAnswered(false);
            correctIcon.setVisible(false);
            incorrectIcon.setVisible(true);
        }
    }


}
