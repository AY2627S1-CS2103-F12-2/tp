package seedu.tutorlink.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;

public class JsonAdaptedFollowUpTest {

    @Test
    public void toModelType_eachStatus_preservesStatus() throws Exception {
        for (FollowUpStatus status : FollowUpStatus.values()) {
            FollowUp followUp = new FollowUp("Review algebra", LocalDate.of(2026, 10, 15), status);
            assertEquals(followUp, new JsonAdaptedFollowUp(followUp).toModelType());
            assertEquals(followUp,
                    new JsonAdaptedFollowUp("Review algebra", "2026-10-15", status.name()).toModelType());
        }
    }

    @Test
    public void toModelType_missingStatus_defaultsToOutstanding() throws Exception {
        assertEquals(FollowUpStatus.OUTSTANDING,
                new JsonAdaptedFollowUp("Review algebra", "2026-10-15").toModelType().getStatus());
    }

    @Test
    public void toModelType_invalidStatus_throwsIllegalValueException() {
        for (String status : new String[]{"", "done", "UNKNOWN", " DONE "}) {
            assertThrows(IllegalValueException.class, JsonAdaptedFollowUp.INVALID_STATUS_MESSAGE, () ->
                    new JsonAdaptedFollowUp("Review algebra", "2026-10-15", status).toModelType());
        }
    }

    @Test
    public void toModelType_validValues_returnsFollowUp() throws Exception {
        FollowUp followUp = new FollowUp("Review algebra", LocalDate.of(2026, 10, 15));
        assertEquals(followUp, new JsonAdaptedFollowUp(followUp).toModelType());
        assertEquals(followUp, new JsonAdaptedFollowUp("  Review algebra  ", "2026-10-15").toModelType());
    }

    @Test
    public void toModelType_missingFields_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedFollowUp.MISSING_FIELD_MESSAGE_FORMAT, "description"), () ->
                        new JsonAdaptedFollowUp(null, "2026-10-15").toModelType());
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedFollowUp.MISSING_FIELD_MESSAGE_FORMAT, "reviewDate"), () ->
                        new JsonAdaptedFollowUp("Review algebra", null).toModelType());
    }

    @Test
    public void toModelType_blankDescription_throwsIllegalValueException() {
        for (String description : new String[]{"", " \t "}) {
            assertThrows(IllegalValueException.class, FollowUp.MESSAGE_DESCRIPTION_CONSTRAINTS, () ->
                    new JsonAdaptedFollowUp(description, "2026-10-15").toModelType());
        }
    }

    @Test
    public void toModelType_descriptionLength_maxAcceptedAndOverlongRejected() throws Exception {
        String maxLength = "x".repeat(FollowUp.MAX_DESCRIPTION_LENGTH);
        assertEquals(maxLength, new JsonAdaptedFollowUp(" " + maxLength + " ", "2026-10-15")
                .toModelType().getDescription());
        assertThrows(IllegalValueException.class, FollowUp.MESSAGE_LONG_DESCRIPTION, () ->
                new JsonAdaptedFollowUp(maxLength + "x", "2026-10-15").toModelType());
    }

    @Test
    public void toModelType_unicodeDescription_countsCodePointsAfterTrimming() throws Exception {
        String emoji = "\uD83D\uDE00";
        for (String description : new String[]{emoji.repeat(200), "a".repeat(199) + emoji}) {
            assertEquals(description, new JsonAdaptedFollowUp("  " + description + "  ", "2026-10-15")
                    .toModelType().getDescription());
            assertThrows(IllegalValueException.class, FollowUp.MESSAGE_LONG_DESCRIPTION, () ->
                    new JsonAdaptedFollowUp(description + emoji, "2026-10-15").toModelType());
        }
    }

    @Test
    public void toModelType_multilineDescription_throwsIllegalValueException() {
        for (String description : new String[]{"Review\nalgebra", "Review\ralgebra", "Review\r\nalgebra"}) {
            assertThrows(IllegalValueException.class, FollowUp.MESSAGE_MULTILINE_DESCRIPTION, () ->
                    new JsonAdaptedFollowUp(description, "2026-10-15").toModelType());
        }
    }

    @Test
    public void toModelType_invalidReviewDate_throwsIllegalValueException() {
        for (String date : new String[]{"", "2026/10/15", "2026-2-01", "2026-02-30", "2026-13-01"}) {
            assertThrows(IllegalValueException.class, JsonAdaptedFollowUp.INVALID_REVIEW_DATE_MESSAGE, () ->
                    new JsonAdaptedFollowUp("Review algebra", date).toModelType());
        }
    }

    @Test
    public void toModelType_pastTodayAndFutureReviewDates_allowed() throws Exception {
        LocalDate today = LocalDate.now();
        for (LocalDate date : new LocalDate[]{today.minusDays(1), today, today.plusDays(1)}) {
            assertEquals(date,
                    new JsonAdaptedFollowUp("Review algebra", date.toString()).toModelType().getReviewDate());
        }
    }
}
