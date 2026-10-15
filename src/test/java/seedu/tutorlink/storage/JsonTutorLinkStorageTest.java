package seedu.tutorlink.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.ALICE;
import static seedu.tutorlink.testutil.TypicalStudents.HOON;
import static seedu.tutorlink.testutil.TypicalStudents.IDA;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.commons.exceptions.DataLoadingException;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

public class JsonTutorLinkStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonTutorLinkStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void saveAndReload_unicodeFollowUp_preservesFullDescription() throws Exception {
        Path path = testFolder.resolve("unicodeFollowUp.json");
        String description = "\uD83D\uDE00".repeat(200);
        TutorLink original = getTypicalTutorLink();
        original.setStudent(ALICE, ALICE.withFollowUp(new FollowUp(description, LocalDate.of(2026, 10, 15))));
        new JsonTutorLinkStorage(path).saveTutorLink(original);

        assertEquals(original, new TutorLink(new JsonTutorLinkStorage(path).readTutorLink().orElseThrow()));
    }

    @Test
    public void read_overlongUnicodeFollowUp_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("overlongUnicodeFollowUp.json");
        String description = "\uD83D\uDE00".repeat(201);
        Files.writeString(path, "{\"students\":[{\"name\":\"John Tan\",\"followUps\":[{\"description\":\""
                + description + "\",\"reviewDate\":\"2026-10-15\"}]}]}");

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void saveAndReload_followUps_preservesRecordsAndOrder() throws Exception {
        Path path = testFolder.resolve("followUps.json");
        TutorLink original = getTypicalTutorLink();
        Student student = ALICE.withFollowUp(new FollowUp("Later", LocalDate.of(2026, 10, 15)))
                .withFollowUp(new FollowUp("Earlier", LocalDate.of(2026, 10, 14)));
        original.setStudent(ALICE, student);
        new JsonTutorLinkStorage(path).saveTutorLink(original);

        ReadOnlyTutorLink restored = new JsonTutorLinkStorage(path).readTutorLink().orElseThrow();
        assertEquals(original, new TutorLink(restored));

        Student updated = student.withFollowUp(new FollowUp("Another", LocalDate.of(2026, 10, 14)));
        original.setStudent(student, updated);
        new JsonTutorLinkStorage(path).saveTutorLink(original);
        assertEquals(original, new TutorLink(new JsonTutorLinkStorage(path).readTutorLink().orElseThrow()));
    }

    @Test
    public void read_legacyFileWithoutFollowUps_loadsStudent() throws Exception {
        Path path = testFolder.resolve("legacy.json");
        Files.writeString(path, "{\"students\":[{\"name\":\"John Tan\",\"subjects\":[],\"interactions\":[]}]}");
        ReadOnlyTutorLink restored = new JsonTutorLinkStorage(path).readTutorLink().orElseThrow();
        assertEquals(List.of(new Student(new Name("John Tan"), List.of())), restored.getStudentList());
    }

    @Test
    public void read_invalidStoredFollowUp_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("invalidFollowUp.json");
        for (String entry : new String[]{"{\"description\":\"\",\"reviewDate\":\"2026-10-15\"}",
            "{\"description\":\"Review\",\"reviewDate\":\"2026-02-30\"}", "null"}) {
            Files.writeString(path, "{\"students\":[{\"name\":\"John Tan\",\"followUps\":[" + entry + "]}]}");
            assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
        }
    }

    @Test
    public void read_multilineStoredInteraction_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("multilineInteraction.json");
        String json = "{\"students\":[{\"name\":\"John Tan\",\"interactions\":[{"
                + "\"date\":\"2026-10-10\",\"note\":\"First line\\nSecond line\"}]}]}";
        Files.writeString(path, json);

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void read_overlongStoredInteraction_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("overlongInteraction.json");
        String note = "A".repeat(JsonAdaptedInteraction.MAX_NOTE_LENGTH + 1);
        String json = "{\"students\":[{\"name\":\"John Tan\",\"interactions\":[{"
                + "\"date\":\"2026-10-10\",\"note\":\"" + note + "\"}]}]}";
        Files.writeString(path, json);

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void read_duplicateStoredSubjects_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("duplicateSubjects.json");
        String json = "{\"students\":[{\"name\":\"John Tan\",\"subjects\":[\"Math\",\"math\"]}]}";
        Files.writeString(path, json);

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void read_overlongEmojiStoredInteraction_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("overlongEmojiInteraction.json");
        String emoji = String.valueOf(Character.toChars(0x1F600));
        String note = emoji.repeat(JsonAdaptedInteraction.MAX_NOTE_LENGTH + 1);
        String json = "{\"students\":[{\"name\":\"John Tan\",\"interactions\":[{"
                + "\"date\":\"2026-10-10\",\"note\":\"" + note + "\"}]}]}";
        Files.writeString(path, json);

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void read_invalidStoredStudent_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("invalidStudent.json");
        Files.writeString(path, "{\"students\":[null]}");

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void read_invalidStoredInteraction_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("invalidInteraction.json");
        Files.writeString(path, "{\"students\":[{\"name\":\"John Tan\",\"interactions\":[null]}]}");

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void readTutorLink_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readTutorLink(null));
    }

    private java.util.Optional<ReadOnlyTutorLink> readTutorLink(String filePath) throws Exception {
        return new JsonTutorLinkStorage(Paths.get(filePath)).readTutorLink(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readTutorLink("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readTutorLink("notJsonFormatTutorLink.json"));
    }

    @Test
    public void readTutorLink_invalidStudentTutorLink_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readTutorLink("invalidStudentTutorLink.json"));
    }

    @Test
    public void readTutorLink_invalidAndValidStudentTutorLink_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readTutorLink("invalidAndValidStudentTutorLink.json"));
    }

    @Test
    public void readAndSaveTutorLink_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempTutorLink.json");
        TutorLink original = getTypicalTutorLink();
        JsonTutorLinkStorage jsonTutorLinkStorage = new JsonTutorLinkStorage(filePath);

        // Save in new file and read back
        jsonTutorLinkStorage.saveTutorLink(original, filePath);
        ReadOnlyTutorLink readBack = jsonTutorLinkStorage.readTutorLink(filePath).get();
        assertEquals(original, new TutorLink(readBack));

        // Modify data, overwrite existing file, and read back
        original.addStudent(HOON);
        original.removeStudent(ALICE);
        jsonTutorLinkStorage.saveTutorLink(original, filePath);
        readBack = jsonTutorLinkStorage.readTutorLink(filePath).get();
        assertEquals(original, new TutorLink(readBack));

        // Save and read without specifying file path
        original.addStudent(IDA);
        jsonTutorLinkStorage.saveTutorLink(original); // file path not specified
        readBack = jsonTutorLinkStorage.readTutorLink().get(); // file path not specified
        assertEquals(original, new TutorLink(readBack));

    }

    @Test
    public void saveTutorLink_nullTutorLink_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveTutorLink(null, "SomeFile.json"));
    }

    /**
     * Saves {@code tutorLink} at the specified {@code filePath}.
     */
    private void saveTutorLink(ReadOnlyTutorLink tutorLink, String filePath) {
        try {
            new JsonTutorLinkStorage(Paths.get(filePath))
                    .saveTutorLink(tutorLink, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveTutorLink_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveTutorLink(new TutorLink(), null));
    }
}
