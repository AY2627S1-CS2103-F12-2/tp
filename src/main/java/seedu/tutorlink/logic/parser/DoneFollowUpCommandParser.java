package seedu.tutorlink.logic.parser;

import seedu.tutorlink.logic.commands.DoneFollowUpCommand;

/**
 * Parses arguments and creates a {@link DoneFollowUpCommand}.
 */
public class DoneFollowUpCommandParser extends ChangeFollowUpStatusCommandParser<DoneFollowUpCommand> {

    /**
     * Creates a parser for completing an outstanding follow-up.
     */
    public DoneFollowUpCommandParser() {
        super(DoneFollowUpCommand.MESSAGE_USAGE, DoneFollowUpCommand::new);
    }
}
