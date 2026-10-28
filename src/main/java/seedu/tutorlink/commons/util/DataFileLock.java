package seedu.tutorlink.commons.util;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holds an exclusive operating-system lock on a data file's companion {@code .lock} file.
 * The companion file remains after closing; its existence does not indicate ownership.
 */
public final class DataFileLock implements Closeable {

    // Reusing one channel per path avoids releasing another local owner's lock when a channel closes.
    private static final Map<Path, DataFileLock> ACTIVE_LOCKS = new HashMap<>();

    private final Path lockPath;
    private final FileLock fileLock;

    private DataFileLock(Path lockPath, FileLock fileLock) {
        this.lockPath = lockPath;
        this.fileLock = fileLock;
    }

    /**
     * Tries to acquire exclusive ownership without reading or modifying the data file.
     * Creates missing parent directories and a companion file named after the data file with {@code .lock} appended.
     *
     * @param dataFile The data file to protect. Cannot be null.
     * @return the acquired lock, or an empty Optional if the lock is already held.
     * @throws IOException if the lock file cannot be created, opened or locked.
     */
    public static synchronized Optional<DataFileLock> tryAcquire(Path dataFile) throws IOException {
        requireNonNull(dataFile);
        Path absoluteDataFile = dataFile.toAbsolutePath().normalize();
        Path directory = Files.createDirectories(absoluteDataFile.getParent()).toRealPath();
        Path lockPath = directory.resolve(absoluteDataFile.getFileName() + ".lock");
        if (Files.exists(lockPath)) {
            lockPath = lockPath.toRealPath();
        }
        if (ACTIVE_LOCKS.containsKey(lockPath)) {
            return Optional.empty();
        }

        FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        boolean acquired = false;
        try {
            FileLock fileLock;
            try {
                fileLock = channel.tryLock();
            } catch (OverlappingFileLockException e) {
                return Optional.empty();
            }
            if (fileLock == null) {
                return Optional.empty();
            }
            DataFileLock owner = new DataFileLock(lockPath, fileLock);
            ACTIVE_LOCKS.put(lockPath, owner);
            acquired = true;
            return Optional.of(owner);
        } finally {
            if (!acquired) {
                channel.close();
            }
        }
    }

    /**
     * Releases ownership by closing its channel, retaining the companion file for later launches.
     * Calling this method again does not affect a subsequent owner.
     *
     * @throws IOException if the channel cannot be closed.
     */
    @Override
    public void close() throws IOException {
        synchronized (DataFileLock.class) {
            fileLock.channel().close();
            ACTIVE_LOCKS.remove(lockPath, this);
        }
    }
}
