package seedu.tutorlink;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.commons.util.DataFileLock;
import seedu.tutorlink.storage.JsonTutorLinkStorage;

/**
 * Uses separate JVMs to test application lock ownership, blocked startup and failure cleanup.
 */
public class MainAppLockTest {

    private static final String ALREADY_RUNNING_MESSAGE = "TutorLink is already using this data folder. "
            + "Close the other TutorLink window and try again.";
    private static final String FOLDER_UNAVAILABLE_MESSAGE = "TutorLink could not open its data folder safely. "
            + "Check that the folder is writable and try again.";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void startup_secondProcessBlocked_preservesFirstProcessData() throws Exception {
        Path folder = prepareFolder("shared");
        Path dataFile = folder.resolve("data/tutorlink.json");
        Path prefsFile = folder.resolve("preferences.json");
        try (AppProcess first = new AppProcess(folder, "hold")) {
            first.expect("READY");
            first.send("student add n/Session Alpha");
            first.expect("OK");
            byte[] savedData = Files.readAllBytes(dataFile);
            byte[] savedPrefs = Files.readAllBytes(prefsFile);

            try (AppProcess second = new AppProcess(folder, "probe")) {
                second.expect("ERROR: " + ALREADY_RUNNING_MESSAGE);
                second.expect("BLOCKED");
                second.awaitNormalExit();
            }

            assertArrayEquals(savedData, Files.readAllBytes(dataFile));
            assertArrayEquals(savedPrefs, Files.readAllBytes(prefsFile));
            first.send("student add n/Session Beta");
            first.expect("OK");
            first.exitNormally();
        }

        try (AppProcess reopened = new AppProcess(folder, "probe")) {
            reopened.expect("READY");
            reopened.awaitNormalExit();
        }
        assertEquals(List.of("Session Alpha", "Session Beta"), new JsonTutorLinkStorage(dataFile)
                .readTutorLink().orElseThrow().getStudentList().stream()
                .map(student -> student.getName().fullName).toList());
    }

    @Test
    public void startup_distinctFolders_allowsIndependentProcesses() throws Exception {
        Path firstFolder = prepareFolder("first");
        Path secondFolder = prepareFolder("second");
        try (AppProcess first = new AppProcess(firstFolder, "hold")) {
            first.expect("READY");
            try (AppProcess second = new AppProcess(secondFolder, "probe")) {
                second.expect("READY");
                second.awaitNormalExit();
            }
            first.exitNormally();
        }
    }

    @Test
    public void startup_forceTerminatedOwner_allowsRelaunch() throws Exception {
        Path folder = prepareFolder("terminated");
        try (AppProcess first = new AppProcess(folder, "hold")) {
            first.expect("READY");
            first.send("student add n/Session Alpha");
            first.expect("OK");
            first.terminate();
        }

        try (AppProcess reopened = new AppProcess(folder, "probe")) {
            reopened.expect("READY");
            reopened.awaitNormalExit();
        }
        assertEquals("Session Alpha", new JsonTutorLinkStorage(folder.resolve("data/tutorlink.json"))
                .readTutorLink().orElseThrow().getStudentList().getFirst().getName().fullName);
    }

    @Test
    public void startup_failureReleasesLock_beforeProcessExit() throws Exception {
        for (String mode : List.of("fail-init", "fail-start")) {
            Path folder = prepareFolder(mode);
            Path dataFile = folder.resolve("data/tutorlink.json");
            Path prefsFile = folder.resolve("preferences.json");
            try (AppProcess failed = new AppProcess(folder, mode)) {
                failed.expect("FAILED");
                try (DataFileLock nextOwner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
                    String replacementPrefs = "{  }\n";
                    Files.writeString(prefsFile, replacementPrefs);
                    failed.exitNormally();

                    assertEquals(replacementPrefs, Files.readString(prefsFile));
                }
            }
        }
    }

    @Test
    public void startup_localRejectedAttempts_preserveNativeLock() throws Exception {
        Path folder = prepareFolder("local-owner");
        Path dataFile = folder.resolve("data/tutorlink.json");
        try (DataFileLock owner = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            try (AppProcess second = new AppProcess(folder, "probe")) {
                second.expect("ERROR: " + ALREADY_RUNNING_MESSAGE);
                second.expect("BLOCKED");
                second.awaitNormalExit();
            }
        }
    }

