package seedu.tutorlink.ui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Student;

/**
 * Panel showing the interactions of the student currently selected, oldest first.
 */
public class InteractionPanel extends UiPart<Region> {
    public static final String MESSAGE_NO_STUDENT = "Use student view n/NAME to see a student's interactions.";
    public static final String MESSAGE_NONE_RECORDED = "No interactions recorded for %1$s yet.";

    private static final String FXML = "InteractionPanel.fxml";

    @FXML
    private Label placeholder;
    @FXML
    private ScrollPane entriesScrollPane;
    @FXML
    private VBox entries;

    /**
     * Creates a {@code InteractionPanel} for whichever student {@code selectedStudent} holds.
     */
    public InteractionPanel(ObservableValue<Student> selectedStudent) {
        super(FXML);
        show(selectedStudent.getValue());
        selectedStudent.addListener((observable, oldStudent, newStudent) -> show(newStudent));
    }

    /**
     * Returns the date of {@code interaction}, followed by its time if one was recorded.
     */
    static String describeWhen(Interaction interaction) {
        return interaction.getDate() + interaction.getTime().map(time -> " " + time).orElse("");
    }

    private void show(Student student) {
        entries.getChildren().clear();
        boolean hasInteractions = student != null && !student.getInteractions().isEmpty();
        setShown(placeholder, !hasInteractions);
        setShown(entriesScrollPane, hasInteractions);
        if (!hasInteractions) {
            placeholder.setText(student == null
                    ? MESSAGE_NO_STUDENT
                    : String.format(MESSAGE_NONE_RECORDED, student.getName()));
            return;
        }

        student.getInteractions().stream()
                .sorted(Interaction.CHRONOLOGICAL_ORDER)
                .map(InteractionPanel::createEntry)
                .forEach(entries.getChildren()::add);
    }

    private static VBox createEntry(Interaction interaction) {
        Label when = new Label(describeWhen(interaction));
        when.getStyleClass().add("entry-when");
        Label note = new Label(interaction.getNote());
        note.getStyleClass().add("entry-text");
        note.setWrapText(true);

        VBox entry = new VBox(2, when, note);
        entry.getStyleClass().add("entry");
        return entry;
    }

    private static void setShown(Region region, boolean isShown) {
        region.setVisible(isShown);
        region.setManaged(isShown);
    }
}
