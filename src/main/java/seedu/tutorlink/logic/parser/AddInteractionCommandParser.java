package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_TIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import seedu.tutorlink.logic.commands.AddInteractionCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;

/**
 * Parses input arguments and creates a new {@link AddInteractionCommand} object.
 */
public class AddInteractionCommandParser implements Parser<AddInteractionCommand> {

    /**
     * Parses the given arguments in the context of {@link AddInteractionCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public AddInteractionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_DATE, PREFIX_TIME, PREFIX_NOTE);

        if (argMultimap.getValue(PREFIX_NAME).isEmpty()
                || argMultimap.getValue(PREFIX_DATE).isEmpty()
                || argMultimap.getValue(PREFIX_NOTE).isEmpty()
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddInteractionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DATE, PREFIX_TIME, PREFIX_NOTE);

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        LocalDate date = ParserUtil.parseInteractionDate(argMultimap.getValue(PREFIX_DATE).get());
        String note = ParserUtil.parseInteractionNote(argMultimap.getValue(PREFIX_NOTE).get());
        Optional<LocalTime> time = Optional.empty();
        if (argMultimap.getValue(PREFIX_TIME).isPresent()) {
            time = Optional.of(ParserUtil.parseInteractionTime(argMultimap.getValue(PREFIX_TIME).get()));
        }

        return new AddInteractionCommand(name, new Interaction(date, time, note));
    }
}
