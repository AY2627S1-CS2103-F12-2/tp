package seedu.tutorlink.logic.commands;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Name;

/**
 * Marks an outstanding follow-up as completed while retaining its history.
 */
public class DoneFollowUpCommand extends ChangeFollowUpStatusCommand {

    public static final String COMMAND_WORD = "followup done";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " n/NAME i/INDEX\n"
            + "Example: " + COMMAND_WORD + " n/John Tan i/1";
    public static final String MESSAGE_SUCCESS = "\u2714 Follow-up completed for %1$s — review on %2$s: \"%3$s\"\n"
            + "Total outstanding follow-ups for %1$s: %4$d";

    /**
     * Creates a command that completes the student's outstanding follow-up at the given displayed index.
     *
     * @param name The student's name.
     * @param index The index shown by {@code followup list}.
     */
    public DoneFollowUpCommand(Name name, Index index) {
        super(name, index, FollowUpStatus.DONE, MESSAGE_SUCCESS);
    }
}
