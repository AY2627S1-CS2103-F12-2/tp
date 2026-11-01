package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.tutorlink.logic.commands.FindCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /* A keyword starting with a parameter prefix such as "n/", other than the name connectors "s/o" and "d/o". */
    private static final Pattern PREFIX_KEYWORD_PATTERN = Pattern.compile("(?!(?i:[sd]/o)$)(\\p{Alpha}+/)");

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");
        verifyNoPrefixKeyword(nameKeywords);

        return new FindCommand(new NameContainsKeywordsPredicate(List.of(nameKeywords)));
    }

    /**
     * Rejects a keyword such as {@code n/Ann}, which no name can match, so the user learns that {@code find}
     * takes plain keywords instead of seeing an empty list.
     *
     * @throws ParseException if any keyword starts with a parameter prefix.
     */
    private static void verifyNoPrefixKeyword(String[] nameKeywords) throws ParseException {
        for (String keyword : nameKeywords) {
            Matcher matcher = PREFIX_KEYWORD_PATTERN.matcher(keyword);
            if (matcher.lookingAt()) {
                throw new ParseException(String.format(ParserUtil.MESSAGE_UNKNOWN_PREFIX, matcher.group(1))
                        + "\n" + FindCommand.MESSAGE_USAGE);
            }
        }
    }

}
