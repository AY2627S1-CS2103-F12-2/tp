package seedu.tutorlink.model.followup;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

import seedu.tutorlink.commons.util.ToStringBuilder;

/**
 * Represents something a tutor intends to revisit with a student on a review date.
 * Guarantees: immutable; description is not blank, and review date and status are not null.
 */
public class FollowUp {

    public static final String MESSAGE_DESCRIPTION_CONSTRAINTS = "Follow-up descriptions should not be blank.";
    /** Maximum number of Unicode code points in a description after surrounding whitespace is removed. */
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final String MESSAGE_LONG_DESCRIPTION =
            "Description must be " + MAX_DESCRIPTION_LENGTH + " characters or fewer.";
    public static final String MESSAGE_MULTILINE_DESCRIPTION = "Description must be on a single line.";

    /** Orders follow-ups by review date, earliest first. The sort is stable, so ties keep the order added. */
    public static final Comparator<FollowUp> REVIEW_DATE_ORDER = Comparator.comparing(FollowUp::getReviewDate);

    private final String description;
    private final LocalDate reviewDate;
    private final FollowUpStatus status;

    /**
     * Creates an outstanding follow-up with the given description and review date.
     */
    public FollowUp(String description, LocalDate reviewDate) {
        this(description, reviewDate, FollowUpStatus.OUTSTANDING);
    }

    /**
     * Creates a follow-up with the given description, review date, and status.
     */
    public FollowUp(String description, LocalDate reviewDate, FollowUpStatus status) {
        requireNonNull(description);
        requireNonNull(reviewDate);
        requireNonNull(status);
        checkArgument(!description.isBlank(), MESSAGE_DESCRIPTION_CONSTRAINTS);

        this.description = description.strip();
        this.reviewDate = reviewDate;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public FollowUpStatus getStatus() {
        return status;
    }

    /**
     * Returns true if this follow-up still needs attention.
     */
    public boolean isOutstanding() {
        return status == FollowUpStatus.OUTSTANDING;
    }

    /**
     * Returns a copy of this follow-up with the given status, preserving its description and review date.
     */
    public FollowUp withStatus(FollowUpStatus status) {
        return new FollowUp(description, reviewDate, status);
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
                && reviewDate.equals(otherFollowUp.reviewDate)
                && status == otherFollowUp.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, reviewDate, status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("description", description)
                .add("reviewDate", reviewDate)
                .add("status", status)
                .toString();
    }
}
