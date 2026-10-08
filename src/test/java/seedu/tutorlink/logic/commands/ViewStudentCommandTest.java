package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

public class ViewStudentCommandTest {

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_existingStudent_showsNameAndSubjects() {
        String expectedMessage = String.format(ViewStudentCommand.MESSAGE_SUCCESS, "Benson Meier", "Physics, Math");
        assertCommandSuccess(new ViewStudentCommand(BENSON.getName()), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameInDifferentCase_showsStoredName() {
        String expectedMessage = String.format(ViewStudentCommand.MESSAGE_SUCCESS, "Benson Meier", "Physics, Math");
        assertCommandSuccess(new ViewStudentCommand(new Name("benson MEIER")), model, expectedMessage,
                expectedModel);
    }

    @Test
    public void execute_studentWithoutSubjects_showsNoneRecorded() {
        String expectedMessage = String.format(ViewStudentCommand.MESSAGE_SUCCESS, "Carl Kurz", "(none recorded)");
        assertCommandSuccess(new ViewStudentCommand(CARL.getName()), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_existingStudent_selectsStudent() throws Exception {
        new ViewStudentCommand(new Name("benson meier")).execute(model);
        assertEquals(BENSON, model.getSelectedStudent().get());
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        assertCommandFailure(new ViewStudentCommand(new Name("John Tan")), model,
                String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "John Tan"));
    }

    @Test
    public void equals() {
        ViewStudentCommand viewBenson = new ViewStudentCommand(BENSON.getName());
        ViewStudentCommand viewCarl = new ViewStudentCommand(CARL.getName());

        assertTrue(viewBenson.equals(viewBenson)); // same object
        assertTrue(viewBenson.equals(new ViewStudentCommand(BENSON.getName()))); // same values
        assertFalse(viewBenson.equals(1)); // different types
        assertFalse(viewBenson.equals(null)); // null
        assertFalse(viewBenson.equals(viewCarl)); // different student
    }

    @Test
    public void toStringMethod() {
        ViewStudentCommand command = new ViewStudentCommand(BENSON.getName());
        String expected = ViewStudentCommand.class.getCanonicalName() + "{name=" + BENSON.getName() + "}";
        assertEquals(expected, command.toString());
    }
}
