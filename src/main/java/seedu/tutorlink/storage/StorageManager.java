package seedu.tutorlink.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.commons.exceptions.DataLoadingException;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.ReadOnlyUserPrefs;
import seedu.tutorlink.model.UserPrefs;

/**
 * Manages storage of TutorLink data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonTutorLinkStorage tutorLinkStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given TutorLink and user prefs storage.
     */
    public StorageManager(JsonTutorLinkStorage tutorLinkStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.tutorLinkStorage = tutorLinkStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ TutorLink methods ==============================

    @Override
    public Path getTutorLinkFilePath() {
        return tutorLinkStorage.getTutorLinkFilePath();
    }

    @Override
    public Optional<ReadOnlyTutorLink> readTutorLink() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + tutorLinkStorage.getTutorLinkFilePath());
        return tutorLinkStorage.readTutorLink();
    }

    @Override
    public void saveTutorLink(ReadOnlyTutorLink tutorLink) throws IOException {
        logger.fine("Attempting to write to data file: " + tutorLinkStorage.getTutorLinkFilePath());
        tutorLinkStorage.saveTutorLink(tutorLink);
    }

}
