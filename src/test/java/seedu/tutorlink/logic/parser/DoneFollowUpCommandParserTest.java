package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.commands.CancelFollowUpCommand;
import seedu.tutorlink.logic.commands.DoneFollowUpCommand;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

public class DoneFollowUpCommandParserTest {

    private final DoneFollowUpCommandParser parser = new DoneFollowUpCommandParser();

    @Test
    public void parse_validArguments_acceptsEitherOrderAndTrimsFields() throws Exception {
        DoneFollowUpCommand expected = new DoneFollowUpCommand(new Name("John Tan"), Index.fromOneBased(1));
        assertEquals(expected, parser.parse(" n/John Tan i/1"));
        assertEquals(expected, parser.parse(" i/ 1 n/ John Tan "));
        assertEquals(expected, new TutorLinkParser().parseCommand(" FOLLOWUP   DONE n/John Tan i/1"));
    }

    @Test
    public void parse_missingOrRepeatedFields_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneFollowUpCommand.MESSAGE_USAGE);
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

    @Test
    public void parseCommand_doneThenCancel_preservesDistinctStatusesAndHistory() throws Exception {
        LocalDate reviewDate = LocalDate.of(2026, 11, 1);
        FollowUp later = new FollowUp("Later", reviewDate.plusDays(1));
        FollowUp first = new FollowUp("First", reviewDate);
        FollowUp second = new FollowUp("Second", reviewDate);
        Student student = BENSON.withFollowUp(later).withFollowUp(first).withFollowUp(second);
        Model model = new ModelManager();
        model.addStudent(student);
        model.setSelectedStudent(student);
        TutorLinkParser commandParser = new TutorLinkParser();

        String doneMessage = commandParser.parseCommand("followup done n/" + student.getName() + " i/1")
                .execute(model).getFeedbackToUser();
        String cancelMessage = commandParser.parseCommand("followup cancel i/1 n/" + student.getName())
                .execute(model).getFeedbackToUser();

        assertEquals(String.format(DoneFollowUpCommand.MESSAGE_SUCCESS, student.getName(), reviewDate, "First", 2),
                doneMessage);
        assertEquals(String.format(CancelFollowUpCommand.MESSAGE_SUCCESS, student.getName(), reviewDate, "Second", 1),
                cancelMessage);
        Student expectedStudent = new Student(student.getName(), student.getSubjects(), student.getInteractions(),
                List.of(later, first.withStatus(FollowUpStatus.DONE), second.withStatus(FollowUpStatus.CANCELLED)));
        Student actualStudent = model.findStudentByName(student.getName()).orElseThrow();
        assertEquals(expectedStudent, actualStudent);
        assertEquals(expectedStudent, model.getSelectedStudent().get());
        assertEquals(List.of(later), actualStudent.getOutstandingFollowUps());
        assertEquals(List.of(later, first, second), student.getFollowUps());
    }
}
