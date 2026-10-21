package seedu.tutorlink.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.testutil.TypicalStudents.AMY;
import static seedu.tutorlink.testutil.TypicalStudents.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.logic.commands.CommandResult;
import seedu.tutorlink.logic.commands.ListFollowUpsCommand;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.storage.JsonTutorLinkStorage;
import seedu.tutorlink.storage.JsonUserPrefsStorage;
import seedu.tutorlink.storage.StorageManager;

public class ListFollowUpsPersistenceTest {

    private static final LocalDate REVIEW_DATE = LocalDate.of(2026, 10, 25);

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_mixedFollowUpStatuses_returnsOutstandingListWithoutSaving() throws Exception {
        Student student = AMY
                .withFollowUp(new FollowUp("Later review", REVIEW_DATE.plusDays(1)))
                .withFollowUp(new FollowUp("Completed history", REVIEW_DATE.minusDays(2), FollowUpStatus.DONE))
                .withFollowUp(new FollowUp("Cancelled history", REVIEW_DATE.minusDays(1), FollowUpStatus.CANCELLED))
                .withFollowUp(new FollowUp("First review", REVIEW_DATE))
                .withFollowUp(new FollowUp("Second review", REVIEW_DATE));
        String expectedMessage = String.format(ListFollowUpsCommand.MESSAGE_SUCCESS, AMY.getName(), 3,
                "1. 2026-10-25 \u2014 First review\n"
                        + "2. 2026-10-25 \u2014 Second review\n"
                        + "3. 2026-10-26 \u2014 Later review");

        assertListingDoesNotSave(student, expectedMessage);
    }

    @Test
    public void execute_noFollowUps_returnsEmptyMessageWithoutSaving() throws Exception {
        assertListingDoesNotSave(AMY, String.format(ListFollowUpsCommand.MESSAGE_NONE_RECORDED, AMY.getName()));
    }

    @Test
    public void execute_onlyFollowUpHistory_returnsEmptyMessageWithoutSaving() throws Exception {
        Student student = AMY
                .withFollowUp(new FollowUp("Completed history", REVIEW_DATE, FollowUpStatus.DONE))
                .withFollowUp(new FollowUp("Cancelled history", REVIEW_DATE, FollowUpStatus.CANCELLED));

        assertListingDoesNotSave(student, String.format(ListFollowUpsCommand.MESSAGE_NONE_RECORDED, AMY.getName()));
    }

    private void assertListingDoesNotSave(Student student, String expectedMessage) throws Exception {
        Model model = new ModelManager();
        model.addStudent(student);
        model.addStudent(BOB);
        model.updateFilteredStudentList(new NameContainsKeywordsPredicate(List.of("Bob")));
        model.setSelectedStudent(BOB);
        TutorLink originalData = new TutorLink(model.getTutorLink());
        Path dataPath = temporaryFolder.resolve("tutorlink.json");
        new JsonTutorLinkStorage(dataPath).saveTutorLink(originalData);
        byte[] originalFile = Files.readAllBytes(dataPath);
        SaveRejectingTutorLinkStorage storage = new SaveRejectingTutorLinkStorage(dataPath);
        Logic logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));

        CommandResult result = logic.execute("followup list n/" + student.getName());

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(0, storage.saveAttempts);
        assertEquals(originalData, model.getTutorLink());
        assertEquals(List.of(BOB), logic.getFilteredStudentList());
        assertEquals(BOB, logic.getSelectedStudent().get());
        assertArrayEquals(originalFile, Files.readAllBytes(dataPath));
    }

    private static class SaveRejectingTutorLinkStorage extends JsonTutorLinkStorage {

        private int saveAttempts;

        SaveRejectingTutorLinkStorage(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveTutorLink(ReadOnlyTutorLink tutorLink) throws IOException {
            saveAttempts++;
            throw new AccessDeniedException(getTutorLinkFilePath().toString());
        }
    }
}
