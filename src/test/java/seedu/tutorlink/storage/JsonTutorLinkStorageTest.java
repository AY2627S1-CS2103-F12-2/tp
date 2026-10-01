package seedu.tutorlink.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.ALICE;
import static seedu.tutorlink.testutil.TypicalStudents.HOON;
import static seedu.tutorlink.testutil.TypicalStudents.IDA;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.commons.exceptions.DataLoadingException;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;

public class JsonTutorLinkStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonTutorLinkStorageTest");

    @TempDir
    public Path testFolder;

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
