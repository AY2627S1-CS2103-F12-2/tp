package seedu.tutorlink.model.student;

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
    public void isSameSubject() {
        Subject math = new Subject("Math");

        assertTrue(math.isSameSubject(new Subject("Math"))); // same case
        assertTrue(math.isSameSubject(new Subject("MATH"))); // different case
        assertFalse(math.isSameSubject(new Subject("Physics"))); // different subject
        assertFalse(math.isSameSubject(null)); // null
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
