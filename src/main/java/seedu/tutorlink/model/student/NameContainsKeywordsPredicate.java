package seedu.tutorlink.model.student;

import java.util.List;
import java.util.function.Predicate;

import seedu.tutorlink.commons.util.StringUtil;
import seedu.tutorlink.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s {@code Name} matches any of the keywords given.
 */
public class NameContainsKeywordsPredicate implements Predicate<Student> {
    private static final String HYPHEN_REGEX = "-";
    private static final String PUNCTUATION_REGEX = "[-'\u2019.]";

    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Student student) {
        String searchableName = toSearchableName(student.getName().fullName);
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(searchableName,
                        Name.toComposedForm(keyword)));
    }

    /**
     * Returns {@code fullName} followed by its parts, so that a keyword can match a whole word
     * such as {@code O'Connor-Jones}, a hyphenated part such as {@code O'Connor},
     * or a part between punctuation such as {@code Connor}.
     */
    private static String toSearchableName(String fullName) {
        return String.join(" ", fullName, fullName.replaceAll(HYPHEN_REGEX, " "),
                fullName.replaceAll(PUNCTUATION_REGEX, " "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
