package seedu.tutorlink;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.commons.util.DataFileLock;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
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
    public void stop_closeFailure_clearsOwnershipWithoutSavingAgain() throws Exception {
        Path prefsFile = testFolder.resolve("preferences.json");
        Storage storage = new StorageManager(new JsonTutorLinkStorage(testFolder.resolve("tutorlink.json")),
                new JsonUserPrefsStorage(prefsFile));
        MainApp application = new MainApp();
        application.model = new ModelManager();
        application.storage = storage;
        int[] closeAttempts = {0};
        setLockOwner(application, () -> {
            closeAttempts[0]++;
            throw new IOException("Simulated lock close failure");
        });

        assertDoesNotThrow(application::stop);
        assertEquals(application.model.getUserPrefs(), storage.readUserPrefs().orElseThrow());
        String replacementPrefs = "{  }\n";
        Files.writeString(prefsFile, replacementPrefs);
        assertDoesNotThrow(application::stop);

        assertEquals(1, closeAttempts[0]);
        assertEquals(replacementPrefs, Files.readString(prefsFile));
    }

    @Test
    public void stop_preferencesSaveFailure_releasesDataFile() throws Exception {
        Path dataFile = testFolder.resolve("tutorlink.json");
        Path prefsFile = testFolder.resolve("preferences.json");
        MainApp application = new MainApp();
        application.model = new ModelManager();
        application.storage = new StorageManager(new JsonTutorLinkStorage(dataFile),
                new JsonUserPrefsStorage(prefsFile)) {
            @Override
            public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
                throw new IOException("Simulated preference save failure");
            }
        };

        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            setLockOwner(application, owner);

            assertDoesNotThrow(application::stop);

            try (DataFileLock nextOwner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
                assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            }
        }
    }

    @Test
    public void stop_partialInitialization_releasesOwnershipWithoutSaving() throws Exception {
        for (boolean storageInitialized : List.of(false, true)) {
            Path dataFile = testFolder.resolve("data-" + storageInitialized + ".json");
            Path prefsFile = testFolder.resolve("preferences-" + storageInitialized + ".json");
            MainApp application = new MainApp();
            if (storageInitialized) {
                application.storage = new StorageManager(new JsonTutorLinkStorage(dataFile),
                        new JsonUserPrefsStorage(prefsFile));
            }

            try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
                setLockOwner(application, owner);

                assertDoesNotThrow(application::stop);

                try (DataFileLock nextOwner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
                    assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
                }
            }
            assertTrue(Files.notExists(prefsFile));
        }
    }

    private void setLockOwner(MainApp application, Closeable owner) throws Exception {
        Field ownerField = MainApp.class.getDeclaredField("dataFileLock");
        ownerField.setAccessible(true);
        ownerField.set(application, owner);
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
