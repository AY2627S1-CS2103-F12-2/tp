package seedu.tutorlink.logic.parser;

import static seedu.tutorlink.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.tutorlink.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.tutorlink.logic.commands.CommandTestUtil.INVALID_SUBJECT_DESC;
import static seedu.tutorlink.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.tutorlink.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH;
import static seedu.tutorlink.logic.commands.CommandTestUtil.SUBJECT_DESC_PHYSICS;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_SUBJECT_MATH;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_SUBJECT_PHYSICS;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.tutorlink.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.tutorlink.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.tutorlink.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.tutorlink.testutil.TypicalIndexes.INDEX_THIRD_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.EditCommand;
import seedu.tutorlink.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Subject;
import seedu.tutorlink.testutil.EditStudentDescriptorBuilder;

public class EditCommandParserTest {

    private static final String SUBJECT_EMPTY = " " + PREFIX_SUBJECT;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + INVALID_SUBJECT_DESC,
                String.format(ParserUtil.MESSAGE_INVALID_SUBJECT, "Math/Physics", Subject.MESSAGE_CONSTRAINTS));

        // parsing an empty subject together with other subjects is not allowed
        assertParseFailure(parser, "1" + SUBJECT_DESC_MATH + SUBJECT_EMPTY,
                String.format(ParserUtil.MESSAGE_INVALID_SUBJECT, "", Subject.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_STUDENT;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY + SUBJECT_DESC_PHYSICS + SUBJECT_DESC_MATH;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY)
                .withSubjects(VALID_SUBJECT_PHYSICS, VALID_SUBJECT_MATH).build();
        assertParseSuccess(parser, userInput, new EditCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_STUDENT;
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY).build();
        assertParseSuccess(parser, targetIndex.getOneBased() + NAME_DESC_AMY,
                new EditCommand(targetIndex, descriptor));

        // subjects
        descriptor = new EditStudentDescriptorBuilder().withSubjects(VALID_SUBJECT_MATH).build();
        assertParseSuccess(parser, targetIndex.getOneBased() + SUBJECT_DESC_MATH,
                new EditCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_repeatedName_failure() {
        Index targetIndex = INDEX_FIRST_STUDENT;
        assertParseFailure(parser, targetIndex.getOneBased() + NAME_DESC_AMY + NAME_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_resetSubjects_success() {
        Index targetIndex = INDEX_THIRD_STUDENT;
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withSubjects().build();
        assertParseSuccess(parser, targetIndex.getOneBased() + SUBJECT_EMPTY, new EditCommand(targetIndex, descriptor));
    }
}
