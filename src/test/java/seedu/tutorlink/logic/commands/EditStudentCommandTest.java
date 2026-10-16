package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.CARL;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code EditStudentCommand}.
 */
public class EditStudentCommandTest {

    private static final Name NEW_NAME = new Name("Benson Lee");
    private static final List<Subject> NEW_SUBJECTS = List.of(new Subject("Chemistry"));

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_newName_renamesStudent() {
        Student edited = new Student(NEW_NAME, BENSON.getSubjects());
        Model expectedModel = new ModelManager(getTypicalTutorLink(), new UserPrefs());
        expectedModel.setStudent(BENSON, edited);
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_SUCCESS, NEW_NAME,
                Messages.formatSubjects(BENSON.getSubjects()));

        assertCommandSuccess(new EditStudentCommand(BENSON.getName(), Optional.of(NEW_NAME), Optional.empty()),
                model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_newSubjects_replacesAllSubjects() {
        Student edited = new Student(BENSON.getName(), NEW_SUBJECTS);
        Model expectedModel = new ModelManager(getTypicalTutorLink(), new UserPrefs());
        expectedModel.setStudent(BENSON, edited);
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_SUCCESS, BENSON.getName(), "Chemistry");

        assertCommandSuccess(new EditStudentCommand(BENSON.getName(), Optional.empty(), Optional.of(NEW_SUBJECTS)),
                model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_studentWithRecords_keepsInteractionsAndFollowUps() throws Exception {
        Student withRecords = BENSON
                .withInteraction(new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Algebra"))
                .withFollowUp(new FollowUp("Review algebra", LocalDate.of(2026, 10, 20)));
        model.setStudent(BENSON, withRecords);

        new EditStudentCommand(BENSON.getName(), Optional.of(NEW_NAME), Optional.of(NEW_SUBJECTS)).execute(model);

        Student edited = model.findStudentByName(NEW_NAME).orElseThrow();
        assertEquals(withRecords.getInteractions(), edited.getInteractions());
        assertEquals(withRecords.getFollowUps(), edited.getFollowUps());
    }

    @Test
    public void execute_caseOnlyRename_success() throws Exception {
        Name lowerCaseName = new Name("benson meier");
        new EditStudentCommand(BENSON.getName(), Optional.of(lowerCaseName), Optional.empty()).execute(model);
        assertEquals(lowerCaseName, model.findStudentByName(BENSON.getName()).orElseThrow().getName());
    }

    @Test
    public void execute_selectedStudentEdited_selectionUpdated() throws Exception {
        model.setSelectedStudent(BENSON);
        new EditStudentCommand(BENSON.getName(), Optional.of(NEW_NAME), Optional.empty()).execute(model);
        assertEquals(NEW_NAME, model.getSelectedStudent().get().getName());
    }

    @Test
    public void execute_renameToExistingStudent_throwsCommandException() {
        Name carlInOtherCase = new Name("carl kurz");
        assertCommandFailure(new EditStudentCommand(BENSON.getName(), Optional.of(carlInOtherCase), Optional.empty()),
                model, String.format(AddCommand.MESSAGE_DUPLICATE_STUDENT, CARL.getName()));
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        assertCommandFailure(new EditStudentCommand(new Name("John Tan"), Optional.of(NEW_NAME), Optional.empty()),
                model, String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "John Tan"));
    }

    @Test
    public void equals() {
        EditStudentCommand renameBenson =
                new EditStudentCommand(BENSON.getName(), Optional.of(NEW_NAME), Optional.empty());

        assertTrue(renameBenson.equals(renameBenson));
        assertTrue(renameBenson.equals(
                new EditStudentCommand(BENSON.getName(), Optional.of(NEW_NAME), Optional.empty())));
        assertFalse(renameBenson.equals(1));
        assertFalse(renameBenson.equals(null));
        assertFalse(renameBenson.equals(
                new EditStudentCommand(CARL.getName(), Optional.of(NEW_NAME), Optional.empty())));
        assertFalse(renameBenson.equals(
                new EditStudentCommand(BENSON.getName(), Optional.empty(), Optional.of(NEW_SUBJECTS))));
        assertFalse(renameBenson.equals(
                new EditStudentCommand(BENSON.getName(), Optional.of(NEW_NAME), Optional.of(NEW_SUBJECTS))));
    }

    @Test
    public void toStringMethod() {
        EditStudentCommand command = new EditStudentCommand(BENSON.getName(), Optional.of(NEW_NAME), Optional.empty());
        String expected = EditStudentCommand.class.getCanonicalName() + "{name=" + BENSON.getName()
                + ", newName=" + Optional.of(NEW_NAME) + ", newSubjects=" + Optional.empty() + "}";
        assertEquals(expected, command.toString());
    }
}
