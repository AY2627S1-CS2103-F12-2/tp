package seedu.tutorlink.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only disallowed characters
        assertFalse(Name.isValidName("peter*")); // contains a disallowed symbol
        assertFalse(Name.isValidName("peter the 2nd")); // contains a digit
        assertFalse(Name.isValidName("Ravi s/ Kumar")); // '/' outside a name connector
        assertFalse(Name.isValidName("Ravi a/o Kumar")); // not a name connector
        assertFalse(Name.isValidName("Ravi s/oKumar")); // connector joined to the next word
        assertFalse(Name.isValidName("-'.")); // no letter
        assertFalse(Name.isValidName(" Peter")); // leading space
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // too long

        // valid name
        assertTrue(Name.isValidName("A")); // one letter
        assertTrue(Name.isValidName("peter jack")); // letters and spaces
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("Mary-Anne")); // hyphen
        assertTrue(Name.isValidName("O'Brien")); // straight apostrophe
        assertTrue(Name.isValidName("O\u2019Brien")); // curly apostrophe
        assertTrue(Name.isValidName("John Tan Jr.")); // full stop
        assertTrue(Name.isValidName("José Müller")); // accented letters
        assertTrue(Name.isValidName("李小龙")); // non-Latin letters
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // maximum length
        assertTrue(Name.isValidName("Ravi s/o Kumar")); // son-of connector
        assertTrue(Name.isValidName("Siti D/O Ahmad")); // daughter-of connector in capitals
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }

    @Test
    public void isSameName() {
        Name name = new Name("John Tan");

        // same letters, same case -> returns true
        assertTrue(name.isSameName(new Name("John Tan")));

        // same letters, different case -> returns true
        assertTrue(name.isSameName(new Name("john TAN")));

        // extra spaces between and after words -> returns true
        assertTrue(name.isSameName(new Name("John  Tan ")));

        // different name -> returns false
        assertFalse(name.isSameName(new Name("John Tang")));

        // null -> returns false
        assertFalse(name.isSameName(null));
    }

    @Test
    public void isSameName_unicodeCaseVariants_returnsTrue() {
        String[][] caseVariants = {
            {"I\u015f\u0131k", "\u0131\u015f\u0131k"}, // Turkish dotless I
            {"\u0130pek", "ipek"}, // Turkish dotted I
            {"\u03a3", "\u03c2"}, // Greek capital and final sigma
            {"\uD801\uDC00", "\uD801\uDC28"} // Deseret capital and small letters
        };
        for (String[] caseVariant : caseVariants) {
            Name upperCaseName = new Name(caseVariant[0]);
            Name lowerCaseName = new Name(caseVariant[1]);

            assertTrue(upperCaseName.isSameName(lowerCaseName));
            assertTrue(lowerCaseName.isSameName(upperCaseName));
        }
    }

    @Test
    public void isSameName_unicodeCaseWithSpacingAndAccentNormalization_returnsTrue() {
        Name name = new Name("I\u015f\u0131k Jos\u00e9");
        Name differentlyTypedName = new Name("\u0131\u015f\u0131k   Jose\u0301 ");

        assertTrue(name.isSameName(differentlyTypedName));
    }

    @Test
    public void isSameName_distinctUnicodeLetters_returnsFalse() {
        assertFalse(new Name("Jos\u00e9").isSameName(new Name("Jose")));
        assertFalse(new Name("I\u015f\u0131k").isSameName(new Name("Isik")));
        assertFalse(new Name("Stra\u00dfe").isSameName(new Name("Strasse")));
    }

    @Test
    public void equals_unicodeCaseVariants_returnsFalse() {
        Name name = new Name("I\u015f\u0131k");
        Name caseVariant = new Name("\u0131\u015f\u0131k");

        assertTrue(name.isSameName(caseVariant));
        assertFalse(name.equals(caseVariant));
        assertEquals("I\u015f\u0131k", name.fullName);
        assertEquals("\u0131\u015f\u0131k", caseVariant.fullName);
    }

    @Test
    public void constructor_decomposedAccent_storesComposedForm() {
        String composed = "Jos\u00e9";
        String decomposed = "Jose\u0301";

        Name fromDecomposed = new Name(decomposed);

        assertEquals(composed, fromDecomposed.fullName);
        assertEquals(new Name(composed), fromDecomposed);
        assertTrue(new Name(composed).isSameName(fromDecomposed));
    }

    @Test
    public void isValidName_lengthCountedAfterComposition() {
        // 49 letters + a decomposed e-acute is 51 chars as typed but 50 once composed
        assertTrue(Name.isValidName("a".repeat(49) + "e\u0301"));
        assertFalse(Name.isValidName("a".repeat(50) + "e\u0301"));
    }
}
