package seedu.tutorlink.ui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.tutorlink.model.student.Student;

/**
 * Panel showing the name and subjects of the student currently selected (e.g. by {@code student view}).
 */
public class StudentDetailPanel extends UiPart<Region> {
    private static final String FXML = "StudentDetailPanel.fxml";

    @FXML
    private Label placeholder;
    @FXML
    private HBox details;
    @FXML
    private Label initials;
    @FXML
    private Label name;
    @FXML
    private FlowPane subjects;

    /**
     * Creates a {@code StudentDetailPanel} that shows whichever student {@code selectedStudent} holds.
     */
    public StudentDetailPanel(ObservableValue<Student> selectedStudent) {
        super(FXML);
        show(selectedStudent.getValue());
        selectedStudent.addListener((observable, oldStudent, newStudent) -> show(newStudent));
    }

    private void show(Student student) {
        boolean hasStudent = student != null;
        setShown(placeholder, !hasStudent);
        setShown(details, hasStudent);
        subjects.getChildren().clear();
        if (!hasStudent) {
            return;
        }

        initials.setText(StudentCard.getInitials(student.getName().fullName));
        name.setText(student.getName().fullName);
        student.getSubjects().forEach(subject -> {
            Label chip = new Label(subject.subjectName);
            chip.getStyleClass().add("subject-chip");
            subjects.getChildren().add(chip);
        });
    }

    private static void setShown(Region region, boolean isShown) {
        region.setVisible(isShown);
        region.setManaged(isShown);
    }
}
