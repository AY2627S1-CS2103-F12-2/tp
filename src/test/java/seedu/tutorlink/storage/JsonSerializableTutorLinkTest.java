package seedu.tutorlink.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.commons.util.JsonUtil;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.testutil.TypicalStudents;

public class JsonSerializableTutorLinkTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableTutorLinkTest");
    private static final Path TYPICAL_STUDENTS_FILE = TEST_DATA_FOLDER.resolve("typicalStudentsTutorLink.json");
    private static final Path INVALID_STUDENT_FILE = TEST_DATA_FOLDER.resolve("invalidStudentTutorLink.json");
    private static final Path DUPLICATE_STUDENT_FILE = TEST_DATA_FOLDER.resolve("duplicateStudentTutorLink.json");

    @Test
    public void toModelType_typicalStudentsFile_success() throws Exception {
        JsonSerializableTutorLink dataFromFile = JsonUtil.readJsonFile(TYPICAL_STUDENTS_FILE,
                JsonSerializableTutorLink.class).get();
        TutorLink tutorLinkFromFile = dataFromFile.toModelType();
        TutorLink typicalStudentsTutorLink = TypicalStudents.getTypicalTutorLink();
        assertEquals(tutorLinkFromFile, typicalStudentsTutorLink);
    }

    @Test
    public void toModelType_invalidStudentFile_throwsIllegalValueException() throws Exception {
        JsonSerializableTutorLink dataFromFile = JsonUtil.readJsonFile(INVALID_STUDENT_FILE,
                JsonSerializableTutorLink.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateStudents_throwsIllegalValueException() throws Exception {
        JsonSerializableTutorLink dataFromFile = JsonUtil.readJsonFile(DUPLICATE_STUDENT_FILE,
                JsonSerializableTutorLink.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableTutorLink.MESSAGE_DUPLICATE_STUDENT,
                dataFromFile::toModelType);
    }

}
