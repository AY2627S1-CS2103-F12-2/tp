package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.commands.DoneFollowUpCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

/**
 * Parses arguments and creates a {@link DoneFollowUpCommand}.
 */
public class DoneFollowUpCommandParser implements Parser<DoneFollowUpCommand> {

    /**
     * Parses a student name and displayed follow-up index.
     *
     * @throws ParseException if required fields are missing, duplicated, or invalid.
     */
    @Override
    public DoneFollowUpCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_INDEX);
        if (arguments.getValue(PREFIX_NAME).isEmpty() || arguments.getValue(PREFIX_INDEX).isEmpty()
                || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneFollowUpCommand.MESSAGE_USAGE));
        }

        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_INDEX);
        Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).get());
        Index index = ParserUtil.parseIndex(arguments.getValue(PREFIX_INDEX).get());
        return new DoneFollowUpCommand(name, index);
    }
}
