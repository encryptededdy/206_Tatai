package tatai.app.util;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Generates Developer Quotes on a label and animates it
 */
public class DevQuotes {
    // The quotes to select from
    private final static ArrayList<String> quotes = new ArrayList<>(Arrays.asList(
            "Doesn't work in Java 9!",
            "Licensed under GPL (except HTK)",
            "Unresizable!",
            "Everything is an AnchorPane"
            ));

    /**
     * Generate a quote and animate the label
     * @param quoteLabel The label to write to & animate
     */
    public static void generateQuote(Label quoteLabel) {
        Random rng = new Random();
        String quote = quotes.get(rng.nextInt(quotes.size()));
        quoteLabel.setText(quote);
        ScaleTransition st = new ScaleTransition(Duration.millis(500), quoteLabel);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setCycleCount(Animation.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
    }
}
