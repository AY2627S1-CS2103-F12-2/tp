package seedu.tutorlink.logic.commands;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Name;

/**
 * Cancels an outstanding follow-up while retaining its history.
 */
public class CancelFollowUpCommand extends ChangeFollowUpStatusCommand {

    public static final String COMMAND_WORD = "followup cancel";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " n/NAME i/INDEX\n"
            + "Example: " + COMMAND_WORD + " n/John Tan i/1";
    public static final String MESSAGE_SUCCESS = "\u2714 Follow-up cancelled for %1$s — review on %2$s: \"%3$s\"\n"
            + "Total outstanding follow-ups for %1$s: %4$d";

    /**
     * Creates a command that cancels the student's outstanding follow-up at the given displayed index.
     *
     * @param name The student's name.
     * @param index The index shown by {@code followup list}.
     */
    public CancelFollowUpCommand(Name name, Index index) {
        super(name, index, FollowUpStatus.CANCELLED, MESSAGE_SUCCESS);
    }
}
