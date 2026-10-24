package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.storage.JsonTutorLinkStorage;

public class CancelFollowUpCommandTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 20);
    @TempDir
    public Path testFolder;

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_sortedOutstandingIndex_preservesRecordsAndUpdatesSelection() {
        FollowUp later = new FollowUp("Later", DATE.plusDays(1));
        FollowUp done = new FollowUp("Completed history", DATE.minusDays(1), FollowUpStatus.DONE);
        FollowUp cancelled = new FollowUp("Cancelled history", DATE.minusDays(1), FollowUpStatus.CANCELLED);
        FollowUp first = new FollowUp("First", DATE);
        FollowUp second = new FollowUp("Second", DATE);
        Interaction interaction = new Interaction(DATE, Optional.empty(), "Lesson");
        Student student = BENSON.withInteraction(interaction).withFollowUp(later).withFollowUp(done)
                .withFollowUp(cancelled).withFollowUp(first).withFollowUp(second);
        model.setStudent(BENSON, student);
        model.setSelectedStudent(student);
        Student expectedStudent = new Student(student.getName(), student.getSubjects(), student.getInteractions(),
                List.of(later, done, cancelled, first, second.withStatus(FollowUpStatus.CANCELLED)));
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        expectedModel.setSelectedStudent(student);
        expectedModel.setStudent(student, expectedStudent);

        String message = String.format(CancelFollowUpCommand.MESSAGE_SUCCESS, student.getName(), DATE, "Second", 2);
        assertCommandSuccess(new CancelFollowUpCommand(new Name(student.getName().fullName.toLowerCase()),
                Index.fromOneBased(2)), model, message, expectedModel);
        assertEquals(expectedStudent, model.getSelectedStudent().getValue());
        assertEquals(List.of(first, later), expectedStudent.getOutstandingFollowUps());
        assertEquals(List.of(later, done, cancelled, first, second), student.getFollowUps());
    }

    @Test
    public void execute_unknownStudentOrOutOfRange_noDataChanged() {
        assertCommandFailure(new CancelFollowUpCommand(new Name("Missing Student"), Index.fromOneBased(1)), model,
                String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "Missing Student"));
        assertCommandFailure(new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(1)), model,
                "Benson Meier has no outstanding follow-ups.");
        model.setStudent(BENSON, BENSON.withFollowUp(new FollowUp("History", DATE, FollowUpStatus.CANCELLED))
                .withFollowUp(new FollowUp("Active", DATE)));
        assertCommandFailure(new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(2)), model,
                "Index 2 is out of range: Benson Meier's outstanding follow-ups are numbered 1 to 1.");
    }

    @Test
    public void execute_lastOutstandingFollowUp_retainsHistoryAfterReload() throws Exception {
        FollowUp followUp = new FollowUp("Review algebra", DATE);
        model.setStudent(BENSON, BENSON.withFollowUp(followUp));
        CancelFollowUpCommand command = new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(1));
        command.execute(model);
        assertCommandFailure(command, model, "Benson Meier has no outstanding follow-ups.");
        assertEquals(List.of(), model.findStudentByName(BENSON.getName()).orElseThrow().getOutstandingFollowUps());

        Path path = testFolder.resolve("cancelled.json");
        new JsonTutorLinkStorage(path).saveTutorLink(model.getTutorLink());
        Model restored = new ModelManager(new JsonTutorLinkStorage(path).readTutorLink().orElseThrow(),
                new UserPrefs());
        assertEquals(List.of(followUp.withStatus(FollowUpStatus.CANCELLED)),
                restored.findStudentByName(BENSON.getName()).orElseThrow().getFollowUps());
        assertEquals(List.of(), restored.findStudentByName(BENSON.getName()).orElseThrow().getOutstandingFollowUps());
    }

    @Test
    public void execute_cancelThenAddSameFollowUp_acceptsNewOutstandingAndRejectsDuplicate() throws Exception {
        FollowUp followUp = new FollowUp("Review algebra", DATE);
        model.setStudent(BENSON, BENSON.withFollowUp(followUp));
        new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(1)).execute(model);

        AddFollowUpCommand addCommand = new AddFollowUpCommand(BENSON.getName(), followUp);
        addCommand.execute(model);
        Student updated = model.findStudentByName(BENSON.getName()).orElseThrow();
        assertEquals(List.of(followUp.withStatus(FollowUpStatus.CANCELLED), followUp), updated.getFollowUps());
        assertEquals(List.of(followUp), updated.getOutstandingFollowUps());
        assertCommandFailure(addCommand, model, AddFollowUpCommand.MESSAGE_DUPLICATE_FOLLOW_UP);
    }

    @Test
    public void constructorAndExecute_nullValues_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CancelFollowUpCommand(null, Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () -> new CancelFollowUpCommand(BENSON.getName(), null));
        assertThrows(NullPointerException.class, () ->
                new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(1)).execute(null));
    }

    @Test
    public void equalsAndToString() {
        CancelFollowUpCommand command = new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(1));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("followup cancel"));
        assertFalse(command.equals(new DoneFollowUpCommand(BENSON.getName(), Index.fromOneBased(1))));
        assertFalse(command.equals(new CancelFollowUpCommand(new Name("Other Student"), Index.fromOneBased(1))));
        assertFalse(command.equals(new CancelFollowUpCommand(BENSON.getName(), Index.fromOneBased(2))));
        assertEquals(CancelFollowUpCommand.class.getCanonicalName() + "{name=" + BENSON.getName() + ", index="
                + Index.fromOneBased(1) + "}", command.toString());
    }
}
