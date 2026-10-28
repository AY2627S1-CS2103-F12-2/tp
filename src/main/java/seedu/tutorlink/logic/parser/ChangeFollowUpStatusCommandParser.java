package seedu.tutorlink.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.function.BiFunction;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.commands.ChangeFollowUpStatusCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

/**
 * Parses the student name and displayed index shared by follow-up status commands.
 *
 * @param <T> The concrete status command created by this parser.
 */
public abstract class ChangeFollowUpStatusCommandParser<T extends ChangeFollowUpStatusCommand> implements Parser<T> {

    private final String messageUsage;
    private final BiFunction<Name, Index, T> commandFactory;

    /**
     * Creates a parser with the usage message and constructor for its concrete command.
     *
     * @param messageUsage The usage message shown for missing fields or an unexpected preamble.
     * @param commandFactory Creates the concrete command from a validated name and index.
     */
    protected ChangeFollowUpStatusCommandParser(String messageUsage, BiFunction<Name, Index, T> commandFactory) {
        this.messageUsage = requireNonNull(messageUsage);
        this.commandFactory = requireNonNull(commandFactory);
    }

    /**
     * Parses a student name and displayed follow-up index.
     *
     * @param args The arguments after the command words.
     * @return the concrete command with a validated name and index.
     * @throws ParseException if required fields are missing, duplicated, or invalid.
     */
    @Override
    public T parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_INDEX);
        if (arguments.getValue(PREFIX_NAME).isEmpty() || arguments.getValue(PREFIX_INDEX).isEmpty()
                || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }

        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_INDEX);
        Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).get());
        Index index = ParserUtil.parseIndex(arguments.getValue(PREFIX_INDEX).get());
        return commandFactory.apply(name, index);
    }
}
