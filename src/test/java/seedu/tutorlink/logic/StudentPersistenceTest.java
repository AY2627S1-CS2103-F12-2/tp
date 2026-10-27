package seedu.tutorlink.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.AMY;
import static seedu.tutorlink.testutil.TypicalStudents.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.logic.commands.CommandResult;
import seedu.tutorlink.logic.commands.ViewStudentCommand;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.storage.JsonTutorLinkStorage;
import seedu.tutorlink.storage.JsonUserPrefsStorage;
import seedu.tutorlink.storage.StorageManager;
import seedu.tutorlink.testutil.StudentBuilder;

/**
 * Regression tests for issues #184 and #185 on the student commands: a failed save must not change the data,
 * and {@code student view} must not save.
 */
public class StudentPersistenceTest {

    private static final String ADD_COMMAND = "student add n/Carl Kurz s/Math";
    private static final String EDIT_COMMAND = "student edit n/" + AMY.getName() + " new/Amy Tan s/Chemistry";
    private static final String DELETE_COMMAND = "student delete n/" + AMY.getName();
    private static final String ADD_INTERACTION_COMMAND =
            "interaction add n/" + AMY.getName() + " d/2026-04-04 note/Second save failure";
    private static final String RECOVERY_NAME = "Dana Lee";

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private Path dataPath;

    @BeforeEach
    public void setUp() throws Exception {
        Student amyWithRecords = AMY
                .withInteraction(new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Algebra"))
                .withFollowUp(new FollowUp("Review algebra", LocalDate.of(2026, 10, 20)));
        model = new ModelManager();
        model.addStudent(amyWithRecords);
        model.addStudent(BOB);
        dataPath = temporaryFolder.resolve("tutorlink.json");
        new JsonTutorLinkStorage(dataPath).saveTutorLink(model.getTutorLink());
    }

    @Test
    public void execute_addStudentIoFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(ADD_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_addStudentPermissionFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(ADD_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_editStudentIoFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(EDIT_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_editStudentPermissionFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(EDIT_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_deleteStudentIoFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(DELETE_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_deleteStudentPermissionFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(DELETE_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_interactionAddFailure_preservesSelectedStudent() throws Exception {
        Student selectedStudent = model.findStudentByName(AMY.getName()).orElseThrow();
        model.setSelectedStudent(selectedStudent);
        RecoverableTutorLinkStorage storage = useStorage(new AccessDeniedException(dataPath.toString()));

        assertThrows(CommandException.class, () -> logic.execute(ADD_INTERACTION_COMMAND));

        assertEquals(selectedStudent, model.getSelectedStudent().get());
        assertEquals(1, storage.saveAttempts);
    }

    @Test
    public void execute_addStudentFailureThenSuccessfulSave_failedAdditionNotPersisted() throws Exception {
        assertRecoveryDoesNotPersistFailedChange(ADD_COMMAND);
    }

    @Test
    public void execute_editStudentFailureThenSuccessfulSave_failedEditNotPersisted() throws Exception {
        assertRecoveryDoesNotPersistFailedChange(EDIT_COMMAND);
    }

    @Test
    public void execute_deleteStudentFailureThenSuccessfulSave_failedDeletionNotPersisted() throws Exception {
        assertRecoveryDoesNotPersistFailedChange(DELETE_COMMAND);
    }

    @Test
    public void execute_viewStudentWithReadOnlyFile_showsStudentWithoutSaving() throws Exception {
        byte[] originalFile = Files.readAllBytes(dataPath);
        RecoverableTutorLinkStorage storage = useStorage(new AccessDeniedException(dataPath.toString()));

        CommandResult result = logic.execute("student view n/" + BOB.getName());

        assertEquals(String.format(ViewStudentCommand.MESSAGE_SUCCESS, BOB.getName(), "Physics, Math"),
                result.getFeedbackToUser());
        assertEquals(BOB, model.getSelectedStudent().get());
        assertEquals(0, storage.saveAttempts);
        assertArrayEquals(originalFile, Files.readAllBytes(dataPath));
    }

    private RecoverableTutorLinkStorage assertSaveFailureRestoresData(String command, IOException failure)
            throws Exception {
        TutorLink originalData = new TutorLink(model.getTutorLink());
        List<Student> originalList = List.copyOf(model.getFilteredStudentList());
        byte[] originalFile = Files.readAllBytes(dataPath);
        RecoverableTutorLinkStorage storage = useStorage(failure);

        CommandException exception = assertThrows(CommandException.class, () -> logic.execute(command));

        String expectedError = failure instanceof AccessDeniedException
                ? String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, failure.getMessage())
                : String.format(LogicManager.FILE_OPS_ERROR_FORMAT, failure.getMessage());
        assertEquals(expectedError, exception.getMessage());
        assertSame(failure, exception.getCause());
        assertEquals(1, storage.saveAttempts);
        assertEquals(originalData, model.getTutorLink());
        assertEquals(originalList, model.getFilteredStudentList());
        assertArrayEquals(originalFile, Files.readAllBytes(dataPath));
        return storage;
    }

    private void assertRecoveryDoesNotPersistFailedChange(String command) throws Exception {
        RecoverableTutorLinkStorage storage = assertSaveFailureRestoresData(command,
                new IOException("Disk unavailable"));
        TutorLink expectedData = new TutorLink(model.getTutorLink());
        expectedData.addStudent(new StudentBuilder().withName(RECOVERY_NAME).build());

        storage.allowSaving();
        logic.execute("student add n/" + RECOVERY_NAME);

        assertEquals(2, storage.saveAttempts);
        assertEquals(expectedData, model.getTutorLink());
        ReadOnlyTutorLink savedData = new JsonTutorLinkStorage(dataPath).readTutorLink().orElseThrow();
        Model restartedModel = new ModelManager(savedData, new UserPrefs());
        assertEquals(expectedData, restartedModel.getTutorLink());
        assertEquals(Optional.empty(), restartedModel.findStudentByName(new Name("Carl Kurz")));
        assertEquals(Optional.empty(), restartedModel.findStudentByName(new Name("Amy Tan")));
    }

    private RecoverableTutorLinkStorage useStorage(IOException failure) {
        RecoverableTutorLinkStorage storage = new RecoverableTutorLinkStorage(dataPath, failure);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        return storage;
    }

    /**
     * A data-file storage that fails every save with the given exception until saving is allowed.
     */
    private static class RecoverableTutorLinkStorage extends JsonTutorLinkStorage {

        private IOException failure;
        private int saveAttempts;

        RecoverableTutorLinkStorage(Path filePath, IOException failure) {
            super(filePath);
            this.failure = failure;
        }

        void allowSaving() {
            failure = null;
        }

        @Override
        public void saveTutorLink(ReadOnlyTutorLink tutorLink) throws IOException {
            saveAttempts++;
            if (failure != null) {
                throw failure;
            }
            super.saveTutorLink(tutorLink);
        }
    }
}
