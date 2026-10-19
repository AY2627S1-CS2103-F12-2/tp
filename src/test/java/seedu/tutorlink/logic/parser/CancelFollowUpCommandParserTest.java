package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.CancelFollowUpCommand;
import seedu.tutorlink.logic.commands.Command;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

public class CancelFollowUpCommandParserTest {

    private final CancelFollowUpCommandParser parser = new CancelFollowUpCommandParser();

    @Test
    public void parse_validArguments_acceptsEitherOrderAndTrimsFields() throws Exception {
        CancelFollowUpCommand expected = new CancelFollowUpCommand(new Name("John Tan"), Index.fromOneBased(1));
        assertEquals(expected, parser.parse(" n/John Tan i/1"));
        assertEquals(expected, parser.parse(" i/ 1 n/ John Tan "));
        assertEquals(new CancelFollowUpCommand(new Name("John Tan"), Index.fromOneBased(Integer.MAX_VALUE)),
                parser.parse(" n/John Tan i/2147483647"));
        assertEquals(expected, new TutorLinkParser().parseCommand(" FOLLOWUP   CANCEL n/John Tan i/1"));
    }

    @Test
    public void parse_missingFieldsOrPreamble_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, CancelFollowUpCommand.MESSAGE_USAGE);
        for (String args : new String[]{"", " n/John Tan", " i/1", " extra n/John Tan i/1"}) {
            assertThrows(ParseException.class, message, () -> parser.parse(args));
        }
    }

    @Test
    public void parse_repeatedFields_throwsParseException() {
        assertThrows(ParseException.class, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME), () ->
                parser.parse(" n/John Tan n/John Tan i/1"));
        assertThrows(ParseException.class, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INDEX), () ->
                parser.parse(" n/John Tan i/1 i/1"));
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

    @Test
    public void parseCommand_validInput_executeCancelsSelectedFollowUp() throws Exception {
        Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());
        LocalDate date = LocalDate.of(2026, 10, 20);
        FollowUp later = new FollowUp("Later review", date.plusDays(1));
        FollowUp earlier = new FollowUp("Earlier review", date);
        model.setStudent(BENSON, BENSON.withFollowUp(later).withFollowUp(earlier));

        Command command = new TutorLinkParser().parseCommand("followup cancel i/1 n/" + BENSON.getName());
        String expectedMessage = String.format(CancelFollowUpCommand.MESSAGE_SUCCESS, BENSON.getName(), date,
                earlier.getDescription(), 1);
        assertEquals(expectedMessage, command.execute(model).getFeedbackToUser());
        Student updated = model.findStudentByName(BENSON.getName()).orElseThrow();
        assertEquals(List.of(later, earlier.withStatus(FollowUpStatus.CANCELLED)), updated.getFollowUps());
        assertEquals(List.of(later), updated.getOutstandingFollowUps());
    }
}
