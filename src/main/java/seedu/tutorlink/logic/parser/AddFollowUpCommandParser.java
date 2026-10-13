package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_REVIEW_DATE;

import java.time.LocalDate;

import seedu.tutorlink.logic.commands.AddFollowUpCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Name;

/**
 * Parses input arguments and creates a new {@link AddFollowUpCommand} object.
 */
public class AddFollowUpCommandParser implements Parser<AddFollowUpCommand> {

    /**
     * Parses the given arguments in the context of {@link AddFollowUpCommand}.
     *
     * @throws ParseException if the input does not conform to the expected format.
     */
    @Override
    public AddFollowUpCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_DESCRIPTION, PREFIX_REVIEW_DATE);
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
}
