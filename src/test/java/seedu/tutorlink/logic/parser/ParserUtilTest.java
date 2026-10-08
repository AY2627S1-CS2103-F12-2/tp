package seedu.tutorlink.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.parser.ParserUtil.MESSAGE_DUPLICATE_SUBJECT;
import static seedu.tutorlink.logic.parser.ParserUtil.MESSAGE_INVALID_CALENDAR_DATE;
import static seedu.tutorlink.logic.parser.ParserUtil.MESSAGE_INVALID_DATE_FORMAT;
import static seedu.tutorlink.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.tutorlink.logic.parser.ParserUtil.MESSAGE_INVALID_SUBJECT;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Subject;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_SUBJECT = "Math/Physics";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_SUBJECT_1 = "Math";
    private static final String VALID_SUBJECT_2 = "A Level Physics";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_STUDENT, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_STUDENT, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseSubject_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSubject(null));
    }

    @Test
    public void parseSubject_invalidValue_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_SUBJECT, INVALID_SUBJECT, Subject.MESSAGE_CONSTRAINTS);
        assertThrows(ParseException.class, expectedMessage, () -> ParserUtil.parseSubject(INVALID_SUBJECT));
    }

    @Test
    public void parseSubject_validValueWithWhitespace_returnsTrimmedSubject() throws Exception {
        String subjectWithWhitespace = WHITESPACE + VALID_SUBJECT_1 + WHITESPACE;
        assertEquals(new Subject(VALID_SUBJECT_1), ParserUtil.parseSubject(subjectWithWhitespace));
    }

    @Test
    public void parseSubjects_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSubjects(null));
    }

    @Test
    public void parseSubjects_collectionWithInvalidSubject_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseSubjects(List.of(VALID_SUBJECT_1, INVALID_SUBJECT)));
    }

    @Test
    public void parseSubjects_sameSubjectDifferentCase_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_DUPLICATE_SUBJECT, "math");
        assertThrows(ParseException.class, expectedMessage, () -> ParserUtil.parseSubjects(List.of("Math", "math")));
    }

    @Test
    public void parseSubjects_emptyCollection_returnsEmptyList() throws Exception {
        assertTrue(ParserUtil.parseSubjects(List.of()).isEmpty());
    }

    @Test
    public void parseSubjects_collectionWithValidSubjects_returnsSubjectsInOrder() throws Exception {
        List<Subject> expectedSubjects = List.of(new Subject(VALID_SUBJECT_2), new Subject(VALID_SUBJECT_1));
        assertEquals(expectedSubjects, ParserUtil.parseSubjects(List.of(VALID_SUBJECT_2, VALID_SUBJECT_1)));
    }

    @Test
    public void parseReviewDate_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseReviewDate(null));
    }

    @Test
    public void parseReviewDate_malformedDate_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_DATE_FORMAT, () -> ParserUtil.parseReviewDate(""));
        assertThrows(ParseException.class, MESSAGE_INVALID_DATE_FORMAT, () -> ParserUtil.parseReviewDate("2026-1-08"));
        assertThrows(ParseException.class, MESSAGE_INVALID_DATE_FORMAT, () -> ParserUtil.parseReviewDate("2026/10/08"));
    }

    @Test
    public void parseReviewDate_nonexistentDate_throwsParseException() {
        String invalidDateMessage = String.format(MESSAGE_INVALID_CALENDAR_DATE, "2026-02-30");
        assertThrows(ParseException.class, invalidDateMessage, () -> ParserUtil.parseReviewDate("2026-02-30"));
        invalidDateMessage = String.format(MESSAGE_INVALID_CALENDAR_DATE, "2025-02-29");
        assertThrows(ParseException.class, invalidDateMessage, () -> ParserUtil.parseReviewDate("2025-02-29"));
    }

    @Test
    public void parseReviewDate_validDate_returnsLocalDate() throws Exception {
        assertEquals(LocalDate.of(2026, 10, 8), ParserUtil.parseReviewDate("2026-10-08"));
        assertEquals(LocalDate.of(2024, 2, 29), ParserUtil.parseReviewDate(" 2024-02-29 "));
        assertEquals(LocalDate.of(2020, 1, 1), ParserUtil.parseReviewDate("2020-01-01"));
    }

    @Test
    public void parseInteractionDate_validDate_returnsLocalDate() throws Exception {
        assertEquals(LocalDate.of(2026, 10, 1), ParserUtil.parseInteractionDate(" 2026-10-01 "));
    }

    @Test
    public void parseInteractionDate_malformedDate_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_DATE_FORMAT, () -> ParserUtil.parseInteractionDate(
                "2026/10/01"));
    }

    @Test
    public void parseInteractionDate_nonexistentDate_throwsParseException() {
        String invalidDateMessage = String.format(MESSAGE_INVALID_CALENDAR_DATE, "2026-02-30");
        assertThrows(ParseException.class, invalidDateMessage, () -> ParserUtil.parseInteractionDate(
                "2026-02-30"));
    }

    @Test
    public void parseInteractionTime_validTime_returnsLocalTime() throws Exception {
        assertEquals(LocalTime.of(14, 30), ParserUtil.parseInteractionTime(" 14:30 "));
    }

    @Test
    public void parseInteractionNote_blankNote_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INTERACTION_NOTE, () ->
                ParserUtil.parseInteractionNote("  "));
    }

    @Test
    public void parseInteractionNote_validBoundaryLengths_returnsTrimmedNote() throws Exception {
        String oneCharacterNote = "a";
        String maximumLengthNote = "a".repeat(ParserUtil.MAX_INTERACTION_NOTE_LENGTH);

        assertEquals(oneCharacterNote, ParserUtil.parseInteractionNote(oneCharacterNote));
        assertEquals(maximumLengthNote, ParserUtil.parseInteractionNote(maximumLengthNote));
    }

    @Test
    public void parseInteractionNote_tooLongNote_throwsParseException() {
        String tooLongNote = "a".repeat(ParserUtil.MAX_INTERACTION_NOTE_LENGTH + 1);

        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INTERACTION_NOTE_LENGTH, () ->
                ParserUtil.parseInteractionNote(tooLongNote));
    }

    @Test
    public void parseInteractionNote_multilineNote_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INTERACTION_NOTE_MULTILINE, () ->
                ParserUtil.parseInteractionNote("First line\nSecond line"));
    }

    @Test
    public void parseName_repeatedSpacesBetweenWords_returnsNameWithSingleSpaces() throws Exception {
        assertEquals(new Name("John Tan"), ParserUtil.parseName("  John   Tan "));
    }
}
