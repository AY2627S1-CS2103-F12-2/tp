package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.commands.ListFollowUpsCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

public class ListFollowUpsCommandParserTest {

    private final ListFollowUpsCommandParser parser = new ListFollowUpsCommandParser();

    @Test
    public void parse_validName_returnsCommand() throws Exception {
        assertEquals(new ListFollowUpsCommand(new Name("John Tan")), parser.parse(" n/John Tan "));
    }

    @Test
    public void parse_missingPrefixOrPreamble_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListFollowUpsCommand.MESSAGE_USAGE);
        for (String args : new String[]{"", "John Tan", " unexpected n/John Tan"}) {
            assertThrows(ParseException.class, message, () -> parser.parse(args));
        }
    }

    @Test
    public void parse_blankOrInvalidName_throwsParseException() {
        for (String args : new String[]{" n/ ", " n/123"}) {
            assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> parser.parse(args));
        }
    }

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" n/John Tan n/John Tan"));
    }

    @Test
    public void parseCommand_followUpList_recognisedIgnoringCaseAndSpacing() throws Exception {
        assertEquals(new ListFollowUpsCommand(new Name("John Tan")),
                new TutorLinkParser().parseCommand(" FOLLOWUP   LIST n/John Tan"));
    }
}
