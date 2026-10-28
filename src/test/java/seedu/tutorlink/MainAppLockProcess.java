package seedu.tutorlink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.storage.JsonUserPrefsStorage;

/**
 * Runs initialization and startup control flow without starting the JavaFX toolkit.
 * Receives commands on standard input so lock tests can control the process lifetime.
 */
public class MainAppLockProcess {

    private static final String INJECTED_FAILURE = "Injected startup failure";

    /**
     * Starts the requested application scenario and reports its state to the parent test.
     *
     * @param args The scenario: hold, probe, fail-init or fail-start.
     * @throws Exception if application initialization or a test command fails unexpectedly.
     */
    public static void main(String[] args) throws Exception {
        String mode = args[0];
        MainApp application = createApplication(mode);
        try {
            if (mode.equals("fail-init") || mode.equals("fail-start")) {
                try {
                    application.init();
                    if (mode.equals("fail-start")) {
                        application.ui = stage -> {
                            throw new IllegalStateException(INJECTED_FAILURE);
                        };
                        application.start(null);
                    }
                    throw new AssertionError("The injected failure was not raised");
                } catch (IllegalStateException e) {
                    if (!INJECTED_FAILURE.equals(e.getMessage())) {
                        throw e;
                    }
                }
                report("FAILED");
            } else {
                application.init();
                startWithoutWindows(application);
            }
            if (!mode.equals("probe")) {
                receiveCommands(application);
            }
        } finally {
            application.stop();
        }
    }

    private static MainApp createApplication(String mode) {
        if (mode.equals("fail-init")) {
            return new MainApp() {
                @Override
                protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
                    throw new IllegalStateException(INJECTED_FAILURE);
                }
            };
        }
        return new MainApp();
    }

    private static void startWithoutWindows(MainApp application) {
        List<String> errors = new ArrayList<>();
        boolean[] mainWindowStarted = {false};
        application.startupErrorHandler = (stage, message) -> errors.add(message);
        application.ui = stage -> mainWindowStarted[0] = true;

        application.start(null);

        if (application.model == null) {
            if (mainWindowStarted[0] || errors.size() != 1) {
                throw new AssertionError("Blocked startup must show one error and skip the main window");
            }
            report("ERROR: " + errors.getFirst());
            report("BLOCKED");
        } else {
            if (!mainWindowStarted[0] || !errors.isEmpty()) {
                throw new AssertionError("Successful startup must open the main window without errors");
            }
            report("READY");
        }
    }

    private static void receiveCommands(MainApp application) throws Exception {
        try (BufferedReader commands = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String command;
            while ((command = commands.readLine()) != null && !command.equals("exit")) {
                application.logic.execute(command);
                report("OK");
            }
        }
    }

    private static void report(String state) {
        System.out.println(state);
        System.out.flush();
    }
}
