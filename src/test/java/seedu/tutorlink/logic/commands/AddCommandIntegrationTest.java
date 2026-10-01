package seedu.tutorlink.logic.commands;

import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.testutil.StudentBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalTutorLink(), new UserPrefs());
    }

    @Test
    public void execute_newStudentWithoutSubjects_success() {
        Student validStudent = new StudentBuilder().build();

        Model expectedModel = new ModelManager(model.getTutorLink(), new UserPrefs());
        expectedModel.addStudent(validStudent);

        String expectedMessage = "\u2714 Student added: Amy Bee (no subjects recorded)\nTotal students: 8";
        assertCommandSuccess(new AddCommand(validStudent), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_newStudentWithSubjects_success() {
        Student validStudent = new StudentBuilder().withName("John Tan").withSubjects("Math", "Physics").build();

        Model expectedModel = new ModelManager(model.getTutorLink(), new UserPrefs());
        expectedModel.addStudent(validStudent);

        String expectedMessage = "\u2714 Student added: John Tan (subjects: Math, Physics)\nTotal students: 8";
        assertCommandSuccess(new AddCommand(validStudent), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateStudent_throwsCommandException() {
        Student studentInList = model.getTutorLink().getStudentList().get(0);
        assertCommandFailure(new AddCommand(studentInList), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_STUDENT, studentInList.getName()));
    }

}
