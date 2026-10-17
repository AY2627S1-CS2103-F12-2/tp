package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.testutil.TypicalStudents.ALICE;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.parser.TutorLinkParser;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;
import seedu.tutorlink.model.student.Student;

/**
 * Regression tests for issue #57: the selected student must stay consistent with the shown list
 * across {@code student view}, {@code find} and {@code list}.
 */
public class SelectedStudentFilterTest {

    private final Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void viewThenFindOtherStudentThenList_selectionClearedAndStaysCleared() throws Exception {
        new ViewStudentCommand(BENSON.getName()).execute(model);
        assertEquals(BENSON, model.getSelectedStudent().get());

        new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice"))).execute(model);
        assertEquals(null, model.getSelectedStudent().get());

        new ListCommand().execute(model);
        assertEquals(null, model.getSelectedStudent().get());
    }

    @Test
    public void viewThenFindSameStudentThenList_selectionKept() throws Exception {
        new ViewStudentCommand(BENSON.getName()).execute(model);

        new FindCommand(new NameContainsKeywordsPredicate(List.of("Benson"))).execute(model);
        assertEquals(BENSON, model.getSelectedStudent().get());

        new ListCommand().execute(model);
        assertEquals(BENSON, model.getSelectedStudent().get());
    }

    @Test
    public void findThenViewHiddenStudent_filterResetAndStudentSelected() throws Exception {
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice"))).execute(model);

        new ViewStudentCommand(BENSON.getName()).execute(model);

        assertEquals(BENSON, model.getSelectedStudent().get());
        assertTrue(model.getFilteredStudentList().contains(BENSON));
        assertEquals(getTypicalTutorLink().getStudentList().size(), model.getFilteredStudentList().size());
    }

    @Test
    public void findThenViewShownStudent_filterKept() throws Exception {
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Benson"))).execute(model);

        new ViewStudentCommand(BENSON.getName()).execute(model);

        assertEquals(BENSON, model.getSelectedStudent().get());
        assertEquals(List.of(BENSON), model.getFilteredStudentList());
    }

    @Test
    public void viewThenFindOtherThenViewHiddenStudentWithRecords_recordsShownAndFilterReset() throws Exception {
        TutorLinkParser parser = new TutorLinkParser();
        parser.parseCommand("interaction add n/Benson Meier d/2026-10-01 note/Practised algebra").execute(model);
        parser.parseCommand("followup add n/Benson Meier desc/Review algebra review/2026-10-15").execute(model);
        parser.parseCommand("student view n/alice pauline").execute(model);
        parser.parseCommand("find alice").execute(model);
        assertEquals(ALICE, model.getSelectedStudent().get());

        parser.parseCommand("student view n/benson meier").execute(model);

        Student selected = model.getSelectedStudent().get();
        assertEquals(BENSON.getName(), selected.getName());
        assertEquals(1, selected.getInteractions().size());
        assertEquals(1, selected.getFollowUps().size());
        assertTrue(model.getFilteredStudentList().contains(selected));
        assertTrue(model.getFilteredStudentList().contains(ALICE));
    }
}
