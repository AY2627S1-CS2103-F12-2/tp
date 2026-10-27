package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.commands.CommandTestUtil.INVALID_SUBJECT_DESC;
import static seedu.tutorlink.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH;
import static seedu.tutorlink.logic.commands.CommandTestUtil.SUBJECT_DESC_PHYSICS;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NEW_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.EditStudentCommand;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Subject;

public class EditStudentCommandParserTest {

    private static final String NEW_NAME_DESC_BOB = " " + PREFIX_NEW_NAME + VALID_NAME_BOB;
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditStudentCommand.MESSAGE_USAGE);

    private EditStudentCommandParser parser = new EditStudentCommandParser();

    @Test
    public void parse_newNameOnly_success() {
        assertParseSuccess(parser, NAME_DESC_AMY + NEW_NAME_DESC_BOB, new EditStudentCommand(
                new Name(VALID_NAME_AMY), Optional.of(new Name(VALID_NAME_BOB)), Optional.empty()));
    }

    @Test
    public void parse_subjectsOnly_success() {
        List<Subject> subjects = List.of(new Subject("Math"), new Subject("Physics"));
        assertParseSuccess(parser, NAME_DESC_AMY + SUBJECT_DESC_MATH + SUBJECT_DESC_PHYSICS,
                new EditStudentCommand(new Name(VALID_NAME_AMY), Optional.empty(), Optional.of(subjects)));
    }

    @Test
    public void parse_unicodeSubjects_success() {
        String tamilSubject = "\u0ba4\u0bae\u0bbf\u0bb4\u0bcd";
        List<Subject> subjects = List.of(new Subject(tamilSubject), new Subject("Fran\u00e7ais"));
        String subjectArguments = " " + PREFIX_SUBJECT + tamilSubject
                + " " + PREFIX_SUBJECT + " Franc\u0327ais ";

        assertParseSuccess(parser, NAME_DESC_AMY + subjectArguments,
                new EditStudentCommand(new Name(VALID_NAME_AMY), Optional.empty(), Optional.of(subjects)));
    }

    @Test
    public void parse_canonicallyEquivalentSubjects_failure() {
        String subjectArguments = " " + PREFIX_SUBJECT + "Franc\u0327ais"
                + " " + PREFIX_SUBJECT + "FRAN\u00c7AIS";

        assertParseFailure(parser, NAME_DESC_AMY + subjectArguments,
                String.format(ParserUtil.MESSAGE_DUPLICATE_SUBJECT, "FRAN\u00c7AIS"));
    }

    @Test
    public void parse_newNameAndSubjects_success() {
        assertParseSuccess(parser, NAME_DESC_AMY + NEW_NAME_DESC_BOB + SUBJECT_DESC_MATH,
                new EditStudentCommand(new Name(VALID_NAME_AMY), Optional.of(new Name(VALID_NAME_BOB)),
                        Optional.of(List.of(new Subject("Math")))));
    }

    @Test
    public void parse_nothingToEdit_failure() {
        assertParseFailure(parser, NAME_DESC_AMY, EditStudentCommand.MESSAGE_NOTHING_TO_EDIT);
    }

    @Test
    public void parse_missingNameOrPreamble_failure() {
        assertParseFailure(parser, NEW_NAME_DESC_BOB, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1" + NAME_DESC_AMY + NEW_NAME_DESC_BOB, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicateNewName_failure() {
        assertParseFailure(parser, NAME_DESC_AMY + NEW_NAME_DESC_BOB + NEW_NAME_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NEW_NAME));
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, NAME_DESC_AMY + " " + PREFIX_NEW_NAME + "James&", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_AMY + INVALID_SUBJECT_DESC,
                String.format(ParserUtil.MESSAGE_INVALID_SUBJECT, "Math/Physics", Subject.MESSAGE_CONSTRAINTS));
    }
}
