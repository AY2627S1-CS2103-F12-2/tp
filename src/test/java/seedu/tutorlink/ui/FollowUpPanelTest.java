package seedu.tutorlink.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.followup.FollowUp;

public class FollowUpPanelTest {

    @Test
    public void describeReview_followUp_showsReviewDate() {
        FollowUp followUp = new FollowUp("Check vector drills", LocalDate.of(2026, 10, 2));
        assertEquals("Review 2026-10-02", FollowUpPanel.describeReview(followUp));
    }
}
