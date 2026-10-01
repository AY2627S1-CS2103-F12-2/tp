package seedu.tutorlink.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    public static final String SUCCESS_STYLE_CLASS = "result-success";
    public static final String ERROR_STYLE_CLASS = "result-error";

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    /**
     * Shows {@code feedbackToUser} as the result of a successful command.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        setStyle(SUCCESS_STYLE_CLASS);
    }

    /**
     * Shows {@code errorMessage} as the result of a failed command.
     */
    public void setErrorToUser(String errorMessage) {
        requireNonNull(errorMessage);
        resultDisplay.setText(errorMessage);
        setStyle(ERROR_STYLE_CLASS);
    }

    private void setStyle(String styleClass) {
        resultDisplay.getStyleClass().removeAll(SUCCESS_STYLE_CLASS, ERROR_STYLE_CLASS);
        resultDisplay.getStyleClass().add(styleClass);
    }
}
