package seedu.tutorlink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.storage.JsonUserPrefsStorage;

/**
 * Runs application initialization in a separate JVM without starting the JavaFX toolkit.
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
                report(application.model == null ? "BLOCKED" : "READY");
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
