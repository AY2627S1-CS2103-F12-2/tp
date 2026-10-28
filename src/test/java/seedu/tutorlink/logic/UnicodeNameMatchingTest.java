package seedu.tutorlink.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.tutorlink.logic.commands.AddCommand;
import seedu.tutorlink.logic.commands.CommandResult;
import seedu.tutorlink.logic.commands.ViewStudentCommand;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.storage.JsonTutorLinkStorage;
import seedu.tutorlink.storage.JsonUserPrefsStorage;
import seedu.tutorlink.storage.StorageManager;

/**
 * Integration tests for Unicode case matching across student commands and JSON storage.
 */
public class UnicodeNameMatchingTest {

    private static final String STORED_NAME = "I\u015f\u0131k";
    private static final String CASE_VARIANT = "\u0131\u015f\u0131k";

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private StorageManager storage;
    private Path dataPath;

    @BeforeEach
    public void setUp() {
        dataPath = temporaryFolder.resolve("tutorlink.json");
        storage = new StorageManager(new JsonTutorLinkStorage(dataPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        model = new ModelManager();
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_findViewAndDuplicateAdd_useSameUnicodeCaseMatching() throws Exception {
        logic.execute("student add n/" + STORED_NAME);
        Student student = model.getTutorLink().getStudentList().getFirst();
        logic.execute("find " + CASE_VARIANT);
        assertEquals(List.of(student), model.getFilteredStudentList());

        CommandResult result = logic.execute("student view n/" + CASE_VARIANT);
        assertEquals(String.format(ViewStudentCommand.MESSAGE_SUCCESS, STORED_NAME, "(none recorded)"),
                result.getFeedbackToUser());
        assertEquals(student, model.getSelectedStudent().get());
        byte[] savedData = Files.readAllBytes(dataPath);

        String expectedMessage = String.format(AddCommand.MESSAGE_DUPLICATE_STUDENT, STORED_NAME);
        assertThrows(CommandException.class, expectedMessage, () ->
                logic.execute("student add n/" + CASE_VARIANT));
        logic.execute("find " + CASE_VARIANT);

        assertEquals(List.of(student), model.getFilteredStudentList());
        assertEquals(List.of(student), model.getTutorLink().getStudentList());
        assertEquals(student, model.getSelectedStudent().get());
        assertArrayEquals(savedData, Files.readAllBytes(dataPath));
    }

    @Test
    public void execute_unicodeCaseRename_preservesRecordsAfterReload() throws Exception {
        logic.execute("student add n/" + STORED_NAME);
        logic.execute("interaction add n/" + CASE_VARIANT + " " + PREFIX_DATE + "2026-10-01 note/Algebra");
        logic.execute("followup add n/" + CASE_VARIANT + " desc/Review algebra review/2026-10-20");
        logic.execute("find " + CASE_VARIANT);
        logic.execute("student view n/" + CASE_VARIANT);
        Student student = model.getSelectedStudent().get();
        Student expectedStudent = new Student(new Name(CASE_VARIANT), student.getSubjects(),
                student.getInteractions(), student.getFollowUps());

        logic.execute("student edit n/" + CASE_VARIANT + " new/" + CASE_VARIANT);

        assertEquals(expectedStudent, model.getSelectedStudent().get());
        assertEquals(List.of(expectedStudent), model.getFilteredStudentList());
        assertEquals(List.of(expectedStudent), model.getTutorLink().getStudentList());

        Model reloadedModel = new ModelManager(storage.readTutorLink().orElseThrow(), new UserPrefs());
        new ViewStudentCommand(new Name(STORED_NAME)).execute(reloadedModel);
        assertEquals(expectedStudent, reloadedModel.getSelectedStudent().get());
    }

    @Test
    public void execute_renameToUnicodeCaseVariantOfAnotherStudent_rejectsDuplicate() throws Exception {
        logic.execute("student add n/" + STORED_NAME);
        logic.execute("student add n/Other Student");
        TutorLink originalData = new TutorLink(model.getTutorLink());
        byte[] savedData = Files.readAllBytes(dataPath);

        String expectedMessage = String.format(AddCommand.MESSAGE_DUPLICATE_STUDENT, STORED_NAME);
        assertThrows(CommandException.class, expectedMessage, () ->
                logic.execute("student edit n/Other Student new/" + CASE_VARIANT));

        assertEquals(originalData, model.getTutorLink());
        assertArrayEquals(savedData, Files.readAllBytes(dataPath));
    }
}
