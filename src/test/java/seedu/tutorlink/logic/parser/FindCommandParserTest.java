package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.commands.FindCommand;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_prefixKeyword_throwsParseException() {
        String expectedMessage = String.format(ParserUtil.MESSAGE_UNKNOWN_PREFIX, "n/") + "\n"
                + FindCommand.MESSAGE_USAGE;
        assertParseFailure(parser, "n/Ann", expectedMessage);
        assertParseFailure(parser, "Tan n/", expectedMessage);
    }

    @Test
    public void parse_nameConnectorKeyword_returnsFindCommand() {
        assertParseSuccess(parser, "s/o D/O",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("s/o", "D/O"))));
    }

}
