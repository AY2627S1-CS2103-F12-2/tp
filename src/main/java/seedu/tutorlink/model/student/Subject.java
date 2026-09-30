package seedu.tutorlink.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a subject that the tutor teaches a Student in TutorLink.
 * Guarantees: immutable; is valid as declared in {@link #isValidSubject(String)}
 */
public class Subject {

    public static final int MAX_LENGTH = 30;

    public static final String MESSAGE_CONSTRAINTS = "Subjects should be 1 to " + MAX_LENGTH
            + " characters long and use only letters, digits, spaces, hyphens, '+' or '&'.";

    /*
     * The first character of the subject must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N}+&-][\\p{L}\\p{N} +&-]{0," + (MAX_LENGTH - 1) + "}";

    public final String subjectName;

    /**
     * Constructs a {@code Subject}.
     *
     * @param subjectName A valid subject name.
     */
    public Subject(String subjectName) {
        requireNonNull(subjectName);
        checkArgument(isValidSubject(subjectName), MESSAGE_CONSTRAINTS);
        this.subjectName = subjectName;
    }

    /**
     * Returns true if a given string is a valid subject name.
     */
    public static boolean isValidSubject(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if both subjects have the same name, ignoring case.
     *
     * @param otherSubject The subject to compare with.
     */
    public boolean isSameSubject(Subject otherSubject) {
        return otherSubject != null
                && subjectName.toLowerCase(Locale.ROOT).equals(otherSubject.subjectName.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Subject otherSubject)) {
            return false;
        }

        return subjectName.equals(otherSubject.subjectName);
    }

    @Override
    public int hashCode() {
        return subjectName.hashCode();
    }

    @Override
    public String toString() {
        return subjectName;
    }

}
