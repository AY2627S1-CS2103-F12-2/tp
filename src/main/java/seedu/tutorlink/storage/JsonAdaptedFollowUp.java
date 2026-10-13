package seedu.tutorlink.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.followup.FollowUp;

/**
 * Jackson-friendly version of {@link FollowUp}.
 */
class JsonAdaptedFollowUp {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Follow-up's %s field is missing!";
    public static final String INVALID_REVIEW_DATE_MESSAGE = "Review date must be a valid date in yyyy-MM-dd format.";

    private final String description;
    private final String reviewDate;

    /**
     * Constructs a {@code JsonAdaptedFollowUp} with the given follow-up details.
     */
    @JsonCreator
    public JsonAdaptedFollowUp(@JsonProperty("description") String description,
            @JsonProperty("reviewDate") String reviewDate) {
        this.description = description;
        this.reviewDate = reviewDate;
    }

    /**
     * Converts the given {@code FollowUp} into this class for Jackson use.
     */
    public JsonAdaptedFollowUp(FollowUp source) {
        description = source.getDescription();
        reviewDate = source.getReviewDate().toString();
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
        if (!reviewDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalValueException(INVALID_REVIEW_DATE_MESSAGE);
        }

        try {
            return new FollowUp(description, LocalDate.parse(reviewDate));
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(INVALID_REVIEW_DATE_MESSAGE);
        }
    }
}
