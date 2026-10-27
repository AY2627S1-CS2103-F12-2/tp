package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_REVIEW_DATE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.tutorlink.logic.commands.AddFollowUpCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Name;

/**
 * Parses input arguments and creates a new {@link AddFollowUpCommand} object.
 */
public class AddFollowUpCommandParser implements Parser<AddFollowUpCommand> {

    private static final Pattern PREFIX_PATTERN = Pattern.compile(" ("
            + Pattern.quote(PREFIX_NAME.getPrefix()) + "|"
            + Pattern.quote(PREFIX_DESCRIPTION.getPrefix()) + "|"
            + Pattern.quote(PREFIX_REVIEW_DATE.getPrefix()) + ")");
    private static final Pattern REVIEW_DATE_START_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}(?=\\s|$)");

    /**
     * Parses the given arguments in the context of {@link AddFollowUpCommand}.
     *
     * @throws ParseException if the input does not conform to the expected format.
     */
    @Override
    public AddFollowUpCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = tokenizeArguments(args);
        if (arguments.getValue(PREFIX_NAME).isEmpty()
                || arguments.getValue(PREFIX_DESCRIPTION).isEmpty()
                || arguments.getValue(PREFIX_REVIEW_DATE).isEmpty()
                || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddFollowUpCommand.MESSAGE_USAGE));
        }

        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DESCRIPTION, PREFIX_REVIEW_DATE);

        Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).get());
        String description = ParserUtil.parseFollowUpDescription(arguments.getValue(PREFIX_DESCRIPTION).get());
        LocalDate reviewDate = ParserUtil.parseReviewDate(arguments.getValue(PREFIX_REVIEW_DATE).get());
        return new AddFollowUpCommand(name, new FollowUp(description, reviewDate));
    }

    /**
     * Tokenizes the arguments while retaining nonnumeric review text inside a description when a separate
     * date-shaped argument exists. Empty and number-starting review values remain parameters, so duplicate
     * dates and malformed numeric dates cannot be silently absorbed into the description.
     */
    private ArgumentMultimap tokenizeArguments(String args) {
        List<PrefixPosition> positions = findPrefixPositions(args);
        boolean hasReviewDate = hasDateShapedReview(args, positions);
        List<PrefixPosition> parameters = new ArrayList<>();
        boolean inDescription = false;
        for (int index = 0; index < positions.size(); index++) {
            PrefixPosition position = positions.get(index);
            if (inDescription && hasReviewDate && position.prefix().equals(PREFIX_REVIEW_DATE)
                    && isLiteralReviewText(extractValue(args, positions, index))) {
                continue;
            }
            parameters.add(position);
            inDescription = position.prefix().equals(PREFIX_DESCRIPTION);
        }

        ArgumentMultimap arguments = new ArgumentMultimap();
        int preambleEnd = parameters.isEmpty() ? args.length() : parameters.getFirst().start();
        arguments.put(new Prefix(""), args.substring(0, preambleEnd).trim());
        for (int index = 0; index < parameters.size(); index++) {
            arguments.put(parameters.get(index).prefix(), extractValue(args, parameters, index));
        }
        return arguments;
    }

    /**
     * Finds the prefixes using the same preceding-space requirement as {@link ArgumentTokenizer}.
     */
    private List<PrefixPosition> findPrefixPositions(String args) {
        List<PrefixPosition> positions = new ArrayList<>();
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            positions.add(new PrefixPosition(new Prefix(matcher.group(1)), matcher.start(1)));
        }
        return positions;
    }

    /**
     * Checks for a date-shaped value without accepting it as a valid calendar date. Calendar validation still
     * belongs to {@link ParserUtil#parseReviewDate(String)}.
     */
    private boolean hasDateShapedReview(String args, List<PrefixPosition> positions) {
        for (int index = 0; index < positions.size(); index++) {
            if (positions.get(index).prefix().equals(PREFIX_REVIEW_DATE)
                    && REVIEW_DATE_START_PATTERN.matcher(extractValue(args, positions, index)).lookingAt()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the trimmed value between a prefix and the next prefix in the supplied list.
     */
    private String extractValue(String args, List<PrefixPosition> positions, int index) {
        PrefixPosition position = positions.get(index);
        int valueStart = position.start() + position.prefix().getPrefix().length();
        int valueEnd = index + 1 < positions.size() ? positions.get(index + 1).start() : args.length();
        return args.substring(valueStart, valueEnd).trim();
    }

    /**
     * Returns whether a review value can remain description text rather than an empty or numeric date parameter.
     */
    private boolean isLiteralReviewText(String value) {
        return !value.isEmpty() && !Character.isDigit(value.codePointAt(0));
    }

    /**
     * A prefix and its zero-based start position in the original arguments.
     *
     * @param prefix The recognized prefix.
     * @param start The index of the first character of the prefix.
     */
    private record PrefixPosition(Prefix prefix, int start) {}
}
