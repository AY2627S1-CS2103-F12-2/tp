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
        Optional<String> dateArgument = arguments.getValue(PREFIX_DATE);
        Optional<String> timeArgument = arguments.getValue(PREFIX_TIME);
        Optional<String> noteArgument = arguments.getValue(PREFIX_NOTE);
        boolean hasEdit = dateArgument.isPresent() || timeArgument.isPresent() || noteArgument.isPresent();
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
        if (dateArgument.isPresent()) {
            date = Optional.of(ParserUtil.parseInteractionDate(dateArgument.get()));
        }
        if (timeArgument.isPresent() && !timeArgument.get().isEmpty()) {
            time = Optional.of(ParserUtil.parseInteractionTime(timeArgument.get()));
        }
        if (noteArgument.isPresent()) {
            note = Optional.of(ParserUtil.parseInteractionNote(noteArgument.get()));
        }
        return new EditInteractionCommand(name, index, date, time, timeArgument.isPresent(), note);
    }
}
