package seedu.tutorlink.logic.commands;

import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;

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
import seedu.tutorlink.testutil.StudentBuilder;

/**
 * Contains integration tests for {@link ListInteractionsCommand}.
 */
public class ListInteractionsCommandTest {

    private static final Name STUDENT_NAME = new Name("Alex Tan");
    private static final Interaction EARLIEST = new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(),
            "Discussed revision plan");
    private static final Interaction MORNING = new Interaction(LocalDate.of(2026, 10, 1),
            Optional.of(LocalTime.of(9, 15)), "Reviewed algebra");
    private static final Interaction LATEST = new Interaction(LocalDate.of(2026, 10, 2),
            Optional.of(LocalTime.of(14, 30)), "Practised fractions");

    @Test
    public void execute_interactionsAreUnordered_returnsChronologicalHistory() {
        Student student = new StudentBuilder().withName("Alex Tan").build()
                .withInteraction(LATEST)
                .withInteraction(EARLIEST)
                .withInteraction(MORNING);
        TutorLink tutorLink = new TutorLink();
        tutorLink.addStudent(student);
        Model model = new ModelManager(tutorLink, new UserPrefs());
        String expectedMessage = String.format(ListInteractionsCommand.MESSAGE_SUCCESS, STUDENT_NAME,
                3,
                "1. 2026-10-01 \u2014 Discussed revision plan\n"
                        + "2. 2026-10-01 09:15 \u2014 Reviewed algebra\n"
                        + "3. 2026-10-02 14:30 \u2014 Practised fractions");

        assertCommandSuccess(new ListInteractionsCommand(STUDENT_NAME), model, expectedMessage,
                new ModelManager(model.getTutorLink(), new UserPrefs()));
    }

    @Test
    public void execute_noInteractions_returnsEmptyHistoryMessage() {
        TutorLink tutorLink = new TutorLink();
        tutorLink.addStudent(new StudentBuilder().withName("Alex Tan").build());
        Model model = new ModelManager(tutorLink, new UserPrefs());

        assertCommandSuccess(new ListInteractionsCommand(STUDENT_NAME), model,
                String.format(ListInteractionsCommand.MESSAGE_NONE_RECORDED, STUDENT_NAME),
                new ModelManager(model.getTutorLink(), new UserPrefs()));
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        Model model = new ModelManager(new TutorLink(), new UserPrefs());

        assertCommandFailure(new ListInteractionsCommand(STUDENT_NAME), model,
                String.format(ListInteractionsCommand.MESSAGE_STUDENT_NOT_FOUND, STUDENT_NAME));
    }

    @Test
    public void equals() {
        ListInteractionsCommand command = new ListInteractionsCommand(STUDENT_NAME);

        org.junit.jupiter.api.Assertions.assertEquals(command, command);
        org.junit.jupiter.api.Assertions.assertEquals(command, new ListInteractionsCommand(STUDENT_NAME));
        org.junit.jupiter.api.Assertions.assertNotEquals(command, null);
        org.junit.jupiter.api.Assertions.assertNotEquals(command,
                new ListInteractionsCommand(new Name("Jamie Tan")));
    }
}
