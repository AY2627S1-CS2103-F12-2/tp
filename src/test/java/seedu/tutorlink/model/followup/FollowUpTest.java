package seedu.tutorlink.model.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class FollowUpTest {

    private static final LocalDate REVIEW_DATE = LocalDate.of(2026, 10, 8);

    @Test
    public void constructor_withoutStatus_defaultsToOutstanding() {
        FollowUp followUp = new FollowUp("Review algebra", REVIEW_DATE);
        assertEquals(FollowUpStatus.OUTSTANDING, followUp.getStatus());
        assertTrue(followUp.isOutstanding());
    }

    @Test
    public void withStatus_preservesDetailsWithoutChangingOriginal() {
        FollowUp original = new FollowUp("Review algebra", REVIEW_DATE);
        for (FollowUpStatus status : FollowUpStatus.values()) {
            FollowUp updated = original.withStatus(status);
            assertEquals(status, updated.getStatus());
            assertEquals(original.getDescription(), updated.getDescription());
            assertEquals(original.getReviewDate(), updated.getReviewDate());
            assertEquals(status == FollowUpStatus.OUTSTANDING, updated.isOutstanding());
            assertEquals(FollowUpStatus.OUTSTANDING, original.getStatus());
        }
        assertFalse(original.withStatus(FollowUpStatus.DONE).isOutstanding());
        assertFalse(original.withStatus(FollowUpStatus.CANCELLED).isOutstanding());
    }

    @Test
    public void constructorAndWithStatus_nullStatus_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FollowUp("Review algebra", REVIEW_DATE, null));
        assertThrows(NullPointerException.class, () -> new FollowUp("Review algebra", REVIEW_DATE).withStatus(null));
    }

    @Test
    public void equalsAndHashCode_includeStatus() {
        FollowUp original = new FollowUp("Review algebra", REVIEW_DATE);
        for (FollowUpStatus status : FollowUpStatus.values()) {
            FollowUp first = original.withStatus(status);
            FollowUp second = new FollowUp("Review algebra", REVIEW_DATE, status);
            assertEquals(first, second);
            assertEquals(first.hashCode(), second.hashCode());
        }
        assertNotEquals(original, original.withStatus(FollowUpStatus.DONE));
        assertNotEquals(original.withStatus(FollowUpStatus.DONE), original.withStatus(FollowUpStatus.CANCELLED));
    }

    @Test
    public void constructor_nullValues_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FollowUp(null, REVIEW_DATE));
        assertThrows(NullPointerException.class, () -> new FollowUp("Revise fractions", null));
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new FollowUp("", REVIEW_DATE));
        assertThrows(IllegalArgumentException.class, () -> new FollowUp("  \t  ", REVIEW_DATE));
    }

    @Test
    public void constructor_validValues_storesDescriptionAndReviewDate() {
        FollowUp followUp = new FollowUp("  Revise fractions  ", REVIEW_DATE);

        assertEquals("Revise fractions", followUp.getDescription());
        assertEquals(REVIEW_DATE, followUp.getReviewDate());
    }

    @Test
    public void constructor_pastReviewDate_isAllowed() {
        LocalDate pastDate = LocalDate.of(2020, 1, 1);

        assertEquals(pastDate, new FollowUp("Review notes", pastDate).getReviewDate());
    }

    @Test
    public void equals() {
        FollowUp followUp = new FollowUp("Revise fractions", REVIEW_DATE);

        assertEquals(followUp, followUp);
        assertEquals(followUp, new FollowUp("Revise fractions", REVIEW_DATE));
        assertNotEquals(followUp, new FollowUp("Revise algebra", REVIEW_DATE));
        assertNotEquals(followUp, new FollowUp("Revise fractions", REVIEW_DATE.plusDays(1)));
        assertNotEquals(followUp, null);
        assertNotEquals(followUp, "Revise fractions");
    }

    @Test
    public void hashCode_equalFollowUps_haveSameHashCode() {
        FollowUp followUp = new FollowUp("Revise fractions", REVIEW_DATE);

        assertEquals(followUp.hashCode(), new FollowUp("Revise fractions", REVIEW_DATE).hashCode());
    }

    @Test
    public void reviewDateOrder_mixedDates_earliestFirstAndTiesKeepOrderAdded() {
        FollowUp later = new FollowUp("Later", REVIEW_DATE.plusDays(1));
        FollowUp firstAdded = new FollowUp("First added", REVIEW_DATE);
        FollowUp secondAdded = new FollowUp("Second added", REVIEW_DATE);

        assertEquals(List.of(firstAdded, secondAdded, later),
                Stream.of(later, firstAdded, secondAdded).sorted(FollowUp.REVIEW_DATE_ORDER).toList());
    }
}
