package seedu.tutorlink.ui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.tutorlink.model.student.Student;

/**
 * Panel showing the interactions of the student currently selected.
 * It shows a placeholder until interactions are stored (F3/F4).
 */
public class InteractionPanel extends UiPart<Region> {
    public static final String MESSAGE_NO_STUDENT = "Use student view n/NAME to see a student's interactions.";
    public static final String MESSAGE_NONE_RECORDED = "No interactions recorded for %1$s yet.";

    private static final String FXML = "InteractionPanel.fxml";

    @FXML
    private Label placeholder;

    /**
     * Creates a {@code InteractionPanel} for whichever student {@code selectedStudent} holds.
     */
    public InteractionPanel(ObservableValue<Student> selectedStudent) {
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
