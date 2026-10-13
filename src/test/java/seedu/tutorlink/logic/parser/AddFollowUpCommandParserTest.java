package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.commands.AddFollowUpCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Name;

public class AddFollowUpCommandParserTest {

    private final AddFollowUpCommandParser parser = new AddFollowUpCommandParser();

    @Test
    public void parse_validArguments_trimsDescriptionAndAllowsAnyPrefixOrder() throws Exception {
        AddFollowUpCommand expected = new AddFollowUpCommand(new Name("John Tan"),
                new FollowUp("Review algebra", LocalDate.of(2026, 10, 15)));
        assertEquals(expected, parser.parse(" review/2026-10-15 desc/  Review algebra  n/John Tan"));
    }

    @Test
    public void parse_descriptionLengthBoundaries_success() throws Exception {
        for (int length : new int[]{1, 200}) {
            String description = "a".repeat(length);
            assertEquals(new AddFollowUpCommand(new Name("John Tan"),
                    new FollowUp(description, LocalDate.of(2026, 10, 15))),
                    parser.parse(" n/John Tan desc/" + description + " review/2026-10-15"));
        }
    }

    @Test
    public void parse_missingPrefixesOrPreamble_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddFollowUpCommand.MESSAGE_USAGE);
        for (String args : new String[]{"", " desc/Review algebra review/2026-10-15",
            " n/John Tan review/2026-10-15", " n/John Tan desc/Review algebra",
            " unexpected n/John Tan desc/Review algebra review/2026-10-15"}) {
            assertThrows(ParseException.class, message, () -> parser.parse(args));
        }
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        String args = " n/John Tan desc/Review algebra review/2026-10-15";
        for (String extra : new String[]{" n/John Tan", " desc/Review algebra", " review/2026-10-15"}) {
            assertThrows(ParseException.class, () -> parser.parse(args + extra));
        }
    }

    @Test
    public void parse_invalidName_throwsParseException() {
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () ->
                parser.parse(" n/ desc/Review algebra review/2026-10-15"));
    }

    @Test
    public void parse_invalidDescription_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_EMPTY_FOLLOW_UP_DESCRIPTION, () ->
                parser.parse(" n/John Tan desc/   review/2026-10-15"));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_LONG_FOLLOW_UP_DESCRIPTION, () ->
                parser.parse(" n/John Tan desc/" + "a".repeat(201) + " review/2026-10-15"));
        for (String newline : new String[]{"\n", "\r", "\r\n"}) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_MULTILINE_FOLLOW_UP_DESCRIPTION, () ->
                    parser.parse(" n/John Tan desc/First" + newline + "Second review/2026-10-15"));
        }
        assertThrows(NullPointerException.class, () -> ParserUtil.parseFollowUpDescription(null));
    }

    @Test
    public void parse_invalidReviewDate_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_DATE_FORMAT, () ->
                parser.parse(" n/John Tan desc/Review algebra review/15-10-2026"));
        assertThrows(ParseException.class, String.format(ParserUtil.MESSAGE_INVALID_CALENDAR_DATE, "2026-02-30"), () ->
                parser.parse(" n/John Tan desc/Review algebra review/2026-02-30"));
    }

    @Test
    public void parseCommand_followUpAdd_recognisedIgnoringCaseAndSpacing() throws Exception {
        AddFollowUpCommand expected = new AddFollowUpCommand(new Name("John Tan"),
                new FollowUp("Review algebra", LocalDate.of(2026, 10, 15)));
        assertEquals(expected, new TutorLinkParser().parseCommand(
                " FOLLOWUP   ADD n/John Tan desc/Review algebra review/2026-10-15"));
    }
}
