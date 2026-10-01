package seedu.tutorlink.logic.commands;

import static seedu.tutorlink.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyTutorLink_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyTutorLink_success() {
        Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalTutorLink(), new UserPrefs());
        expectedModel.setTutorLink(new TutorLink());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
