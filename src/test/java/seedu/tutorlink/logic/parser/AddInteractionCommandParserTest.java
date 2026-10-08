package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.commands.AddInteractionCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;

public class AddInteractionCommandParserTest {

    private final AddInteractionCommandParser parser = new AddInteractionCommandParser();

    @Test
    public void parse_allFieldsSpecified_returnsCommand() throws Exception {
        AddInteractionCommand expected = new AddInteractionCommand(new Name("Alex Tan"),
                new Interaction(LocalDate.of(2026, 10, 1), Optional.of(LocalTime.of(14, 30)),
                        "Practised algebraic fractions"));

        assertEquals(expected, parser.parse(
                " n/Alex Tan d/2026-10-01 t/14:30 note/Practised algebraic fractions"));
    }

    @Test
    public void parse_withoutTime_returnsCommandWithNoTime() throws Exception {
        AddInteractionCommand expected = new AddInteractionCommand(new Name("Alex Tan"),
                new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Discussed revision plan"));

        assertEquals(expected, parser.parse(" n/Alex Tan d/2026-10-01 note/Discussed revision plan"));
    }

    @Test
    public void parse_missingRequiredField_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddInteractionCommand.MESSAGE_USAGE);

        assertThrows(ParseException.class, expectedMessage, () ->
                parser.parse(" n/Alex Tan d/2026-10-01"));
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        assertThrows(ParseException.class, String.format(ParserUtil.MESSAGE_INVALID_CALENDAR_DATE, "2026-02-30"), () ->
                parser.parse(" n/Alex Tan d/2026-02-30 note/Discussed revision plan"));
    }

    @Test
    public void parse_malformedDate_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_DATE_FORMAT, () ->
                parser.parse(" n/Alex Tan d/2026/10/01 note/Discussed revision plan"));
    }

    @Test
    public void parse_invalidTime_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INTERACTION_TIME, () ->
                parser.parse(" n/Alex Tan d/2026-10-01 t/25:00 note/Discussed revision plan"));
    }

    @Test
    public void parse_blankNote_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INTERACTION_NOTE, () ->
                parser.parse(" n/Alex Tan d/2026-10-01 note/   "));
    }
}
