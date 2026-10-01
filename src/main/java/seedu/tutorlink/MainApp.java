package seedu.tutorlink;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.commons.exceptions.DataLoadingException;
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

    public static final String VERSION = "V0.5.1";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path TUTOR_LINK_FILE_PATH = Paths.get("data", "tutorlink.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing TutorLink ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonTutorLinkStorage tutorLinkStorage = new JsonTutorLinkStorage(TUTOR_LINK_FILE_PATH);
        storage = new StorageManager(tutorLinkStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getTutorLinkFilePath());
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
                logger.info("Creating a new data file " + storage.getTutorLinkFilePath()
                        + " populated with a sample TutorLink.");
            }
            initialData = tutorLinkOptional.orElseGet(SampleDataUtil::getSampleTutorLink);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getTutorLinkFilePath() + " could not be loaded."
                    + " Will be starting with an empty TutorLink.");
            initialData = new TutorLink();
        }

        return new ModelManager(initialData, userPrefs);
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
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping TutorLink ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
