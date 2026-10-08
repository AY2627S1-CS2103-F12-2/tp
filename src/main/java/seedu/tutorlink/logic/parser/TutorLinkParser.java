package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.logic.commands.AddCommand;
import seedu.tutorlink.logic.commands.AddInteractionCommand;
import seedu.tutorlink.logic.commands.Command;
import seedu.tutorlink.logic.commands.DeleteCommand;
import seedu.tutorlink.logic.commands.ExitCommand;
import seedu.tutorlink.logic.commands.FindCommand;
import seedu.tutorlink.logic.commands.HelpCommand;
import seedu.tutorlink.logic.commands.ListCommand;
import seedu.tutorlink.logic.commands.ListInteractionsCommand;
import seedu.tutorlink.logic.commands.ViewStudentCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class TutorLinkParser {

    /**
     * Used for initial separation of command word and args.
     * A command word is either one word (e.g. {@code help}), or a domain followed by an action
     * (e.g. {@code student add}). Command words are not case-sensitive.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile(
            "(?<commandWord>(?:(?:student|interaction|followup)\\s+)?\\S+)(?<arguments>.*)",
            Pattern.CASE_INSENSITIVE);
    private static final Logger logger = LogsCenter.getLogger(TutorLinkParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = normalizeCommandWord(matcher.group("commandWord"));
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        return switch (commandWord) {
            case AddCommand.COMMAND_WORD -> new AddCommandParser().parse(arguments);
            case AddInteractionCommand.COMMAND_WORD -> new AddInteractionCommandParser().parse(arguments);
            case ViewStudentCommand.COMMAND_WORD -> new ViewStudentCommandParser().parse(arguments);
            case DeleteCommand.COMMAND_WORD -> new DeleteCommandParser().parse(arguments);
            case FindCommand.COMMAND_WORD -> new FindCommandParser().parse(arguments);
            case ListCommand.COMMAND_WORD -> new ListCommand();
            case ListInteractionsCommand.COMMAND_WORD -> new ListInteractionsCommandParser().parse(arguments);
            case ExitCommand.COMMAND_WORD -> new ExitCommand();
            case HelpCommand.COMMAND_WORD -> new HelpCommand();
            default -> {
                logger.finer("This user input caused a ParseException: " + userInput);
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        };
    }

    /**
     * Returns {@code commandWord} in lower case, with the domain and action separated by a single space.
     */
    private static String normalizeCommandWord(String commandWord) {
        return commandWord.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

}
