package tatai.app.util.factories;

import com.jfoenix.controls.JFXDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Generates dialog boxes for various uses...
 *
 * @author Edward
 */
public class DialogFactory {

    /**
     * Displays an error message with a stack trace.
     *
     * Source: http://code.makery.ch/blog/javafx-dialogs-official/
     * @param content The error message
     * @param title The title of the dialog box
     * @param e The exception
     */
    public static void exception(String content, String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Encountered");
        alert.setContentText(content);
        alert.setHeaderText(title);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();

    }

    /**
     * Generates a Material design dialog
     * @param container The StackPane to display the dialog in
     * @param heading The heading of the dialog box
     * @param body The body of the dialog box
     * @param btn1 The first button (Left)
     * @param btn2 The second button (Right)
     * @return The JFXDialog object
     */
    public static JFXDialog mdDialog(StackPane container, String heading, String body, Button btn1, Button btn2) {
        VBox dialogLayout = new VBox();
        dialogLayout.setPadding(new Insets(24));

        // Heading Text
        Label headingLabel = new Label(heading);
        headingLabel.setStyle("-fx-font: 18 \"Roboto Bold\"");
        VBox.setMargin(headingLabel,  new Insets(0));

        // Body Text
        Label bodyLabel = new Label(body);
        bodyLabel.setStyle("-fx-font: 16 \"Roboto\"; -fx-text-fill: grey;");
        VBox.setMargin(bodyLabel, new Insets(20, 0, 0, 0));

        // Buttons
        HBox buttons = new HBox(18);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(buttons, new Insets(32, 0, 0, 0));
        btn1.setStyle("-fx-font: 16 \"Roboto Bold\"; -fx-text-fill: #3F51B5;");
        btn2.setStyle("-fx-font: 16 \"Roboto Bold\"; -fx-text-fill: #3F51B5;");
        buttons.getChildren().addAll(btn1, btn2);

        dialogLayout.getChildren().addAll(headingLabel, bodyLabel, buttons);

        return new JFXDialog(container, dialogLayout, JFXDialog.DialogTransition.CENTER);
    }
}
