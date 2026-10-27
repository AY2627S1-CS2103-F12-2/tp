package seedu.tutorlink;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ReadOnlyUserPrefs;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.storage.JsonTutorLinkStorage;
import seedu.tutorlink.storage.JsonUserPrefsStorage;
import seedu.tutorlink.storage.Storage;
import seedu.tutorlink.storage.StorageManager;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    @Test
    public void initModelManager_nullJsonRoot_startsEmptyWithBackupAndWarning() throws Exception {
        Method initializeModel = MainApp.class.getDeclaredMethod("initModelManager", Storage.class,
                ReadOnlyUserPrefs.class);
        initializeModel.setAccessible(true);
        Field warningField = MainApp.class.getDeclaredField("startupWarning");
        warningField.setAccessible(true);
        Path dataFile = testFolder.resolve("tutorlink.json");
        Path backupFile = testFolder.resolve("tutorlink.json.bak");
        for (String content : new String[]{"null", " \r\nnull\t\n"}) {
            Files.writeString(dataFile, content);
            byte[] originalData = Files.readAllBytes(dataFile);
            Storage storage = new StorageManager(new JsonTutorLinkStorage(dataFile),
                    new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
            MainApp application = new MainApp();

            Model initializedModel = (Model) initializeModel.invoke(application, storage, new UserPrefs());

            assertTrue(initializedModel.getTutorLink().getStudentList().isEmpty());
            assertArrayEquals(originalData, Files.readAllBytes(dataFile));
            assertArrayEquals(originalData, Files.readAllBytes(backupFile));
            assertEquals(Optional.of(String.format(MainApp.MESSAGE_UNREADABLE_DATA_BACKED_UP, dataFile, backupFile)),
                    warningField.get(application));
        }
    }

    @Test
    public void initPrefs_nullJsonRoot_restoresAndSavesDefaults() throws Exception {
        Path prefsFile = testFolder.resolve("preferences.json");
        Files.writeString(prefsFile, "null");
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(prefsFile);

        UserPrefs initializedPrefs = new MainApp().initPrefs(storage);

        assertEquals(new UserPrefs(), initializedPrefs);
        assertEquals(initializedPrefs, storage.readUserPrefs().orElseThrow());
    }

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
