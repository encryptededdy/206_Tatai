package tatai.app.util;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import tatai.app.Main;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles recording audio and storing it in a wav, along with playback.
 * Recording logic is handled in a seperate thread
 *
 * @author Zach
 * @author Edward
 */
public class Record {
    private File recordingWav = new File(".tmp/foo.wav");
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private TargetDataLine line;
    private ArrayList<Node> nodeListeners = new ArrayList<Node>();
    private ArrayList<EventHandler<ActionEvent>> recordingeventHandlers = new ArrayList<>();
    private ArrayList<EventHandler<ActionEvent>> playbackeventHandlers = new ArrayList<>();

    /**
     * Define the Audio recording format (currently setup to match HTK input specifications)
     */
    private AudioFormat getAudioFormat() {
        float sampleRate = 22050;
        int sampleSize = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSize, channels, signed, bigEndian);

    }

    private void start() {
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

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    private void finishRecording() {
        line.stop();
        line.close();
        completeRecordingEvent();
    }

    /**
     * Begin the audio recording
     * @param duration  Duration of audio recording in ms
     */
    public void record(long duration) {
        Task<Void> stopRecording = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread startRecording = new Thread(this::start);

        stopRecording.setOnSucceeded(event -> finishRecording());
        startRecording.start();
        new Thread(stopRecording).start();
    }

    /**
     * Play back the audio recording using a JFX Media object. Record must be called first.
     */
    public void play() {
        Media recording = new Media(Paths.get(".tmp/foo.wav").toUri().toString());
        MediaPlayer recordingPlayer = new MediaPlayer(recording);
        recordingPlayer.setOnEndOfMedia(this::completePlaybackEvent);
        recordingPlayer.play();
    }

    /**
     * Add an event handler to be called at the completion of an audio recording
     * @param handler   the EventHandler to be called
     */
    public void setOnFinished(EventHandler<ActionEvent> handler) {
        recordingeventHandlers.add(handler);
    }

    /**
     * Add an event handler to be called at the completion of audio playback
     * @param handler   the EventHandler to be called
     */
    public void setOnPlaybackFinished(EventHandler<ActionEvent> handler) {
        playbackeventHandlers.add(handler);
    }

    private void completeRecordingEvent() {
        for (EventHandler<ActionEvent> handler : recordingeventHandlers) {
            handler.handle(new ActionEvent());
        }
    }

    private void completePlaybackEvent() {
        for (EventHandler<ActionEvent> handler : playbackeventHandlers) {
            handler.handle(new ActionEvent());
        }
    }

    /**
     * Uses HTK speech recognition to convert the audio recording to text
     * @return String represented the detected speech
     */
    public String speechToText() {
        String maoriText;
        ProcessBuilder translateSpeechToMaoriPB;
        if (Main.isWindows) {
            translateSpeechToMaoriPB = new ProcessBuilder(
                    "cmd", "/C", "HVite", "-H", "HMMs/hmm15/macros", "-H", "HMMs/hmm15/hmmdefs", "-C", "user/configLR", "-w", "user/wordNetworkNum", "-o", "SWT", "-l", "*", "-i", Paths.get(".tmp/recout.mlf").toAbsolutePath().toString(), "-p", "0.0", "-s", "5.0", "user/dictionaryD", "user/tiedList", Paths.get(".tmp/foo.wav").toAbsolutePath().toString());
        } else {
            translateSpeechToMaoriPB = new ProcessBuilder(
                    "HVite", "-H", "HMMs/hmm15/macros", "-H", "HMMs/hmm15/hmmdefs", "-C", "user/configLR", "-w", "user/wordNetworkNum", "-o", "SWT", "-l", "*", "-i", Paths.get(".tmp/recout.mlf").toAbsolutePath().toString(), "-p", "0.0", "-s", "5.0", "user/dictionaryD", "user/tiedList", Paths.get(".tmp/foo.wav").toAbsolutePath().toString());
        }
        File htkMaoriNumbersDirectory = new File("HTK/MaoriNumbers");
        translateSpeechToMaoriPB.directory(htkMaoriNumbersDirectory);

    try {
        Process translateSpeechToMaoriProcess = translateSpeechToMaoriPB.start();
        Scanner outputScanner = new Scanner(translateSpeechToMaoriProcess.getInputStream());
        //while (outputScanner.hasNext()) {
        //    System.out.println(outputScanner.next());
        //}
        translateSpeechToMaoriProcess.waitFor();
        FileReader translationFileReader = new FileReader(".tmp/recout.mlf");
        BufferedReader translationBufferedReader = new BufferedReader(translationFileReader);
        StringBuilder maoriTextBuilder = new StringBuilder();
        String line = null;

        while ((line = translationBufferedReader.readLine()) != null) {
            if (!line.equals("#!MLF!#") && !line.equals("\"*/foo.rec\"") && !line.equals("sil") && !line.equals(".")) {
                maoriTextBuilder.append(line);
                maoriTextBuilder.append(" ");
            }
        }

        maoriText = maoriTextBuilder.toString();
    } catch (IOException | InterruptedException ioe) {
        ioe.printStackTrace();
        throw new RuntimeException("HTK Execution error!");
    }
        return maoriText;
    }

    /**
     * Constructor has no parameters
     */
    public Record() {
    }
}
