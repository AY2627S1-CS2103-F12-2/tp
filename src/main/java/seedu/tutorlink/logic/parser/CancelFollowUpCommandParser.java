package seedu.tutorlink.logic.parser;

import seedu.tutorlink.logic.commands.CancelFollowUpCommand;

/**
 * Parses arguments and creates a {@link CancelFollowUpCommand}.
 */
public class CancelFollowUpCommandParser extends ChangeFollowUpStatusCommandParser<CancelFollowUpCommand> {

    /**
     * Creates a parser for cancelling an outstanding follow-up.
     */
    public CancelFollowUpCommandParser() {
        super(CancelFollowUpCommand.MESSAGE_USAGE, CancelFollowUpCommand::new);
    }
}
