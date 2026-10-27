package seedu.tutorlink.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.commons.exceptions.DataLoadingException;
import seedu.tutorlink.testutil.SerializableTestClass;
import seedu.tutorlink.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @TempDir
    public Path testFolder;

    @Test
    public void readJsonFile_nullRoot_throwsDataLoadingException() throws IOException {
        Path file = testFolder.resolve("nullRoot.json");
        for (String content : new String[]{"null", " \r\nnull\t\n"}) {
            Files.writeString(file, content);

            assertThrows(DataLoadingException.class, () -> JsonUtil.readJsonFile(file, SerializableTestClass.class));
            assertEquals(content, Files.readString(file));
        }
    }

    @Test
    public void readJsonFile_missingFile_returnsEmpty() throws DataLoadingException {
        Path file = testFolder.resolve("missing.json");

        assertTrue(JsonUtil.readJsonFile(file, SerializableTestClass.class).isEmpty());
    }

    @Test
    public void readJsonFile_validRoot_returnsObject() throws Exception {
        Path file = testFolder.resolve("validRoot.json");
        Files.writeString(file, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass result = JsonUtil.readJsonFile(file, SerializableTestClass.class).orElseThrow();

        assertEquals(SerializableTestClass.getNameTestValue(), result.getName());
        assertEquals(SerializableTestClass.getListTestValues(), result.getListOfLocalDateTimes());
        assertEquals(SerializableTestClass.getHashMapTestValues(), result.getMapOfIntegerToString());
    }

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    @Test
    public void fromString_trailingContent_throwsException() {
        String jsonWithTrailingContent = SerializableTestClass.JSON_STRING_REPRESENTATION + "TRAILING";

        assertThrows(IOException.class, () ->
                JsonUtil.fromJsonString(jsonWithTrailingContent, SerializableTestClass.class));
    }

    @Test
    public void fromString_secondRoot_throwsException() {
        String jsonWithSecondRoot = SerializableTestClass.JSON_STRING_REPRESENTATION + "{}";

        assertThrows(IOException.class, () ->
                JsonUtil.fromJsonString(jsonWithSecondRoot, SerializableTestClass.class));
    }

    @Test
    public void fromString_trailingWhitespace_succeeds() throws IOException {
        String jsonWithTrailingWhitespace = SerializableTestClass.JSON_STRING_REPRESENTATION + " \r\n\t";

        SerializableTestClass result = JsonUtil.fromJsonString(
                jsonWithTrailingWhitespace, SerializableTestClass.class);

        assertEquals(SerializableTestClass.getNameTestValue(), result.getName());
    }

    //TODO: @Test jsonUtil_readJsonStringToObjectInstance_correctObject()

    //TODO: @Test jsonUtil_writeThenReadObjectToJson_correctObject()
}
