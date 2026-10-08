package seedu.tutorlink.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static seedu.tutorlink.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.tutorlink.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.logic.commands.AddCommand;
import seedu.tutorlink.logic.commands.AddInteractionCommand;
import seedu.tutorlink.logic.commands.CommandResult;
import seedu.tutorlink.logic.commands.ListCommand;
import seedu.tutorlink.logic.commands.ListInteractionsCommand;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.storage.JsonTutorLinkStorage;
import seedu.tutorlink.storage.JsonUserPrefsStorage;
import seedu.tutorlink.storage.StorageManager;
import seedu.tutorlink.testutil.StudentBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonTutorLinkStorage tutorLinkStorage =
                new JsonTutorLinkStorage(temporaryFolder.resolve("tutorLink.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(tutorLinkStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_addInteraction_recordsAndPersistsInteraction() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + " n/Alex Tan");

        String interactionCommand = AddInteractionCommand.COMMAND_WORD
                + " n/Alex Tan d/2026-10-01 t/14:30 note/Practised algebraic fractions";
        CommandResult result = logic.execute(interactionCommand);

        Interaction expectedInteraction = new Interaction(LocalDate.of(2026, 10, 1),
                Optional.of(LocalTime.of(14, 30)), "Practised algebraic fractions");
        assertEquals(String.format(AddInteractionCommand.MESSAGE_SUCCESS, "Alex Tan",
                expectedInteraction.getDate(), " (14:30)", 1),
                result.getFeedbackToUser());

        Student student = model.findStudentByName(new Name("Alex Tan")).orElseThrow();
        assertEquals(List.of(expectedInteraction), student.getInteractions());

        ReadOnlyTutorLink readBack = new JsonTutorLinkStorage(temporaryFolder.resolve("tutorLink.json"))
                .readTutorLink().orElseThrow();
        assertEquals(model.getTutorLink(), new TutorLink(readBack));
    }

    @Test
    public void execute_addFutureInteraction_warnsAndRecordsInteraction() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + " n/Alex Tan");

        LocalDate futureDate = LocalDate.now().plusDays(1);
        String interactionCommand = AddInteractionCommand.COMMAND_WORD
                + " n/Alex Tan d/" + futureDate + " note/Planned lesson";
        CommandResult result = logic.execute(interactionCommand);

        String expectedMessage = String.format(AddInteractionCommand.MESSAGE_FUTURE_DATE_WARNING,
                futureDate, LocalDate.now())
                + String.format(AddInteractionCommand.MESSAGE_SUCCESS, "Alex Tan", futureDate, "", 1);
        assertEquals(expectedMessage, result.getFeedbackToUser());

        Interaction expectedInteraction = new Interaction(futureDate, Optional.empty(), "Planned lesson");
        Student student = model.findStudentByName(new Name("Alex Tan")).orElseThrow();
        assertEquals(List.of(expectedInteraction), student.getInteractions());
    }

    @Test
    public void execute_addInteraction_reportsUpdatedInteractionCount() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + " n/Alex Tan");
        logic.execute(AddInteractionCommand.COMMAND_WORD
                + " n/Alex Tan d/2026-10-01 note/First lesson");

        LocalDate secondDate = LocalDate.of(2026, 10, 2);
        CommandResult result = logic.execute(AddInteractionCommand.COMMAND_WORD
                + " n/Alex Tan d/" + secondDate + " note/Second lesson");

        assertEquals(String.format(AddInteractionCommand.MESSAGE_SUCCESS, "Alex Tan", secondDate, "", 2),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_listInteractions_returnsChronologicalHistory() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + " n/Alex Tan");
        logic.execute(AddInteractionCommand.COMMAND_WORD
                + " n/Alex Tan d/2026-10-02 t/14:30 note/Practised fractions");
        logic.execute(AddInteractionCommand.COMMAND_WORD
                + " n/Alex Tan d/2026-10-01 note/Discussed revision plan");

        CommandResult result = logic.execute(ListInteractionsCommand.COMMAND_WORD + " n/Alex Tan");

        assertEquals(String.format(ListInteractionsCommand.MESSAGE_SUCCESS, "Alex Tan",
                "2026-10-01 - Discussed revision plan\n2026-10-02 14:30 - Practised fractions"),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredStudentList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getTutorLink(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonTutorLinkStorage that throws the IOException e when saving
        JsonTutorLinkStorage tutorLinkStorage = new JsonTutorLinkStorage(prefPath) {
            @Override
            public void saveTutorLink(ReadOnlyTutorLink tutorLink) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(tutorLinkStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveTutorLink method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SUBJECT_DESC_MATH;
        Student expectedStudent = new StudentBuilder(AMY).build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addStudent(expectedStudent);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
