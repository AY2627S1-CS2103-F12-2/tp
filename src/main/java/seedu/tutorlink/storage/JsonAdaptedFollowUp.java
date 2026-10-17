package seedu.tutorlink.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;

/**
 * Jackson-friendly version of {@link FollowUp}.
 */
class JsonAdaptedFollowUp {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Follow-up's %s field is missing!";
    public static final String INVALID_REVIEW_DATE_MESSAGE = "Review date must be a valid date in yyyy-MM-dd format.";
    public static final String INVALID_STATUS_MESSAGE = "Follow-up status must be OUTSTANDING, DONE or CANCELLED.";

    private final String description;
    private final String reviewDate;
    private final String status;

    /**
     * Constructs a {@code JsonAdaptedFollowUp} with the given follow-up details.
     */
    @JsonCreator
    public JsonAdaptedFollowUp(@JsonProperty("description") String description,
            @JsonProperty("reviewDate") String reviewDate,
            @JsonProperty("status") String status) {
        this.description = description;
        this.reviewDate = reviewDate;
        this.status = status;
    }

    /**
     * Constructs an adapter without a status for compatibility with v1.3 data and existing callers.
     */
    public JsonAdaptedFollowUp(String description, String reviewDate) {
        this(description, reviewDate, null);
    }

    /**
     * Converts the given {@code FollowUp} into this class for Jackson use.
     */
    public JsonAdaptedFollowUp(FollowUp source) {
        description = source.getDescription();
        reviewDate = source.getReviewDate().toString();
        status = source.getStatus().name();
    }

    /**
     * Converts this Jackson-friendly object into a model {@code FollowUp}.
     *
     * @throws IllegalValueException if a stored value is missing or violates the model constraints.
     */
    public FollowUp toModelType() throws IllegalValueException {
        if (description == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "description"));
        }
        if (reviewDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "reviewDate"));
        }
        if (description.isBlank()) {
            throw new IllegalValueException(FollowUp.MESSAGE_DESCRIPTION_CONSTRAINTS);
        }
        String strippedDescription = description.strip();
        if (strippedDescription.codePointCount(0, strippedDescription.length()) > FollowUp.MAX_DESCRIPTION_LENGTH) {
            throw new IllegalValueException(FollowUp.MESSAGE_LONG_DESCRIPTION);
        }
        if (description.contains("\n") || description.contains("\r")) {
            throw new IllegalValueException(FollowUp.MESSAGE_MULTILINE_DESCRIPTION);
        }
        if (!reviewDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalValueException(INVALID_REVIEW_DATE_MESSAGE);
        }

        FollowUpStatus modelStatus = FollowUpStatus.OUTSTANDING;
        if (status != null) {
            try {
                modelStatus = FollowUpStatus.valueOf(status);
            } catch (IllegalArgumentException exception) {
                throw new IllegalValueException(INVALID_STATUS_MESSAGE);
            }
        }

        try {
            return new FollowUp(description, LocalDate.parse(reviewDate), modelStatus);
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(INVALID_REVIEW_DATE_MESSAGE);
        }
    }
}
