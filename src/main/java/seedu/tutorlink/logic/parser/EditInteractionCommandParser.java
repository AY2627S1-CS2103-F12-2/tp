package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_TIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.commands.EditInteractionCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

/**
 * Parses arguments and creates an {@link EditInteractionCommand}.
 */
public class EditInteractionCommandParser implements Parser<EditInteractionCommand> {

    @Override
    public EditInteractionCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeWithTrailingPrefix(args, PREFIX_NOTE,
                PREFIX_NAME, PREFIX_INDEX, PREFIX_DATE, PREFIX_TIME);
        boolean hasEdit = arguments.getValue(PREFIX_DATE).isPresent()
                || arguments.getValue(PREFIX_TIME).isPresent()
                || arguments.getValue(PREFIX_NOTE).isPresent();
        if (arguments.getValue(PREFIX_NAME).isEmpty() || arguments.getValue(PREFIX_INDEX).isEmpty()
                || !hasEdit || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditInteractionCommand.MESSAGE_USAGE));
        }

        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_INDEX, PREFIX_DATE, PREFIX_TIME, PREFIX_NOTE);
        Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).get());
        Index index = ParserUtil.parseIndex(arguments.getValue(PREFIX_INDEX).get());
        Optional<LocalDate> date = Optional.empty();
        Optional<LocalTime> time = Optional.empty();
        Optional<String> note = Optional.empty();
        if (arguments.getValue(PREFIX_DATE).isPresent()) {
            date = Optional.of(ParserUtil.parseInteractionDate(arguments.getValue(PREFIX_DATE).get()));
        }
        if (arguments.getValue(PREFIX_TIME).isPresent()) {
            time = Optional.of(ParserUtil.parseInteractionTime(arguments.getValue(PREFIX_TIME).get()));
        }
        if (arguments.getValue(PREFIX_NOTE).isPresent()) {
            note = Optional.of(ParserUtil.parseInteractionNote(arguments.getValue(PREFIX_NOTE).get()));
        }
        return new EditInteractionCommand(name, index, date, time, note);
    }
}
