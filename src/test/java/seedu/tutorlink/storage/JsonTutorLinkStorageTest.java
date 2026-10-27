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
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

public class JsonTutorLinkStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonTutorLinkStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void saveAndReload_followUpStatuses_preservesRecordsAndOrder() throws Exception {
        Path path = testFolder.resolve("followUpStatuses.json");
        TutorLink original = getTypicalTutorLink();
        Student student = ALICE;
        for (FollowUpStatus status : FollowUpStatus.values()) {
            student = student.withFollowUp(new FollowUp("Review " + status, LocalDate.of(2026, 10, 15), status));
        }
        original.setStudent(ALICE, student);
        new JsonTutorLinkStorage(path).saveTutorLink(original);

        assertEquals(original, new TutorLink(new JsonTutorLinkStorage(path).readTutorLink().orElseThrow()));
    }

    @Test
    public void read_legacyFollowUpWithoutStatus_defaultsToOutstanding() throws Exception {
        Path path = testFolder.resolve("legacyFollowUp.json");
        Files.writeString(path, "{\"students\":[{\"name\":\"John Tan\",\"followUps\":["
                + "{\"description\":\"Review algebra\",\"reviewDate\":\"2026-10-15\"}]}]}");

        Student restored = new JsonTutorLinkStorage(path).readTutorLink().orElseThrow().getStudentList().getFirst();
        assertEquals(List.of(new FollowUp("Review algebra", LocalDate.of(2026, 10, 15))), restored.getFollowUps());
    }

    @Test
    public void read_invalidFollowUpStatus_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("invalidFollowUpStatus.json");
        Files.writeString(path, "{\"students\":[{\"name\":\"John Tan\",\"followUps\":["
                + "{\"description\":\"Review algebra\",\"reviewDate\":\"2026-10-15\",\"status\":\"UNKNOWN\"}]}]}");

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

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
    public void read_duplicateStoredSubjectsWithTrailingSpace_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("duplicateSubjectsTrailingSpace.json");
        String json = "{\"students\":[{\"name\":\"John Tan\",\"subjects\":[\"Math\",\"math \"]}]}";
        Files.writeString(path, json);

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void readAndSave_unicodeSubjects_normalizesAndPreservesOrder() throws Exception {
        Path path = testFolder.resolve("unicodeSubjects.json");
        String tamilSubject = "\u0ba4\u0bae\u0bbf\u0bb4\u0bcd";
        String decomposedSubject = "e\u0301".repeat(Subject.MAX_LENGTH);
        String json = "{\"students\":[{\"name\":\"John Tan\",\"subjects\":[\"Franc\u0327ais\",\""
                + tamilSubject + "\",\"" + decomposedSubject + "\"]}]}";
        Files.writeString(path, json);

        JsonTutorLinkStorage storage = new JsonTutorLinkStorage(path);
        ReadOnlyTutorLink restored = storage.readTutorLink().orElseThrow();
        assertEquals(List.of("Fran\u00e7ais", tamilSubject, "\u00e9".repeat(Subject.MAX_LENGTH)),
                restored.getStudentList().getFirst().getSubjects().stream()
                        .map(subject -> subject.subjectName).toList());

        storage.saveTutorLink(restored);
        assertEquals(new TutorLink(restored), new TutorLink(storage.readTutorLink().orElseThrow()));
    }

    @Test
    public void read_invalidUnicodeSubjects_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("invalidUnicodeSubject.json");
        for (String subject : List.of("\u0327Math", "e\u0301".repeat(Subject.MAX_LENGTH + 1))) {
            String json = "{\"students\":[{\"name\":\"John Tan\",\"subjects\":[\"" + subject + "\"]}]}";
            Files.writeString(path, json);

            assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
        }
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
    public void read_trailingJsonContent_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("trailingJsonContent.json");
        Files.writeString(path, "{\"students\":[] }TRAILING_GARBAGE");

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
    }

    @Test
    public void read_trailingWhitespace_succeeds() throws Exception {
        Path path = testFolder.resolve("trailingWhitespace.json");
        Files.writeString(path, "{\"students\":[] } \r\n\t");

        assertEquals(new TutorLink(), new TutorLink(new JsonTutorLinkStorage(path).readTutorLink().orElseThrow()));
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readTutorLink("notJsonFormatTutorLink.json"));
    }

    @Test
    public void read_visuallyIdenticalStoredNames_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("visuallyIdenticalNames.json");
        String json = "{\"students\":[{\"name\":\"Jos\u00e9\"},{\"name\":\"Jose\u0301\"}]}";
        Files.writeString(path, json);

        assertThrows(DataLoadingException.class, () -> new JsonTutorLinkStorage(path).readTutorLink());
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
