package seedu.tutorlink.ui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Student;

/**
 * Panel showing the outstanding follow-ups of the student currently selected, earliest review date first.
 */
public class FollowUpPanel extends UiPart<Region> {
    public static final String MESSAGE_NO_STUDENT =
            "Use student view n/NAME to see a student's outstanding follow-ups.";
    public static final String MESSAGE_NONE_RECORDED = "No outstanding follow-ups for %1$s.";

    private static final String FXML = "FollowUpPanel.fxml";

    @FXML
    private Label placeholder;
    @FXML
    private ScrollPane entriesScrollPane;
    @FXML
    private VBox entries;

    /**
     * Creates a {@code FollowUpPanel} for whichever student {@code selectedStudent} holds.
     */
    public FollowUpPanel(ObservableValue<Student> selectedStudent) {
        super(FXML);
        show(selectedStudent.getValue());
        selectedStudent.addListener((observable, oldStudent, newStudent) -> show(newStudent));
    }

    /**
     * Returns the review date line shown above the description of {@code followUp}.
     */
    static String describeReview(FollowUp followUp) {
        return "Review " + followUp.getReviewDate();
    }

    private void show(Student student) {
        entries.getChildren().clear();
        boolean hasFollowUps = student != null && !student.getFollowUps().isEmpty();
        PanelEntries.setShown(placeholder, !hasFollowUps);
        PanelEntries.setShown(entriesScrollPane, hasFollowUps);
        if (!hasFollowUps) {
            placeholder.setText(student == null
                    ? MESSAGE_NO_STUDENT
                    : String.format(MESSAGE_NONE_RECORDED, student.getName()));
            return;
        }

        student.getFollowUps().stream()
                .sorted(FollowUp.REVIEW_DATE_ORDER)
                .map(followUp -> PanelEntries.createEntry(describeReview(followUp), followUp.getDescription()))
                .forEach(entries.getChildren()::add);
    }
}
