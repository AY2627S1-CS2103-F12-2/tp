package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.tutorlink.logic.commands.CommandTestUtil.INVALID_SUBJECT_DESC;
import static seedu.tutorlink.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.tutorlink.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.tutorlink.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH;
import static seedu.tutorlink.logic.commands.CommandTestUtil.SUBJECT_DESC_PHYSICS;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.tutorlink.testutil.TypicalStudents.AMY;
import static seedu.tutorlink.testutil.TypicalStudents.BOB;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.AddCommand;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;
import seedu.tutorlink.testutil.StudentBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Student expectedStudent = new StudentBuilder(BOB).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + SUBJECT_DESC_PHYSICS + SUBJECT_DESC_MATH,
                new AddCommand(expectedStudent));

        // subjects keep the order they were given in
        Student reorderedStudent = new StudentBuilder(BOB).withSubjects("Math", "Physics").build();
        assertParseSuccess(parser, NAME_DESC_BOB + SUBJECT_DESC_MATH + SUBJECT_DESC_PHYSICS,
                new AddCommand(reorderedStudent));
    }

    @Test
    public void parse_repeatedName_failure() {
        assertParseFailure(parser, NAME_DESC_AMY + NAME_DESC_BOB + SUBJECT_DESC_MATH,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid value followed by valid value
        assertParseFailure(parser, INVALID_NAME_DESC + NAME_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero subjects
        Student expectedStudent = new StudentBuilder(AMY).withSubjects().build();
        assertParseSuccess(parser, NAME_DESC_AMY, new AddCommand(expectedStudent));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + SUBJECT_DESC_MATH, expectedMessage);

        // empty input
        assertParseFailure(parser, "", expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + SUBJECT_DESC_MATH, Name.MESSAGE_CONSTRAINTS);

        // invalid subject
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_SUBJECT_DESC,
                String.format(ParserUtil.MESSAGE_INVALID_SUBJECT, "Math/Physics", Subject.MESSAGE_CONSTRAINTS));

        // same subject twice, ignoring case
        assertParseFailure(parser, NAME_DESC_BOB + SUBJECT_DESC_MATH + " s/math",
                String.format(ParserUtil.MESSAGE_DUPLICATE_SUBJECT, "math"));

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + SUBJECT_DESC_MATH,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
