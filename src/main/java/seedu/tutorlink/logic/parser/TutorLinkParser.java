package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.tutorlink.logic.Messages.MESSAGE_UNKNOWN_SUBCOMMAND;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.logic.commands.AddCommand;
import seedu.tutorlink.logic.commands.AddFollowUpCommand;
import seedu.tutorlink.logic.commands.AddInteractionCommand;
import seedu.tutorlink.logic.commands.CancelFollowUpCommand;
import seedu.tutorlink.logic.commands.Command;
import seedu.tutorlink.logic.commands.DeleteCommand;
import seedu.tutorlink.logic.commands.DeleteInteractionCommand;
import seedu.tutorlink.logic.commands.DoneFollowUpCommand;
import seedu.tutorlink.logic.commands.EditInteractionCommand;
import seedu.tutorlink.logic.commands.EditStudentCommand;
import seedu.tutorlink.logic.commands.ExitCommand;
import seedu.tutorlink.logic.commands.FindCommand;
import seedu.tutorlink.logic.commands.HelpCommand;
import seedu.tutorlink.logic.commands.ListCommand;
import seedu.tutorlink.logic.commands.ListFollowUpsCommand;
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
    /** Command words that start with a domain, in the order they are suggested to the user. */
    private static final List<String> DOMAIN_COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD, ViewStudentCommand.COMMAND_WORD, EditStudentCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD, AddInteractionCommand.COMMAND_WORD, ListInteractionsCommand.COMMAND_WORD,
            EditInteractionCommand.COMMAND_WORD, DeleteInteractionCommand.COMMAND_WORD,
            AddFollowUpCommand.COMMAND_WORD, ListFollowUpsCommand.COMMAND_WORD, DoneFollowUpCommand.COMMAND_WORD,
            CancelFollowUpCommand.COMMAND_WORD);
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
            case AddFollowUpCommand.COMMAND_WORD -> new AddFollowUpCommandParser().parse(arguments);
            case AddInteractionCommand.COMMAND_WORD -> new AddInteractionCommandParser().parse(arguments);
            case ViewStudentCommand.COMMAND_WORD -> new ViewStudentCommandParser().parse(arguments);
            case EditStudentCommand.COMMAND_WORD -> new EditStudentCommandParser().parse(arguments);
            case DeleteCommand.COMMAND_WORD -> new DeleteCommandParser().parse(arguments);
            case DeleteInteractionCommand.COMMAND_WORD -> new DeleteInteractionCommandParser().parse(arguments);
            case EditInteractionCommand.COMMAND_WORD -> new EditInteractionCommandParser().parse(arguments);
            case FindCommand.COMMAND_WORD -> new FindCommandParser().parse(arguments);
            case ListCommand.COMMAND_WORD -> new ListCommand();
            case ListFollowUpsCommand.COMMAND_WORD -> new ListFollowUpsCommandParser().parse(arguments);
            case DoneFollowUpCommand.COMMAND_WORD -> new DoneFollowUpCommandParser().parse(arguments);
            case CancelFollowUpCommand.COMMAND_WORD -> new CancelFollowUpCommandParser().parse(arguments);
            case ListInteractionsCommand.COMMAND_WORD -> new ListInteractionsCommandParser().parse(arguments);
            case ExitCommand.COMMAND_WORD -> new ExitCommand();
            case HelpCommand.COMMAND_WORD -> new HelpCommand();
            default -> {
                logger.finer("This user input caused a ParseException: " + userInput);
                throw new ParseException(getUnknownCommandMessage(commandWord));
            }
        };
    }

    /**
     * Returns the error message for an unknown {@code commandWord}. If it starts with a domain such as
     * {@code student}, the message lists the commands in that domain.
     */
    private static String getUnknownCommandMessage(String commandWord) {
        String domainWithSpace = commandWord.split(" ")[0] + " ";
        List<String> domainCommandWords = DOMAIN_COMMAND_WORDS.stream()
                .filter(word -> word.startsWith(domainWithSpace))
                .toList();
        if (domainCommandWords.isEmpty()) {
            return MESSAGE_UNKNOWN_COMMAND;
        }
        return String.format(MESSAGE_UNKNOWN_SUBCOMMAND, String.join(", ", domainCommandWords));
    }

    /**
     * Returns {@code commandWord} in lower case, with the domain and action separated by a single space.
     */
    private static String normalizeCommandWord(String commandWord) {
        return commandWord.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

}
