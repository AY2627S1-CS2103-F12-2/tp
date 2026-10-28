package seedu.tutorlink.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests ownership and resource cleanup for the data file lock.
 */
public class DataFileLockTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void tryAcquire_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DataFileLock.tryAcquire(null));
    }

    @Test
    public void tryAcquire_missingParent_createsOnlyLockFile() throws Exception {
        Path dataFile = temporaryFolder.resolve("new/data/tutorlink.json");

        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(Files.isRegularFile(dataFile.resolveSibling("tutorlink.json.lock")));
            assertFalse(Files.exists(dataFile));
        }

        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void tryAcquire_existingData_preservesContents() throws Exception {
        Path dataFile = temporaryFolder.resolve("tutorlink.json");
        String originalData = "{\"students\":[{\"name\":\"Session Alpha\"}]}";
        Files.writeString(dataFile, originalData);

        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertEquals(originalData, Files.readString(dataFile));
        }

        assertEquals(originalData, Files.readString(dataFile));
    }

    @Test
    public void tryAcquire_sameFile_blocksFurtherOwnersUntilReleased() throws Exception {
        Path dataFile = temporaryFolder.resolve("tutorlink.json");

        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }

        try (DataFileLock nextOwner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }
    }

    @Test
    public void tryAcquire_lockHeldOutsideRegistry_returnsEmpty() throws Exception {
        Path dataFile = temporaryFolder.resolve("tutorlink.json");
        Path lockFile = dataFile.resolveSibling("tutorlink.json.lock");
        try (FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock externalOwner = channel.lock()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            assertFalse(Files.exists(dataFile));
        }

        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }
    }

    @Test
    public void tryAcquire_normalizedAlias_recognizesExistingOwner() throws Exception {
        Path dataFile = temporaryFolder.resolve("tutorlink.json");
        Path alias = temporaryFolder.resolve("unused/../tutorlink.json");

        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(alias).isEmpty());
        }
    }

    @Test
    public void tryAcquire_differentFolders_allowsBothOwners() throws Exception {
        Path firstFile = temporaryFolder.resolve("first/tutorlink.json");
        Path secondFile = temporaryFolder.resolve("second/tutorlink.json");

        try (DataFileLock firstOwner = DataFileLock.tryAcquire(firstFile).orElseThrow();
                DataFileLock secondOwner = DataFileLock.tryAcquire(secondFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(firstFile).isEmpty());
            assertTrue(DataFileLock.tryAcquire(secondFile).isEmpty());
        }
    }

    @Test
    public void close_repeatedClose_doesNotReleaseNewOwner() throws Exception {
        Path dataFile = temporaryFolder.resolve("tutorlink.json");
        DataFileLock firstOwner = DataFileLock.tryAcquire(dataFile).orElseThrow();
        firstOwner.close();

        try (DataFileLock nextOwner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            firstOwner.close();
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }
    }

    @Test
    public void tryAcquire_leftoverLockFile_allowsNewOwner() throws Exception {
        Path dataFile = temporaryFolder.resolve("tutorlink.json");
        Path lockFile = temporaryFolder.resolve("tutorlink.json.lock");
        Files.writeString(lockFile, "Left over from an earlier launch");

        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }

        assertEquals("Left over from an earlier launch", Files.readString(lockFile));
    }

    @Test
    public void tryAcquire_parentIsFile_throwsIoException() throws Exception {
        Path parentFile = temporaryFolder.resolve("not-a-directory");
        Files.writeString(parentFile, "Existing file");

        assertThrows(IOException.class, () -> DataFileLock.tryAcquire(parentFile.resolve("tutorlink.json")));
        assertEquals("Existing file", Files.readString(parentFile));
    }
}
