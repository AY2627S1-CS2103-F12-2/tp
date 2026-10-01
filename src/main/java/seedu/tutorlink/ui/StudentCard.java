package seedu.tutorlink.ui;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.student.Student;

/**
 * A UI component that displays information of a {@code Student} in the student index.
 */
public class StudentCard extends UiPart<Region> {

    private static final String FXML = "StudentListCard.fxml";
    private static final int MAX_INITIALS = 2;

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Student student;

    @FXML
    private HBox cardPane;
    @FXML
    private Label initials;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label subjects;

    /**
     * Creates a {@code StudentCard} with the given {@code Student} and index to display.
     */
    public StudentCard(Student student, int displayedIndex) {
        super(FXML);
        this.student = student;
        id.setText(displayedIndex + ".");
        initials.setText(getInitials(student.getName().fullName));
        name.setText(student.getName().fullName);
        subjects.setText(student.getSubjects().isEmpty()
                ? Messages.MESSAGE_NO_SUBJECTS
                : Messages.formatSubjects(student.getSubjects()));
    }

    /**
     * Returns up to two initials of {@code fullName}, taken from the first letters of its first two words.
     */
    static String getInitials(String fullName) {
        return Arrays.stream(fullName.trim().split("\\s+"))
                .limit(MAX_INITIALS)
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT))
                .collect(Collectors.joining());
    }
}
