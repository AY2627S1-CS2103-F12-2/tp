package seedu.tutorlink.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.commons.exceptions.DataLoadingException;
import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.commons.util.FileUtil;
import seedu.tutorlink.commons.util.JsonUtil;
import seedu.tutorlink.model.ReadOnlyTutorLink;

/**
 * A class to access TutorLink data stored as a JSON file on the hard disk.
 */
public class JsonTutorLinkStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonTutorLinkStorage.class);

    private Path filePath;

    public JsonTutorLinkStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getTutorLinkFilePath() {
        return filePath;
    }

    /**
     * Returns TutorLink data as a {@link ReadOnlyTutorLink}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyTutorLink> readTutorLink() throws DataLoadingException {
        return readTutorLink(filePath);
    }

    /**
     * Similar to {@link #readTutorLink()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyTutorLink> readTutorLink(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableTutorLink> jsonTutorLink = JsonUtil.readJsonFile(
                filePath, JsonSerializableTutorLink.class);
        if (!jsonTutorLink.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonTutorLink.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyTutorLink} to the storage.
     * @param tutorLink cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveTutorLink(ReadOnlyTutorLink tutorLink) throws IOException {
        saveTutorLink(tutorLink, filePath);
    }

    /**
     * Similar to {@link #saveTutorLink(ReadOnlyTutorLink)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveTutorLink(ReadOnlyTutorLink tutorLink, Path filePath) throws IOException {
        requireNonNull(tutorLink);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableTutorLink(tutorLink), filePath);
    }

}
