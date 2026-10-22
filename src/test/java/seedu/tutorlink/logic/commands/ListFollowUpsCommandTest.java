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
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

public class ListFollowUpsCommandTest {

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_mixedStatuses_numbersOnlyOutstandingFollowUps() {
        Student student = BENSON.withFollowUp(new FollowUp("Done", LocalDate.of(2026, 10, 1), FollowUpStatus.DONE))
                .withFollowUp(new FollowUp("Cancelled", LocalDate.of(2026, 10, 2), FollowUpStatus.CANCELLED))
                .withFollowUp(new FollowUp("Outstanding", LocalDate.of(2026, 10, 3)));
        model.setStudent(BENSON, student);
        Model expected = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        assertCommandSuccess(new ListFollowUpsCommand(student.getName()), model,
                "\u2714 Outstanding follow-ups for " + student.getName() + " (1):\n1. 2026-10-03 — Outstanding",
                expected);
    }

    @Test
    public void execute_onlyHistoricalFollowUps_informationalMessage() {
        Student student = BENSON.withFollowUp(new FollowUp("Done", LocalDate.of(2026, 10, 1), FollowUpStatus.DONE));
        model.setStudent(BENSON, student);
        Model expected = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        assertCommandSuccess(new ListFollowUpsCommand(student.getName()), model,
                String.format(ListFollowUpsCommand.MESSAGE_NONE_RECORDED, student.getName()), expected);
    }

    @Test
    public void execute_unsortedFollowUps_sortedOutputWithoutChangingRecords() {
        FollowUp later = new FollowUp("Later review", LocalDate.of(2026, 10, 15));
        FollowUp first = new FollowUp("First added", LocalDate.of(2026, 10, 14));
        FollowUp second = new FollowUp("Second added", LocalDate.of(2026, 10, 14));
        Student student = BENSON.withFollowUp(later).withFollowUp(first).withFollowUp(second);
        model.setStudent(BENSON, student);
        model.setSelectedStudent(student);
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        expectedModel.setSelectedStudent(student);
        String expectedMessage = "\u2714 Outstanding follow-ups for " + BENSON.getName() + " (3):\n"
                + "1. 2026-10-14 — First added\n"
                + "2. 2026-10-14 — Second added\n"
                + "3. 2026-10-15 — Later review";

        assertCommandSuccess(new ListFollowUpsCommand(new Name(BENSON.getName().fullName.toLowerCase())),
                model, expectedMessage, expectedModel);
        assertEquals(List.of(later, first, second),
                model.findStudentByName(BENSON.getName()).orElseThrow().getFollowUps());
        assertEquals(student, model.getSelectedStudent().getValue());
    }

    @Test
    public void execute_noFollowUps_informationalMessage() {
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        assertCommandSuccess(new ListFollowUpsCommand(BENSON.getName()), model,
                "\u2139 No outstanding follow-ups for " + BENSON.getName() + ".", expectedModel);
    }

    @Test
    public void execute_unknownStudent_noDataChanged() {
        assertCommandFailure(new ListFollowUpsCommand(new Name("Unknown Student")), model,
                String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "Unknown Student"));
    }

    @Test
    public void execute_pastTodayAndFutureFollowUps_allOutstanding() {
        LocalDate today = LocalDate.now();
        Student student = BENSON.withFollowUp(new FollowUp("Past", today.minusDays(1)))
                .withFollowUp(new FollowUp("Today", today))
                .withFollowUp(new FollowUp("Future", today.plusDays(1)));
        model.setStudent(BENSON, student);
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        String message = "\u2714 Outstanding follow-ups for " + BENSON.getName() + " (3):\n"
                + "1. " + today.minusDays(1) + " — Past\n"
                + "2. " + today + " — Today\n"
                + "3. " + today.plusDays(1) + " — Future";
        assertCommandSuccess(new ListFollowUpsCommand(BENSON.getName()), model, message, expectedModel);
    }

    @Test
    public void constructorAndExecute_nullValues_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListFollowUpsCommand(null));
        assertThrows(NullPointerException.class, () -> new ListFollowUpsCommand(BENSON.getName()).execute(null));
    }

    @Test
    public void equalsAndToString() {
        ListFollowUpsCommand command = new ListFollowUpsCommand(BENSON.getName());
        assertTrue(command.equals(command));
        assertTrue(command.equals(new ListFollowUpsCommand(BENSON.getName())));
        assertFalse(command.equals(null));
        assertFalse(command.equals("followup list"));
        assertFalse(command.equals(new ListFollowUpsCommand(new Name("Other Student"))));
        assertEquals(ListFollowUpsCommand.class.getCanonicalName() + "{name=" + BENSON.getName() + "}",
                command.toString());
    }
}
