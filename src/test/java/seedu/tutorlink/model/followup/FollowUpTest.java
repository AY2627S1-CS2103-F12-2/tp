package seedu.tutorlink.model.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class FollowUpTest {

    private static final LocalDate REVIEW_DATE = LocalDate.of(2026, 10, 8);

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
}
