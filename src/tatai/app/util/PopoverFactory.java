package tatai.app.util;

import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.PopOver;

/**
 * Generates PopOver objects
 *
 * @author Edward
 */
public class PopoverFactory {
    /**
     * Generates a PopOver used for help text
     * @param text The text to show in the popover
     * @return The popover object
     */
    public static PopOver helpPopOver(String text) {
        Label inside = new Label(text);
        inside.setStyle("-fx-padding: 20 20 20 20");
        inside.setTextAlignment(TextAlignment.CENTER);
        PopOver popover = new PopOver(inside);
        popover.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
        popover.setDetachable(false);
        popover.setAutoHide(false);
        return popover;
    }
}
