package seedu.tutorlink.storage;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.interaction.Interaction;

/**
 * Jackson-friendly version of {@link Interaction}.
 */
class JsonAdaptedInteraction {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Interaction's %s field is missing!";
    public static final String INVALID_DATE_MESSAGE = "Interaction date must be a valid date.";
    public static final String INVALID_TIME_MESSAGE = "Interaction time must be a valid time in HH:mm format.";

    private final String date;
    private final String time;
    private final String note;

    /**
     * Constructs a {@code JsonAdaptedInteraction} with the given interaction details.
     */
    @JsonCreator
    public JsonAdaptedInteraction(@JsonProperty("date") String date,
            @JsonProperty("time") String time,
            @JsonProperty("note") String note) {
        this.date = date;
        this.time = time;
        this.note = note;
    }

    /**
     * Converts a given {@code Interaction} into this class for Jackson use.
     */
    public JsonAdaptedInteraction(Interaction source) {
        date = source.getDate().toString();
        time = source.getTime().map(JsonAdaptedInteraction::formatTime).orElse(null);
        note = source.getNote();
    }

    /**
     * Converts this Jackson-friendly object into a model {@code Interaction}.
     *
     * @throws IllegalValueException If a stored value violates the model constraints.
     */
    public Interaction toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }
        if (note == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "note"));
        }

        LocalDate modelDate;
        try {
            modelDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeException exception) {
            throw new IllegalValueException(INVALID_DATE_MESSAGE);
        }

        Optional<LocalTime> modelTime = Optional.empty();
        if (time != null) {
            try {
                modelTime = Optional.of(LocalTime.parse(time, Interaction.TIME_FORMATTER));
            } catch (DateTimeException exception) {
                throw new IllegalValueException(INVALID_TIME_MESSAGE);
            }
        }

        try {
            return new Interaction(modelDate, modelTime, note);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(Interaction.MESSAGE_NOTE_CONSTRAINTS);
        }
    }

    private static String formatTime(LocalTime time) {
        return time.format(Interaction.TIME_FORMATTER);
    }
}
