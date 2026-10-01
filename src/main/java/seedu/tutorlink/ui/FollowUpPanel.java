package seedu.tutorlink.ui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.tutorlink.model.student.Student;

/**
 * Panel showing the outstanding follow-ups of the student currently selected.
 * It shows a placeholder until outstanding follow-ups are stored (F5/F6).
 */
public class FollowUpPanel extends UiPart<Region> {
    public static final String MESSAGE_NO_STUDENT =
            "Use student view n/NAME to see a student's outstanding follow-ups.";
    public static final String MESSAGE_NONE_RECORDED = "No outstanding follow-ups for %1$s.";

    private static final String FXML = "FollowUpPanel.fxml";

    @FXML
    private Label placeholder;

    /**
     * Creates a {@code FollowUpPanel} for whichever student {@code selectedStudent} holds.
     */
    public FollowUpPanel(ObservableValue<Student> selectedStudent) {
        super(FXML);
        show(selectedStudent.getValue());
        selectedStudent.addListener((observable, oldStudent, newStudent) -> show(newStudent));
    }

    private void show(Student student) {
        placeholder.setText(student == null
                ? MESSAGE_NO_STUDENT
                : String.format(MESSAGE_NONE_RECORDED, student.getName()));
    }
}
