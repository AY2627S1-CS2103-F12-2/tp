package seedu.tutorlink.model.interaction;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import seedu.tutorlink.commons.util.ToStringBuilder;

/**
 * Represents a dated interaction between a tutor and a student.
 * Guarantees: date and note are present, note is not blank, and the interaction is immutable.
 */
public class Interaction {

    public static final String MESSAGE_NOTE_CONSTRAINTS = "Interaction notes should not be blank.";

    /** Orders interactions oldest first; an interaction without a time comes before timed ones on the same date. */
    public static final Comparator<Interaction> CHRONOLOGICAL_ORDER = Comparator
            .comparing(Interaction::getDate)
            .thenComparing(interaction -> interaction.getTime().orElse(LocalTime.MIN));

    private final LocalDate date;
    private final Optional<LocalTime> time;
    private final String note;

    /**
     * Creates an interaction with the given date, optional time, and note.
     *
     * @param date The date on which the interaction occurred.
     * @param time The optional time at which the interaction occurred.
     * @param note A concise note describing the interaction.
     */
    public Interaction(LocalDate date, Optional<LocalTime> time, String note) {
        requireNonNull(date);
        requireNonNull(time);
        requireNonNull(note);
        if (note.isBlank()) {
            throw new IllegalArgumentException(MESSAGE_NOTE_CONSTRAINTS);
        }

        this.date = date;
        this.time = time;
        this.note = note.strip();
    }

    /**
     * Returns the date on which the interaction occurred.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the optional time at which the interaction occurred.
     */
    public Optional<LocalTime> getTime() {
        return time;
    }

    /**
     * Returns the concise note describing the interaction.
     */
    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Interaction otherInteraction)) {
            return false;
        }
        return date.equals(otherInteraction.date)
                && time.equals(otherInteraction.time)
                && note.equals(otherInteraction.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("date", date)
                .add("time", time)
                .add("note", note)
                .toString();
    }
}
