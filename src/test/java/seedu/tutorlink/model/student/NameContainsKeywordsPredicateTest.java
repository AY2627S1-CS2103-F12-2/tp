package seedu.tutorlink.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.testutil.StudentBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different student -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Bob").build()));

        // Decomposed accent in keyword, composed accent in name
        predicate = new NameContainsKeywordsPredicate(List.of("Jose\u0301"));
        assertTrue(predicate.test(new StudentBuilder().withName("Jos\u00e9 Tan").build()));

        // Whole word, hyphenated part, or part between punctuation
        Student student = new StudentBuilder().withName("O'Connor-Jones").build();
        assertTrue(new NameContainsKeywordsPredicate(List.of("o'connor-jones")).test(student));
        assertTrue(new NameContainsKeywordsPredicate(List.of("O'Connor")).test(student));
        assertTrue(new NameContainsKeywordsPredicate(List.of("connor")).test(student));
        assertTrue(new NameContainsKeywordsPredicate(List.of("JONES")).test(student));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new StudentBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new StudentBuilder().withName("Alice Bob").build()));

        // Part of a word between punctuation is still not a match
        predicate = new NameContainsKeywordsPredicate(List.of("Conn"));
        assertFalse(predicate.test(new StudentBuilder().withName("O'Connor-Jones").build()));

        // Keywords match subjects, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("Math", "Physics"));
        assertFalse(predicate.test(new StudentBuilder().withName("Alice").withSubjects("Math", "Physics").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
