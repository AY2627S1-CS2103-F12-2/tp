package seedu.tutorlink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.commons.exceptions.DataLoadingException;
import seedu.tutorlink.commons.util.DataFileLock;
import seedu.tutorlink.commons.util.StringUtil;
import seedu.tutorlink.logic.Logic;
import seedu.tutorlink.logic.LogicManager;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.ReadOnlyUserPrefs;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.util.SampleDataUtil;
import seedu.tutorlink.storage.JsonTutorLinkStorage;
import seedu.tutorlink.storage.JsonUserPrefsStorage;
import seedu.tutorlink.storage.Storage;
import seedu.tutorlink.storage.StorageManager;
import seedu.tutorlink.ui.Ui;
import seedu.tutorlink.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.4";

    static final String MESSAGE_UNREADABLE_DATA_BACKED_UP = "\u26a0 The data file %1$s could not be read, so "
            + "TutorLink started with no students.\nA copy of it was saved as %2$s. "
            + "Your next change will overwrite %1$s.";
    static final String MESSAGE_UNREADABLE_DATA_NOT_BACKED_UP = "\u26a0 The data file %1$s could not be read, so "
            + "TutorLink started with no students.\nA backup copy could not be saved. "
            + "Your next change will overwrite %1$s, so copy it elsewhere first if you want to keep it.";
    private static final String MESSAGE_DATA_ALREADY_OPEN = "TutorLink is already using this data folder. "
            + "Close the other TutorLink window and try again.";
    private static final String MESSAGE_DATA_FOLDER_UNAVAILABLE = "TutorLink could not open its data folder safely. "
            + "Check that the folder is writable and try again.";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path TUTOR_LINK_FILE_PATH = Paths.get("data", "tutorlink.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    private Optional<String> startupWarning = Optional.empty();
    private Optional<String> startupError = Optional.empty();
    private DataFileLock dataFileLock;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing TutorLink ]===========================");
        super.init();

        if (!acquireDataFileLock(TUTOR_LINK_FILE_PATH)) {
            return;
        }
        try {
            JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
            UserPrefs userPrefs = initPrefs(userPrefsStorage);
            JsonTutorLinkStorage tutorLinkStorage = new JsonTutorLinkStorage(TUTOR_LINK_FILE_PATH);
            storage = new StorageManager(tutorLinkStorage, userPrefsStorage);

            model = initModelManager(storage, userPrefs);

            logic = new LogicManager(model, storage);

            ui = new UiManager(logic, storage.getTutorLinkFilePath(), startupWarning);
        } catch (RuntimeException | Error e) {
            releaseDataFileLock();
            throw e;
        }
    }

    /**
     * Acquires ownership before startup can read or modify persistent data.
     */
    private boolean acquireDataFileLock(Path dataFile) {
        try {
            dataFileLock = DataFileLock.tryAcquire(dataFile).orElse(null);
            if (dataFileLock == null) {
                startupError = Optional.of(MESSAGE_DATA_ALREADY_OPEN);
                logger.warning(MESSAGE_DATA_ALREADY_OPEN);
                return false;
            }
            return true;
        } catch (IOException e) {
            startupError = Optional.of(MESSAGE_DATA_FOLDER_UNAVAILABLE);
            logger.severe("Failed to acquire data file lock: " + StringUtil.getDetails(e));
            return false;
        }
    }

    /**
     * Releases any acquired lock after shutdown or incomplete startup.
     */
    private void releaseDataFileLock() {
        if (dataFileLock == null) {
            return;
        }
        try {
            dataFileLock.close();
        } catch (IOException e) {
            logger.severe("Failed to release data file lock: " + StringUtil.getDetails(e));
        } finally {
            dataFileLock = null;
        }
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s TutorLink and {@code userPrefs}. <br>
     * The data from the sample TutorLink will be used instead if {@code storage}'s TutorLink is not found,
     * or an empty TutorLink will be used instead if errors occur when reading {@code storage}'s TutorLink.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getTutorLinkFilePath());

        Optional<ReadOnlyTutorLink> tutorLinkOptional;
        ReadOnlyTutorLink initialData;
        try {
            tutorLinkOptional = storage.readTutorLink();
            if (tutorLinkOptional.isEmpty()) {
                logger.info("Data file " + storage.getTutorLinkFilePath() + " not found. Starting with sample data;"
                        + " the file will be created after the first command that changes data.");
            }
            initialData = tutorLinkOptional.orElseGet(SampleDataUtil::getSampleTutorLink);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getTutorLinkFilePath() + " could not be loaded."
                    + " Will be starting with an empty TutorLink.");
            initialData = new TutorLink();
            Path dataFile = storage.getTutorLinkFilePath();
            startupWarning = Optional.of(backUpUnreadableDataFile(dataFile)
                    .map(backup -> String.format(MESSAGE_UNREADABLE_DATA_BACKED_UP, dataFile, backup))
                    .orElseGet(() -> String.format(MESSAGE_UNREADABLE_DATA_NOT_BACKED_UP, dataFile)));
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Copies {@code dataFile} to a {@code .bak} file next to it, so the unreadable data survives
     * the next save. Returns the backup path, or an empty Optional if the copy failed.
     */
    static Optional<Path> backUpUnreadableDataFile(Path dataFile) {
        Path backup = dataFile.resolveSibling(dataFile.getFileName() + ".bak");
        try {
            Files.copy(dataFile, backup, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Backed up unreadable data file to " + backup);
            return Optional.of(backup);
        } catch (IOException e) {
            logger.warning("Failed to back up unreadable data file: " + StringUtil.getDetails(e));
            return Optional.empty();
        }
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting TutorLink " + MainApp.VERSION);
        if (startupError.isPresent()) {
            UiManager.showStartupErrorAndShutdown(primaryStage, startupError.get());
            return;
        }
        try {
            ui.start(primaryStage);
        } catch (RuntimeException | Error e) {
            releaseDataFileLock();
            throw e;
        }
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping TutorLink ] =============================");
        try {
            if (dataFileLock != null && storage != null && model != null) {
                storage.saveUserPrefs(model.getUserPrefs());
            }
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        } finally {
            releaseDataFileLock();
        }
    }
}
