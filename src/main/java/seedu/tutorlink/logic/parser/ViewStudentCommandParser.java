package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.tutorlink.logic.commands.ViewStudentCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

/**
 * Parses input arguments and creates a new ViewStudentCommand object
 */
public class ViewStudentCommandParser implements Parser<ViewStudentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewStudentCommand
     * and returns a ViewStudentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ViewStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME);

        if (argMultimap.getValue(PREFIX_NAME).isEmpty() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());

        return new ViewStudentCommand(name);
    }

}
