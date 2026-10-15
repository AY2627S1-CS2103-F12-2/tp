package seedu.tutorlink.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.commons.util.AppUtil.checkArgument;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Represents a Student's name in TutorLink.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS = "Student name must be 1 to " + MAX_LENGTH
            + " characters, contain at least one letter, and use only letters, spaces, hyphens, apostrophes"
            + " or full stops.";

    /*
     * Letters include accented letters and combining marks (e.g. José). Both straight and curly apostrophes are
     * accepted (e.g. O'Brien). The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{M}'\u2019.-][\\p{L}\\p{M}'\u2019. -]*";
    private static final String LETTER_REGEX = ".*\\p{L}.*";

    public final String fullName;

    /**
     * Constructs a {@code Name}, stored in Unicode NFC form so that visually identical names
     * (e.g. a composed é and an e followed by a combining accent) are the same name.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        String composedName = toComposedForm(name);
        checkArgument(isValidName(composedName), MESSAGE_CONSTRAINTS);
        fullName = composedName;
    }

    /**
     * Returns true if a given string is a valid name. The length limit applies to the NFC form.
     */
    public static boolean isValidName(String test) {
        String composedTest = toComposedForm(test);
        return composedTest.length() <= MAX_LENGTH
                && composedTest.matches(VALIDATION_REGEX)
                && composedTest.matches(LETTER_REGEX);
    }

    /**
     * Returns {@code text} in Unicode NFC form.
     */
    public static String toComposedForm(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFC);
    }

    /**
     * Returns true if both names refer to the same student.
     * Names are compared ignoring case and differences in whitespace, as real-world names are not case-sensitive.
     *
     * @param otherName The name to compare with.
     */
    public boolean isSameName(Name otherName) {
        return otherName != null && normalize(fullName).equals(normalize(otherName.fullName));
    }

    private static String normalize(String name) {
        return name.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
