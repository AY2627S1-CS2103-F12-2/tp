package seedu.tutorlink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    @Test
    public void backUpUnreadableDataFile_existingFile_copiesNextToIt() throws Exception {
        Path dataFile = testFolder.resolve("tutorlink.json");
        Files.writeString(dataFile, "{ not valid json");

        Optional<Path> backup = MainApp.backUpUnreadableDataFile(dataFile);

        assertEquals(Optional.of(testFolder.resolve("tutorlink.json.bak")), backup);
        assertEquals("{ not valid json", Files.readString(backup.get()));
        assertEquals("{ not valid json", Files.readString(dataFile));
    }

    @Test
    public void backUpUnreadableDataFile_olderBackup_isReplaced() throws Exception {
        Path dataFile = testFolder.resolve("tutorlink.json");
        Files.writeString(testFolder.resolve("tutorlink.json.bak"), "old");
        Files.writeString(dataFile, "new");

        assertEquals("new", Files.readString(MainApp.backUpUnreadableDataFile(dataFile).get()));
    }

    @Test
    public void backUpUnreadableDataFile_missingFile_returnsEmpty() {
        assertTrue(MainApp.backUpUnreadableDataFile(testFolder.resolve("missing.json")).isEmpty());
    }
}
