package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.commands.DeleteInteractionCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

public class DeleteInteractionCommandParserTest {

    private final DeleteInteractionCommandParser parser = new DeleteInteractionCommandParser();

    @Test
    public void parse_validArguments_acceptsEitherOrderAndTrimsFields() throws Exception {
        DeleteInteractionCommand expected =
                new DeleteInteractionCommand(new Name("John Tan"), Index.fromOneBased(1));
        assertEquals(expected, parser.parse(" n/John Tan i/1"));
        assertEquals(expected, parser.parse(" i/ 1 n/ John Tan "));
        assertEquals(expected, new TutorLinkParser().parseCommand(" INTERACTION   DELETE n/John Tan i/1"));
    }

    @Test
    public void parse_missingOrRepeatedFields_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteInteractionCommand.MESSAGE_USAGE);
        for (String args : new String[]{"", " n/John Tan", " i/1", " extra n/John Tan i/1"}) {
            assertThrows(ParseException.class, message, () -> parser.parse(args));
        }
        for (String args : new String[]{" n/John Tan n/John Tan i/1", " n/John Tan i/1 i/1"}) {
            assertThrows(ParseException.class, () -> parser.parse(args));
        }
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String value : new String[]{"", "0", "-1", "1.5", "abc", "2147483648", "+1"}) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INDEX, () ->
                    parser.parse(" n/John Tan i/" + value));
        }
    }

    @Test
    public void parse_invalidName_throwsParseException() {
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> parser.parse(" n/ i/1"));
    }
}
