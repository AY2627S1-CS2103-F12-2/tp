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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

public class EditInteractionCommandTest {

    private static final Interaction EARLIER = new Interaction(LocalDate.of(2026, 10, 1),
            Optional.of(LocalTime.of(9, 0)), "Original note");
    private static final Interaction LATER = new Interaction(LocalDate.of(2026, 10, 2),
            Optional.empty(), "Later note");

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_allFields_preservesOtherDataAndSelection() {
        FollowUp followUp = new FollowUp("Send worksheet", LocalDate.of(2026, 10, 4));
        Student student = BENSON.withInteraction(LATER).withInteraction(EARLIER).withFollowUp(followUp);
        model.setStudent(BENSON, student);
        model.setSelectedStudent(student);
        Interaction edited = new Interaction(LocalDate.of(2026, 10, 3),
                Optional.of(LocalTime.of(14, 30)), "Corrected note");
        Student expectedStudent = new Student(student.getName(), student.getSubjects(),
                List.of(LATER, edited), student.getFollowUps());
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        expectedModel.setSelectedStudent(student);
        expectedModel.setStudent(student, expectedStudent);
        EditInteractionCommand command = new EditInteractionCommand(student.getName(), Index.fromOneBased(1),
                Optional.of(edited.getDate()), edited.getTime(), Optional.of(edited.getNote()));
        String message = String.format(EditInteractionCommand.MESSAGE_SUCCESS, student.getName(),
                Messages.formatInteraction(EARLIER), Messages.formatInteraction(edited));

        assertCommandSuccess(command, model, message, expectedModel);
        assertEquals(expectedStudent, model.getSelectedStudent().getValue());
        assertEquals(List.of(LATER, EARLIER), student.getInteractions());
    }

    @Test
    public void execute_noteOnly_preservesDateAndTime() throws Exception {
        Student student = BENSON.withInteraction(EARLIER);
        model.setStudent(BENSON, student);
        EditInteractionCommand command = new EditInteractionCommand(student.getName(), Index.fromOneBased(1),
                Optional.empty(), Optional.empty(), Optional.of("New note"));
        command.execute(model);
        Interaction actual = model.findStudentByName(student.getName()).orElseThrow().getInteractions().getFirst();
        assertEquals(EARLIER.getDate(), actual.getDate());
        assertEquals(EARLIER.getTime(), actual.getTime());
        assertEquals("New note", actual.getNote());
    }

    @Test
    public void execute_emptyTimeEdit_removesTimeAndPreservesOtherFields() throws Exception {
        Student student = BENSON.withInteraction(EARLIER);
        model.setStudent(BENSON, student);
        EditInteractionCommand command = new EditInteractionCommand(student.getName(), Index.fromOneBased(1),
                Optional.empty(), Optional.empty(), true, Optional.empty());

        command.execute(model);

        Interaction actual = model.findStudentByName(student.getName()).orElseThrow().getInteractions().getFirst();
        assertEquals(EARLIER.getDate(), actual.getDate());
        assertTrue(actual.getTime().isEmpty());
        assertEquals(EARLIER.getNote(), actual.getNote());
    }

    @Test
    public void execute_compoundEdit_updatesSelectedInteractionAndPreservesSiblings() throws Exception {
        Student student = BENSON.withInteraction(LATER).withInteraction(EARLIER);
        model.setStudent(BENSON, student);
        model.setSelectedStudent(student);
        Interaction edited = new Interaction(LocalDate.of(2026, 10, 4),
                Optional.of(LocalTime.of(16, 45)), "Reviewed algebra");
        EditInteractionCommand command = new EditInteractionCommand(student.getName(), Index.fromOneBased(1),
                Optional.of(edited.getDate()), edited.getTime(), Optional.of(edited.getNote()));

        command.execute(model);

        Student actual = model.findStudentByName(student.getName()).orElseThrow();
        assertEquals(List.of(LATER, edited), actual.getInteractions());
        assertEquals(actual, model.getSelectedStudent().getValue());
    }

