package tatai.app.util;

import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Record {
    private File recordingWav = new File("foo.wav");
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private TargetDataLine line;
    private ArrayList<Node> nodeListeners = new ArrayList<Node>();

    private AudioFormat getAudioFormat() {
        float sampleRate = 22050;
        int sampleSize = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        AudioFormat format = new AudioFormat(sampleRate, sampleSize, channels, signed, bigEndian);

        return format;

    }

    private void start() {
        System.out.println("start called");
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException();
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            AudioInputStream audioInputStream = new AudioInputStream(line);
            AudioSystem.write(audioInputStream, fileType, recordingWav);


        } catch (LineUnavailableException e) {
            System.out.println("LineUnavailableException thrown");
        } catch (IOException ioe) {
            System.out.println("IOException thrown");

        }
    }

    private void finishRecording() {
        line.stop();
        line.close();
        enableNodes();
    }

    public void record(long duration) {
        System.out.println("record called");
        disableNodes();
        Thread stopRecording = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {

                }
                System.out.println("record finished");
                finishRecording();
            }
        });

        Thread startRecording = new Thread(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });

        stopRecording.start();
        startRecording.start();
    }

    public void play() {
        Media recording = new Media(Paths.get("foo.wav").toUri().toString());
        MediaPlayer recordingPlayer = new MediaPlayer(recording);
        recordingPlayer.play();
    }

    public void addNodeListener(Node node) {
        nodeListeners.add(node);
    }

    private void disableNodes() {
        for (Node node : nodeListeners) {
            node.setDisable(true);
        }
    }

    private void enableNodes() {
        for (Node node : nodeListeners) {
            node.setDisable(false);
        }
    }

    public Record() {
    }
}
