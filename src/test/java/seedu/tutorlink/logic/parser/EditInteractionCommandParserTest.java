package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.commands.EditInteractionCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;

public class EditInteractionCommandParserTest {

    private final EditInteractionCommandParser parser = new EditInteractionCommandParser();

    @Test
    public void parse_allFields_returnsCommand() throws Exception {
        EditInteractionCommand expected = new EditInteractionCommand(new Name("John Tan"), Index.fromOneBased(2),
                Optional.of(LocalDate.of(2026, 10, 2)), Optional.of(LocalTime.of(14, 30)),
                Optional.of("Corrected note"));
        assertEquals(expected, parser.parse(
                " i/2 n/John Tan date/2026-10-02 t/14:30 note/Corrected note"));
    }

    @Test
    public void parse_eachOptionalFieldAlone_returnsCommand() throws Exception {
        Name name = new Name("John Tan");
        Index index = Index.fromOneBased(1);
        assertEquals(new EditInteractionCommand(name, index, Optional.of(LocalDate.of(2026, 10, 2)),
                Optional.empty(), Optional.empty()), parser.parse(" n/John Tan i/1 date/2026-10-02"));
        assertEquals(new EditInteractionCommand(name, index, Optional.empty(),
                Optional.of(LocalTime.of(14, 30)), Optional.empty()),
                parser.parse(" n/John Tan i/1 t/14:30"));
        assertEquals(new EditInteractionCommand(name, index, Optional.empty(), Optional.empty(),
                Optional.of("Corrected note")), parser.parse(" n/John Tan i/1 note/Corrected note"));
    }

    @Test
    public void parse_emptyTime_returnsCommandThatRemovesTime() throws Exception {
        EditInteractionCommand expected = new EditInteractionCommand(new Name("John Tan"), Index.fromOneBased(1),
                Optional.empty(), Optional.empty(), true, Optional.empty());

        assertEquals(expected, parser.parse(" n/John Tan i/1 t/"));
    }

    @Test
    public void parse_emptyTimeWithOtherFields_returnsCommandThatRemovesTime() throws Exception {
        EditInteractionCommand expected = new EditInteractionCommand(new Name("John Tan"), Index.fromOneBased(1),
                Optional.of(LocalDate.of(2026, 10, 2)), Optional.empty(), true,
                Optional.of("Reviewed algebra"));

        assertEquals(expected, parser.parse(
                " n/John Tan i/1 date/2026-10-02 t/ note/Reviewed algebra"));
    }

    @Test
    public void parse_noteConsumesRemainingPrefixes() throws Exception {
        EditInteractionCommand expected = new EditInteractionCommand(new Name("John Tan"), Index.fromOneBased(1),
                Optional.empty(), Optional.empty(), Optional.of("Corrected date/2026-10-02 t/14:30"));
        assertEquals(expected, parser.parse(
                " n/John Tan i/1 note/Corrected date/2026-10-02 t/14:30"));
    }

    @Test
    public void parse_missingRequiredOrEditField_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditInteractionCommand.MESSAGE_USAGE);
        for (String args : new String[]{"", " n/John Tan i/1", " n/John Tan note/New",
            " i/1 note/New", " extra n/John Tan i/1 note/New"}) {
            assertThrows(ParseException.class, message, () -> parser.parse(args));
        }
    }

    @Test
    public void parse_duplicateOrInvalidFields_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" n/John Tan i/1 i/2 note/New"));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INDEX, () ->
                parser.parse(" n/John Tan i/0 note/New"));
        assertThrows(ParseException.class,
                String.format(ParserUtil.MESSAGE_INVALID_CALENDAR_DATE, "2026-02-30"), () ->
                        parser.parse(" n/John Tan i/1 date/2026-02-30"));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INTERACTION_TIME, () ->
                parser.parse(" n/John Tan i/1 t/25:00"));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INTERACTION_NOTE, () ->
                parser.parse(" n/John Tan i/1 note/   "));
    }
}
