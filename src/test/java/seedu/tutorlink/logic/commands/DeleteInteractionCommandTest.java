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

public class DeleteInteractionCommandTest {

    private static final Interaction EARLIEST = new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(),
            "Discussed revision plan");
    private static final Interaction MIDDLE = new Interaction(LocalDate.of(2026, 10, 1),
            Optional.of(LocalTime.of(14, 30)), "Practised algebra");
    private static final Interaction LATEST = new Interaction(LocalDate.of(2026, 10, 2), Optional.empty(),
            "Reviewed fractions");

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_chronologicalIndex_preservesOtherDataAndUpdatesSelection() {
        FollowUp followUp = new FollowUp("Send worksheet", LocalDate.of(2026, 10, 4));
        Student student = BENSON.withInteraction(LATEST).withInteraction(EARLIEST)
                .withInteraction(MIDDLE).withFollowUp(followUp);
        model.setStudent(BENSON, student);
        model.setSelectedStudent(student);
        Student expectedStudent = new Student(student.getName(), student.getSubjects(),
                List.of(LATEST, EARLIEST), student.getFollowUps());
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        expectedModel.setSelectedStudent(student);
        expectedModel.setStudent(student, expectedStudent);

        String message = String.format(DeleteInteractionCommand.MESSAGE_SUCCESS, student.getName(),
                Messages.formatInteraction(MIDDLE), 2);
        assertCommandSuccess(new DeleteInteractionCommand(
                new Name(student.getName().fullName.toLowerCase()), Index.fromOneBased(2)),
                model, message, expectedModel);
        assertEquals(expectedStudent, model.getSelectedStudent().getValue());
        assertEquals(List.of(LATEST, EARLIEST, MIDDLE), student.getInteractions());
    }

    @Test
    public void execute_unknownStudentOrOutOfRange_noDataChanged() {
        assertCommandFailure(new DeleteInteractionCommand(new Name("Missing Student"), Index.fromOneBased(1)),
                model, String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, "Missing Student"));
        assertCommandFailure(new DeleteInteractionCommand(BENSON.getName(), Index.fromOneBased(1)),
                model, Messages.MESSAGE_INDEX_OUT_OF_RANGE);

        model.setStudent(BENSON, BENSON.withInteraction(EARLIEST));
        assertCommandFailure(new DeleteInteractionCommand(BENSON.getName(), Index.fromOneBased(2)),
                model, Messages.MESSAGE_INDEX_OUT_OF_RANGE);
    }

    @Test
    public void constructorAndExecute_nullValues_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new DeleteInteractionCommand(null, Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () -> new DeleteInteractionCommand(BENSON.getName(), null));
        assertThrows(NullPointerException.class, () ->
                new DeleteInteractionCommand(BENSON.getName(), Index.fromOneBased(1)).execute(null));
    }

    @Test
    public void equalsAndToString() {
        DeleteInteractionCommand command =
                new DeleteInteractionCommand(BENSON.getName(), Index.fromOneBased(1));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteInteractionCommand(BENSON.getName(), Index.fromOneBased(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("interaction delete"));
        assertFalse(command.equals(
                new DeleteInteractionCommand(new Name("Other Student"), Index.fromOneBased(1))));
        assertFalse(command.equals(new DeleteInteractionCommand(BENSON.getName(), Index.fromOneBased(2))));
        assertEquals(DeleteInteractionCommand.class.getCanonicalName() + "{name=" + BENSON.getName()
                + ", index=" + Index.fromOneBased(1) + "}", command.toString());
    }
}
