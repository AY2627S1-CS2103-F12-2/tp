package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

public class AddFollowUpCommandTest {

    private static final FollowUp FOLLOW_UP = new FollowUp("Review algebra", LocalDate.of(2026, 10, 15));
    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_existingStudent_preservesRecordsAndUpdatesSelection() {
        Student student = BENSON.withInteraction(new Interaction(LocalDate.of(2026, 10, 1),
                Optional.empty(), "Revision lesson")).withFollowUp(
                        new FollowUp("Review fractions", LocalDate.of(2026, 10, 14)));
        model.setStudent(BENSON, student);
        model.setSelectedStudent(student);
        Student updatedStudent = student.withFollowUp(FOLLOW_UP);
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        expectedModel.setSelectedStudent(student);
        expectedModel.setStudent(student, updatedStudent);
        AddFollowUpCommand command = new AddFollowUpCommand(new Name(student.getName().fullName.toLowerCase()),
                FOLLOW_UP);
        String message = String.format(AddFollowUpCommand.MESSAGE_SUCCESS, student.getName(),
                FOLLOW_UP.getReviewDate(), FOLLOW_UP.getDescription(), 2);

        assertCommandSuccess(command, model, message, expectedModel);
        assertEquals(updatedStudent, model.getSelectedStudent().getValue());
    }

    @Test
    public void execute_unknownStudent_noDataChanged() {
        assertCommandFailure(new AddFollowUpCommand(new Name("Unknown Student"), FOLLOW_UP), model,
                String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "Unknown Student"));
    }

    @Test
    public void execute_exactDuplicate_noDataChanged() {
        model.setStudent(BENSON, BENSON.withFollowUp(FOLLOW_UP));
        assertCommandFailure(new AddFollowUpCommand(BENSON.getName(), new FollowUp("  Review algebra  ",
                FOLLOW_UP.getReviewDate())), model, AddFollowUpCommand.MESSAGE_DUPLICATE_FOLLOW_UP);
    }

    @Test
    public void execute_differentCaseOrDate_allowed() throws Exception {
        model.setStudent(BENSON, BENSON.withFollowUp(FOLLOW_UP));
        new AddFollowUpCommand(BENSON.getName(), new FollowUp("review algebra", FOLLOW_UP.getReviewDate()))
                .execute(model);
        new AddFollowUpCommand(BENSON.getName(), new FollowUp("Review algebra", FOLLOW_UP.getReviewDate().plusDays(1)))
                .execute(model);
        assertEquals(3, model.findStudentByName(BENSON.getName()).orElseThrow().getFollowUps().size());
    }

    @Test
    public void execute_pastTodayAndFutureDates_allowed() throws Exception {
        LocalDate today = LocalDate.now();
        for (LocalDate date : new LocalDate[]{today.minusDays(1), today, today.plusDays(1)}) {
            new AddFollowUpCommand(BENSON.getName(), new FollowUp("Review algebra", date)).execute(model);
        }
        assertEquals(3, model.findStudentByName(BENSON.getName()).orElseThrow().getFollowUps().size());
    }

    @Test
    public void constructorAndExecute_nullValues_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddFollowUpCommand(null, FOLLOW_UP));
        assertThrows(NullPointerException.class, () -> new AddFollowUpCommand(BENSON.getName(), null));
        assertThrows(NullPointerException.class, () ->
                new AddFollowUpCommand(BENSON.getName(), FOLLOW_UP).execute(null));
    }

    @Test
    public void equalsAndToString() {
        AddFollowUpCommand command = new AddFollowUpCommand(BENSON.getName(), FOLLOW_UP);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddFollowUpCommand(BENSON.getName(), FOLLOW_UP)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("followup add"));
        assertFalse(command.equals(new AddFollowUpCommand(new Name("Other Student"), FOLLOW_UP)));
        assertFalse(command.equals(new AddFollowUpCommand(BENSON.getName(),
                new FollowUp("Review fractions", FOLLOW_UP.getReviewDate()))));
        assertEquals(AddFollowUpCommand.class.getCanonicalName() + "{name=" + BENSON.getName()
                + ", followUp=" + FOLLOW_UP + "}", command.toString());
    }
}
