package seedu.tutorlink.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.tutorlink.commons.exceptions.DataLoadingException;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.ReadOnlyUserPrefs;
import seedu.tutorlink.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage {

    /**
     * Returns the file path of the UserPrefs data file.
     */
    Path getUserPrefsFilePath();

    /**
     * Returns UserPrefs data from storage.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if the loading of data from preference file failed.
     */
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    /**
     * Saves the given {@link seedu.tutorlink.model.ReadOnlyUserPrefs} to the storage.
     * @param userPrefs cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    /**
     * Returns the file path of the TutorLink data file.
     */
    Path getTutorLinkFilePath();

    /**
     * Returns TutorLink data as a {@link ReadOnlyTutorLink}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyTutorLink> readTutorLink() throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyTutorLink} to the storage.
     * @param tutorLink cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveTutorLink(ReadOnlyTutorLink tutorLink) throws IOException;

}
