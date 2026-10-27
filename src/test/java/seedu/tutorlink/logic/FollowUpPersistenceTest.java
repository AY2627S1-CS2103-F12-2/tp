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

import seedu.tutorlink.logic.commands.AddFollowUpCommand;
import seedu.tutorlink.logic.commands.CommandResult;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.storage.JsonTutorLinkStorage;
import seedu.tutorlink.storage.JsonUserPrefsStorage;
import seedu.tutorlink.storage.StorageManager;

public class FollowUpPersistenceTest {

    private static final LocalDate REVIEW_DATE = LocalDate.of(2026, 10, 25);
    private static final String ADD_COMMAND = "followup add n/" + AMY.getName()
            + " desc/Failed addition review/2026-10-25";
    private static final String DONE_COMMAND = "followup done n/" + AMY.getName() + " i/2";
    private static final String CANCEL_COMMAND = "followup cancel n/" + AMY.getName() + " i/2";

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private Student originalStudent;
    private Path dataPath;

    @BeforeEach
    public void setUp() throws Exception {
        originalStudent = AMY
                .withInteraction(new Interaction(REVIEW_DATE.minusDays(1), Optional.empty(), "Existing lesson"))
                .withFollowUp(new FollowUp("Later review", REVIEW_DATE.plusDays(1)))
                .withFollowUp(new FollowUp("Completed history", REVIEW_DATE.minusDays(2), FollowUpStatus.DONE))
                .withFollowUp(new FollowUp("Cancelled history", REVIEW_DATE.minusDays(1), FollowUpStatus.CANCELLED))
                .withFollowUp(new FollowUp("First review", REVIEW_DATE))
                .withFollowUp(new FollowUp("Second review", REVIEW_DATE));
        model = new ModelManager();
        model.addStudent(originalStudent);
        model.addStudent(BOB);
        model.updateFilteredStudentList(new NameContainsKeywordsPredicate(List.of("Amy")));
        model.setSelectedStudent(originalStudent);
        dataPath = temporaryFolder.resolve("tutorlink.json");
        new JsonTutorLinkStorage(dataPath).saveTutorLink(model.getTutorLink());
    }

    @Test
    public void execute_addFollowUpIoFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(ADD_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_addFollowUpPermissionFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(ADD_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_doneFollowUpIoFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(DONE_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_doneFollowUpPermissionFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(DONE_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_cancelFollowUpIoFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(CANCEL_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_cancelFollowUpPermissionFailure_restoresData() throws Exception {
        assertSaveFailureRestoresData(CANCEL_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_addFollowUpIoFailureThenSuccessfulSave_failedAdditionNotPersisted() throws Exception {
        assertRecoveryDoesNotPersistFailedChange(ADD_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_addFollowUpPermissionFailureThenSuccessfulSave_failedAdditionNotPersisted() throws Exception {
        assertRecoveryDoesNotPersistFailedChange(ADD_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_doneFollowUpIoFailureThenSuccessfulSave_failedCompletionNotPersisted() throws Exception {
        assertRecoveryDoesNotPersistFailedChange(DONE_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_doneFollowUpPermissionFailureThenSuccessfulSave_failedCompletionNotPersisted()
            throws Exception {
        assertRecoveryDoesNotPersistFailedChange(DONE_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    @Test
    public void execute_cancelFollowUpIoFailureThenSuccessfulSave_failedCancellationNotPersisted() throws Exception {
        assertRecoveryDoesNotPersistFailedChange(CANCEL_COMMAND, new IOException("Disk unavailable"));
    }

    @Test
    public void execute_cancelFollowUpPermissionFailureThenSuccessfulSave_failedCancellationNotPersisted()
            throws Exception {
        assertRecoveryDoesNotPersistFailedChange(CANCEL_COMMAND, new AccessDeniedException(dataPath.toString()));
    }

    private RecoverableTutorLinkStorage assertSaveFailureRestoresData(String command, IOException failure)
            throws Exception {
        TutorLink originalData = new TutorLink(model.getTutorLink());
        byte[] originalFile = Files.readAllBytes(dataPath);
        RecoverableTutorLinkStorage storage = new RecoverableTutorLinkStorage(dataPath, failure);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        var displayedStudents = logic.getFilteredStudentList();
        var selectedStudent = logic.getSelectedStudent();

        CommandException exception = assertThrows(CommandException.class, () -> logic.execute(command));

        String expectedError = failure instanceof AccessDeniedException
                ? String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, failure.getMessage())
                : String.format(LogicManager.FILE_OPS_ERROR_FORMAT, failure.getMessage());
        assertEquals(expectedError, exception.getMessage());
        assertSame(failure, exception.getCause());
        assertEquals(1, storage.saveAttempts);
        assertEquals(originalData, model.getTutorLink());
        assertEquals(List.of(originalStudent), displayedStudents);
        assertEquals(originalStudent.getOutstandingFollowUps(),
                model.findStudentByName(AMY.getName()).orElseThrow().getOutstandingFollowUps());
        // Rollback restores data while retaining the selected student's context.
        assertEquals(originalStudent, selectedStudent.get());
        assertArrayEquals(originalFile, Files.readAllBytes(dataPath));
        assertEquals(originalData, new TutorLink(new JsonTutorLinkStorage(dataPath).readTutorLink().orElseThrow()));
        return storage;
    }

    private void assertRecoveryDoesNotPersistFailedChange(String command, IOException failure) throws Exception {
        RecoverableTutorLinkStorage storage = assertSaveFailureRestoresData(command, failure);
        FollowUp successfulAddition = new FollowUp("Successful addition", REVIEW_DATE.plusDays(2));
        Student expectedStudent = originalStudent.withFollowUp(successfulAddition);
        TutorLink expectedData = new TutorLink(model.getTutorLink());
        expectedData.setStudent(originalStudent, expectedStudent);

        storage.allowSaving();
        CommandResult result = logic.execute("followup add n/" + AMY.getName()
                + " desc/" + successfulAddition.getDescription() + " review/" + successfulAddition.getReviewDate());

        assertEquals(String.format(AddFollowUpCommand.MESSAGE_SUCCESS, AMY.getName(),
                successfulAddition.getReviewDate(), successfulAddition.getDescription(), 4),
                result.getFeedbackToUser());
        assertEquals(2, storage.saveAttempts);
        assertEquals(expectedData, model.getTutorLink());

        ReadOnlyTutorLink savedData = new JsonTutorLinkStorage(dataPath).readTutorLink().orElseThrow();
        Model restartedModel = new ModelManager(savedData, new UserPrefs());
        assertEquals(expectedData, restartedModel.getTutorLink());
        assertEquals(expectedStudent.getOutstandingFollowUps(),
                restartedModel.findStudentByName(AMY.getName()).orElseThrow().getOutstandingFollowUps());
    }

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
