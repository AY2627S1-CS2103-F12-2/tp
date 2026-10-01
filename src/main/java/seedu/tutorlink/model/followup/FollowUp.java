package seedu.tutorlink.model.followup;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.util.Objects;

import seedu.tutorlink.commons.util.ToStringBuilder;

/**
 * Represents something a tutor intends to revisit with a student on a review date.
 * Guarantees: immutable; description is not blank and review date is not null.
 */
public class FollowUp {

    public static final String MESSAGE_DESCRIPTION_CONSTRAINTS = "Follow-up descriptions should not be blank.";

    private final String description;
    private final LocalDate reviewDate;

    /**
     * Creates a follow-up with the given description and review date.
     */
    public FollowUp(String description, LocalDate reviewDate) {
        requireNonNull(description);
        requireNonNull(reviewDate);
        checkArgument(!description.isBlank(), MESSAGE_DESCRIPTION_CONSTRAINTS);

        this.description = description.strip();
        this.reviewDate = reviewDate;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FollowUp otherFollowUp)) {
            return false;
        }
        return description.equals(otherFollowUp.description)
                && reviewDate.equals(otherFollowUp.reviewDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, reviewDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("description", description)
                .add("reviewDate", reviewDate)
                .toString();
    }
}
