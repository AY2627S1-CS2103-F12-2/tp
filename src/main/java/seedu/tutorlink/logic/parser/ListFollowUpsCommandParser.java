package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.tutorlink.logic.commands.ListFollowUpsCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

/**
 * Parses input arguments and creates a new {@link ListFollowUpsCommand} object.
 */
public class ListFollowUpsCommandParser implements Parser<ListFollowUpsCommand> {

    /**
     * Parses the given arguments in the context of {@link ListFollowUpsCommand}.
     *
     * @throws ParseException if the input does not conform to the expected format.
     */
    @Override
    public ListFollowUpsCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_NAME);
        if (arguments.getValue(PREFIX_NAME).isEmpty() || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListFollowUpsCommand.MESSAGE_USAGE));
        }

        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME);
        Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).get());
        return new ListFollowUpsCommand(name);
    }
}
