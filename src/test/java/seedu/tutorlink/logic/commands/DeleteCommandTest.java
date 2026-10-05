package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.CARL;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.student.Name;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_existingStudent_success() {
        Model expectedModel = new ModelManager(getTypicalTutorLink(), new UserPrefs());
        expectedModel.deleteStudent(BENSON);
        String expectedMessage = String.format(DeleteCommand.MESSAGE_SUCCESS, BENSON.getName(),
                expectedModel.getTutorLink().getStudentList().size());

        assertCommandSuccess(new DeleteCommand(BENSON.getName()), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameInDifferentCase_deletesStoredStudent() throws Exception {
        new DeleteCommand(new Name("benson MEIER")).execute(model);
        assertFalse(model.hasStudent(BENSON));
    }

    @Test
    public void execute_selectedStudentDeleted_selectionCleared() throws Exception {
        model.setSelectedStudent(BENSON);
        new DeleteCommand(BENSON.getName()).execute(model);
        assertNull(model.getSelectedStudent().get());
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        assertCommandFailure(new DeleteCommand(new Name("John Tan")), model,
                String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "John Tan"));
    }

    @Test
    public void equals() {
        DeleteCommand deleteBenson = new DeleteCommand(BENSON.getName());
        DeleteCommand deleteCarl = new DeleteCommand(CARL.getName());

        assertTrue(deleteBenson.equals(deleteBenson)); // same object
        assertTrue(deleteBenson.equals(new DeleteCommand(BENSON.getName()))); // same values
        assertFalse(deleteBenson.equals(1)); // different types
        assertFalse(deleteBenson.equals(null)); // null
        assertFalse(deleteBenson.equals(deleteCarl)); // different student
    }

    @Test
    public void toStringMethod() {
        DeleteCommand command = new DeleteCommand(BENSON.getName());
        String expected = DeleteCommand.class.getCanonicalName() + "{name=" + BENSON.getName() + "}";
        assertEquals(expected, command.toString());
    }
}