    @Test
    public void startup_lockIoFailure_preservesFiles() throws Exception {
        Path folder = prepareFolder("unavailable");
        Path dataFile = folder.resolve("data/tutorlink.json");
        Path prefsFile = folder.resolve("preferences.json");
        Files.writeString(dataFile, "null");
        Files.createDirectory(folder.resolve("data/tutorlink.json.lock"));
        byte[] originalData = Files.readAllBytes(dataFile);
        byte[] originalPrefs = Files.readAllBytes(prefsFile);

        try (AppProcess application = new AppProcess(folder, "probe")) {
            application.expect("ERROR: " + FOLDER_UNAVAILABLE_MESSAGE);
            application.expect("BLOCKED");
            application.awaitNormalExit();
        }

        assertArrayEquals(originalData, Files.readAllBytes(dataFile));
        assertArrayEquals(originalPrefs, Files.readAllBytes(prefsFile));
        assertFalse(Files.exists(folder.resolve("data/tutorlink.json.bak")));
    }

    private Path prepareFolder(String name) throws IOException {
        Path folder = temporaryFolder.resolve(name);
        Files.createDirectories(folder.resolve("data"));
        Files.writeString(folder.resolve("data/tutorlink.json"), "{\"students\":[]}\n");
        Files.writeString(folder.resolve("preferences.json"), "{ }\n");
        return folder;
    }

    /**
     * Owns a test child process, with bounded waits and forced cleanup if an assertion fails.
     */
    private static class AppProcess implements AutoCloseable {
        private final Process process;
        private final Path errorLog;
        private final BufferedWriter commandWriter;
        private final BufferedReader signalReader;
        private final ExecutorService outputTasks = Executors.newVirtualThreadPerTaskExecutor();

        AppProcess(Path folder, String mode) throws IOException {
            String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
            Path java = Path.of(System.getProperty("java.home"), "bin", executable);
            String classpath = System.getProperty("tutorlink.test.runtimeClasspath",
                    System.getProperty("java.class.path"));
            errorLog = Files.createTempFile(folder, "process-", ".log");
            List<String> arguments = new ArrayList<>();
            arguments.add(java.toString());
            // Keep coverage from child JVMs in the same JaCoCo execution data as the parent test.
            ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                    .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                    .map(AppProcess::resolveCoveragePath)
                    .forEach(arguments::add);
            arguments.addAll(List.of("-cp", classpath, MainAppLockProcess.class.getName(), mode));
            process = new ProcessBuilder(arguments)
                    .directory(folder.toFile()).redirectError(errorLog.toFile()).start();
            commandWriter = process.outputWriter(StandardCharsets.UTF_8);
            signalReader = process.inputReader(StandardCharsets.UTF_8);
        }

        private static String resolveCoveragePath(String argument) {
            Matcher destination = Pattern.compile("destfile=([^,]+)").matcher(argument);
            if (!destination.find()) {
                return argument;
            }
            Path absolutePath = Path.of(destination.group(1)).toAbsolutePath();
            return destination.replaceFirst(Matcher.quoteReplacement("destfile=" + absolutePath));
        }

        void expect(String expected) throws Exception {
            Future<String> signal = outputTasks.submit(signalReader::readLine);
            try {
                assertEquals(expected, signal.get(15, TimeUnit.SECONDS), Files.readString(errorLog));
            } catch (TimeoutException e) {
                throw new AssertionError("Timed out waiting for " + expected + "\n" + Files.readString(errorLog), e);
            }
        }

        void send(String command) throws IOException {
            commandWriter.write(command);
            commandWriter.newLine();
            commandWriter.flush();
        }

        void exitNormally() throws Exception {
            send("exit");
            awaitNormalExit();
        }

        void awaitNormalExit() throws Exception {
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Child process did not exit");
            assertEquals(0, process.exitValue(), Files.readString(errorLog));
        }

        void terminate() throws InterruptedException {
            process.destroyForcibly();
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Child process did not terminate");
        }

        @Override
        public void close() throws Exception {
            try {
                if (process.isAlive()) {
                    terminate();
                }
            } finally {
                outputTasks.shutdownNow();
                commandWriter.close();
                signalReader.close();
            }
        }
    }
}
