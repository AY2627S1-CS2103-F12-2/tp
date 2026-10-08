package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Contains integration tests for {@link AddInteractionCommand}.
 */
public class AddInteractionCommandTest {

    private static final Interaction INTERACTION = new Interaction(LocalDate.of(2026, 10, 1),
            Optional.of(LocalTime.of(14, 30)), "Practised algebraic fractions");

    private Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void execute_existingStudent_success() {
        Student updatedStudent = BENSON.withInteraction(INTERACTION);
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        Student expectedTarget = expectedModel.findStudentByName(BENSON.getName()).orElseThrow();
        expectedModel.setStudent(expectedTarget, updatedStudent);

        AddInteractionCommand command = new AddInteractionCommand(BENSON.getName(), INTERACTION);
        String expectedMessage = String.format(AddInteractionCommand.MESSAGE_SUCCESS, BENSON.getName());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_todayDate_successWithoutWarning() {
        Interaction todayInteraction = new Interaction(LocalDate.now(), Optional.empty(), "Today's lesson");
        Student updatedStudent = BENSON.withInteraction(todayInteraction);
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        Student expectedTarget = expectedModel.findStudentByName(BENSON.getName()).orElseThrow();
        expectedModel.setStudent(expectedTarget, updatedStudent);

        AddInteractionCommand command = new AddInteractionCommand(BENSON.getName(), todayInteraction);
        String expectedMessage = String.format(AddInteractionCommand.MESSAGE_SUCCESS, BENSON.getName());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_futureDate_successWithWarning() {
        Interaction futureInteraction = new Interaction(LocalDate.now().plusDays(1), Optional.empty(),
                "Planned lesson");
        Student updatedStudent = BENSON.withInteraction(futureInteraction);
        Model expectedModel = new ModelManager(new TutorLink(model.getTutorLink()), new UserPrefs());
        Student expectedTarget = expectedModel.findStudentByName(BENSON.getName()).orElseThrow();
        expectedModel.setStudent(expectedTarget, updatedStudent);

        AddInteractionCommand command = new AddInteractionCommand(BENSON.getName(), futureInteraction);
        String expectedMessage = String.format(AddInteractionCommand.MESSAGE_FUTURE_DATE_WARNING,
                futureInteraction.getDate(), LocalDate.now())
                + String.format(AddInteractionCommand.MESSAGE_SUCCESS, BENSON.getName());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        AddInteractionCommand command = new AddInteractionCommand(new Name("John Tan"), INTERACTION);

        assertCommandFailure(command, model,
                String.format(AddInteractionCommand.MESSAGE_STUDENT_NOT_FOUND, "John Tan"));
    }

    @Test
    public void equals() {
        AddInteractionCommand command = new AddInteractionCommand(BENSON.getName(), INTERACTION);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddInteractionCommand(BENSON.getName(), INTERACTION)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new AddInteractionCommand(new Name("John Tan"), INTERACTION)));
        assertFalse(command.equals(new AddInteractionCommand(BENSON.getName(), new Interaction(
                LocalDate.of(2026, 10, 2), Optional.empty(), "Different note"))));
    }
}
