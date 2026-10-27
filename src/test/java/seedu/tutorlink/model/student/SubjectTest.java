package seedu.tutorlink.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
    }

    @Test
    public void constructor_invalidSubject_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Subject(""));
    }

    @Test
    public void constructor_decomposedSubject_storesComposedForm() {
        Subject subject = new Subject("Franc\u0327ais");

        assertEquals("Fran\u00e7ais", subject.subjectName);
    }

    @Test
    public void isValidSubject() {
        // null subject
        assertThrows(NullPointerException.class, () -> Subject.isValidSubject(null));

        // invalid subjects
        assertFalse(Subject.isValidSubject("")); // empty string
        assertFalse(Subject.isValidSubject(" ")); // spaces only
        assertFalse(Subject.isValidSubject(" Math")); // leading space
        assertFalse(Subject.isValidSubject("Math/Physics")); // disallowed symbol
        assertFalse(Subject.isValidSubject("a".repeat(Subject.MAX_LENGTH + 1))); // too long

        // valid subjects
        assertTrue(Subject.isValidSubject("M")); // one character
        assertTrue(Subject.isValidSubject("Math"));
        assertTrue(Subject.isValidSubject("A Level Physics")); // with spaces
        assertTrue(Subject.isValidSubject("H2 Math")); // with digits
        assertTrue(Subject.isValidSubject("Biology + Chemistry")); // with '+'
        assertTrue(Subject.isValidSubject("Science & Tech")); // with '&'
        assertTrue(Subject.isValidSubject("E-Math")); // with hyphen
        assertTrue(Subject.isValidSubject("Français")); // accented letter
        assertTrue(Subject.isValidSubject("a".repeat(Subject.MAX_LENGTH))); // maximum length
    }

    @Test
    public void isValidSubject_combiningMarks_returnsTrue() {
        assertTrue(Subject.isValidSubject("Franc\u0327ais"));
        assertTrue(Subject.isValidSubject("\u0ba4\u0bae\u0bbf\u0bb4\u0bcd")); // Tamil
    }

    @Test
    public void isValidSubject_normalizedLengthBoundary_returnsExpectedResult() {
        String decomposedSubject = "e\u0301".repeat(Subject.MAX_LENGTH);
        String supplementarySubject = "\uD840\uDC00".repeat(Subject.MAX_LENGTH);

        assertTrue(Subject.isValidSubject(decomposedSubject));
        assertEquals("\u00e9".repeat(Subject.MAX_LENGTH), new Subject(decomposedSubject).subjectName);
        assertFalse(Subject.isValidSubject(decomposedSubject + "e\u0301"));
        assertThrows(IllegalArgumentException.class, () -> new Subject(decomposedSubject + "e\u0301"));
        assertTrue(Subject.isValidSubject(supplementarySubject));
        assertFalse(Subject.isValidSubject(supplementarySubject + "\uD840\uDC00"));
    }

    @Test
    public void isValidSubject_disallowedCharacters_returnsFalse() {
        assertFalse(Subject.isValidSubject("\u0327")); // standalone combining mark
        assertFalse(Subject.isValidSubject("\u0327Math")); // leading combining mark
        assertFalse(Subject.isValidSubject("Math\u200b")); // zero-width space
        assertFalse(Subject.isValidSubject("Math\uD83D\uDE00")); // emoji
    }

    @Test
    public void isSameSubject() {
        Subject math = new Subject("Math");

        assertTrue(math.isSameSubject(new Subject("Math"))); // same case
        assertTrue(math.isSameSubject(new Subject("MATH"))); // different case
        assertFalse(math.isSameSubject(new Subject("Physics"))); // different subject
        assertFalse(math.isSameSubject(null)); // null
    }

    @Test
    public void isSameSubject_canonicallyEquivalentSubjects_returnsTrue() {
        Subject composedSubject = new Subject("Fran\u00e7ais");
        Subject decomposedSubject = new Subject("Franc\u0327ais");

        assertTrue(composedSubject.isSameSubject(decomposedSubject));
        assertTrue(composedSubject.isSameSubject(new Subject("FRANC\u0327AIS")));
        assertEquals(composedSubject, decomposedSubject);
        assertEquals(composedSubject.hashCode(), decomposedSubject.hashCode());
    }

    @Test
    public void equals() {
        Subject math = new Subject("Math");

        assertTrue(math.equals(new Subject("Math"))); // same values
        assertTrue(math.equals(math)); // same object
        assertFalse(math.equals(null)); // null
        assertFalse(math.equals(5.0f)); // different type
        assertFalse(math.equals(new Subject("Physics"))); // different values
    }
}
