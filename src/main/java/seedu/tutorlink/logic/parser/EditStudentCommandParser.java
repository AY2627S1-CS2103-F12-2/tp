package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NEW_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.List;
import java.util.Optional;

import seedu.tutorlink.logic.commands.EditStudentCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Subject;

/**
 * Parses input arguments and creates a new EditStudentCommand object.
 */
public class EditStudentCommandParser implements Parser<EditStudentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditStudentCommand
     * and returns an EditStudentCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public EditStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_NEW_NAME, PREFIX_SUBJECT);

        if (argMultimap.getValue(PREFIX_NAME).isEmpty() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_NEW_NAME);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Optional<Name> newName = parseNewName(argMultimap);
        Optional<List<Subject>> newSubjects = parseNewSubjects(argMultimap);

        if (newName.isEmpty() && newSubjects.isEmpty()) {
            throw new ParseException(EditStudentCommand.MESSAGE_NOTHING_TO_EDIT);
        }

        return new EditStudentCommand(name, newName, newSubjects);
    }

    private static Optional<Name> parseNewName(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> newName = argMultimap.getValue(PREFIX_NEW_NAME);
        if (newName.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseName(newName.get()));
    }

    private static Optional<List<Subject>> parseNewSubjects(ArgumentMultimap argMultimap) throws ParseException {
        List<String> subjects = argMultimap.getAllValues(PREFIX_SUBJECT);
        if (subjects.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseSubjects(subjects));
    }

}