    @Test
    public void execute_futureDate_successWithWarning() {
        Student student = BENSON.withInteraction(EARLIER);
        model.setStudent(BENSON, student);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Interaction edited = new Interaction(futureDate, EARLIER.getTime(), EARLIER.getNote());
        Student expectedStudent = student.withInteractionAtChronologicalIndex(0, edited);
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        Student expectedTarget = expectedModel.findStudentByName(BENSON.getName()).orElseThrow();
        expectedModel.setStudent(expectedTarget, expectedStudent);
        EditInteractionCommand command = new EditInteractionCommand(student.getName(), Index.fromOneBased(1),
                Optional.of(futureDate), Optional.empty(), Optional.empty());
        String message = String.format(Messages.MESSAGE_FUTURE_INTERACTION_DATE_WARNING,
                futureDate, LocalDate.now()) + String.format(EditInteractionCommand.MESSAGE_SUCCESS,
                student.getName(), Messages.formatInteraction(EARLIER), Messages.formatInteraction(edited));

        assertCommandSuccess(command, model, message, expectedModel);
    }

    @Test
    public void execute_unchangedValues_throwsCommandException() {
        Student student = BENSON.withInteraction(LATER);
        model.setStudent(BENSON, student);
        String message = String.format(EditInteractionCommand.MESSAGE_NO_CHANGE, 1, student.getName());

        assertCommandFailure(new EditInteractionCommand(student.getName(), Index.fromOneBased(1),
                Optional.empty(), Optional.empty(), true, Optional.empty()), model, message);
        assertCommandFailure(new EditInteractionCommand(student.getName(), Index.fromOneBased(1),
                Optional.of(LATER.getDate()), Optional.empty(), Optional.of(LATER.getNote())), model, message);
    }

    @Test
    public void execute_unknownStudentOrOutOfRange_throwsCommandException() {
        EditInteractionCommand missing = new EditInteractionCommand(new Name("Missing Student"),
                Index.fromOneBased(1), Optional.empty(), Optional.empty(), Optional.of("New"));
        assertCommandFailure(missing, model,
                String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "Missing Student"));
        EditInteractionCommand outOfRange = new EditInteractionCommand(BENSON.getName(),
                Index.fromOneBased(1), Optional.empty(), Optional.empty(), Optional.of("New"));
        assertCommandFailure(outOfRange, model, "Benson Meier has no interactions.");
    }

    @Test
    public void constructorAndEquals() {
        Index index = Index.fromOneBased(1);
        EditInteractionCommand command = new EditInteractionCommand(BENSON.getName(), index,
                Optional.empty(), Optional.empty(), Optional.of("New"));
        assertThrows(IllegalArgumentException.class, () -> new EditInteractionCommand(BENSON.getName(), index,
                Optional.empty(), Optional.empty(), Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> new EditInteractionCommand(BENSON.getName(), index,
                Optional.empty(), Optional.of(LocalTime.of(14, 30)), false, Optional.empty()));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new EditInteractionCommand(BENSON.getName(), index,
                Optional.empty(), Optional.empty(), Optional.of("New"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new EditInteractionCommand(BENSON.getName(), index,
                Optional.of(LocalDate.of(2026, 10, 2)), Optional.empty(), Optional.empty())));
        assertFalse(command.equals(new EditInteractionCommand(BENSON.getName(), index,
                Optional.empty(), Optional.empty(), true, Optional.of("New"))));
        assertFalse(command.equals(new EditInteractionCommand(BENSON.getName(), index,
                Optional.empty(), Optional.of(LocalTime.of(14, 30)), Optional.of("New"))));
        assertFalse(command.equals(new EditInteractionCommand(BENSON.getName(), index,
                Optional.empty(), Optional.empty(), Optional.of("Different"))));
        assertTrue(command.toString().contains("isTimeEdited=false"));
    }
}
