package seedu.tutorlink.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.commons.util.StringUtil;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Subject;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_SUBJECT = "Subject '%1$s' is not valid. %2$s";
    public static final String MESSAGE_DUPLICATE_SUBJECT = "Subject '%1$s' is specified twice for the same student.";
    public static final String MESSAGE_INVALID_DATE_FORMAT =
            "Date must be in format yyyy-MM-dd (e.g. 2026-09-14).";
    public static final String MESSAGE_INVALID_CALENDAR_DATE = "'%1$s' is not a valid calendar date.";
    public static final String MESSAGE_INVALID_INTERACTION_TIME =
            "Interaction time must be a valid time in HH:mm format.";
    public static final int MAX_INTERACTION_NOTE_LENGTH = 500;
    public static final String MESSAGE_INVALID_INTERACTION_NOTE = "Note cannot be empty.";
    public static final String MESSAGE_INVALID_INTERACTION_NOTE_LENGTH =
            "Note must be 500 characters or fewer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed, and repeated spaces between words are reduced to one.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim().replaceAll("\\s+", " ");
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String subject} into a {@code Subject}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code subject} is invalid.
     */
    public static Subject parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        String trimmedSubject = subject.trim();
        if (!Subject.isValidSubject(trimmedSubject)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_SUBJECT, trimmedSubject, Subject.MESSAGE_CONSTRAINTS));
        }
        return new Subject(trimmedSubject);
    }

    /**
     * Parses {@code Collection<String> subjects} into a {@code List<Subject>}, keeping the order they were given in.
     *
     * @throws ParseException if any subject is invalid, or the same subject is given twice (ignoring case).
     */
    public static List<Subject> parseSubjects(Collection<String> subjects) throws ParseException {
        requireNonNull(subjects);
        final List<Subject> subjectList = new ArrayList<>();
        for (String subjectName : subjects) {
            Subject subject = parseSubject(subjectName);
            if (subjectList.stream().anyMatch(subject::isSameSubject)) {
                throw new ParseException(String.format(MESSAGE_DUPLICATE_SUBJECT, subject));
            }
            subjectList.add(subject);
        }
        return subjectList;
    }

    /**
     * Parses {@code reviewDate} into a {@code LocalDate} in yyyy-MM-dd format.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the review date is malformed or does not exist.
     */
    public static LocalDate parseReviewDate(String reviewDate) throws ParseException {
        requireNonNull(reviewDate);
        return parseDate(reviewDate);
    }

    /**
     * Parses {@code interactionDate} into a {@code LocalDate} in yyyy-MM-dd format.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the interaction date is malformed or does not exist.
     */
    public static LocalDate parseInteractionDate(String interactionDate) throws ParseException {
        requireNonNull(interactionDate);
        return parseDate(interactionDate);
    }

    private static LocalDate parseDate(String date) throws ParseException {
        String trimmedDate = date.trim();
        if (!trimmedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ParseException(MESSAGE_INVALID_DATE_FORMAT);
        }
        try {
            return LocalDate.parse(trimmedDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_CALENDAR_DATE, trimmedDate));
        }
    }

    /**
     * Parses {@code interactionTime} into a {@code LocalTime} in HH:mm format.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the interaction time is malformed or does not exist.
     */
    public static LocalTime parseInteractionTime(String interactionTime) throws ParseException {
        requireNonNull(interactionTime);
        String trimmedInteractionTime = interactionTime.trim();
        if (!trimmedInteractionTime.matches("\\d{2}:\\d{2}")) {
            throw new ParseException(MESSAGE_INVALID_INTERACTION_TIME);
        }

        try {
            return LocalTime.parse(trimmedInteractionTime, Interaction.TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_INTERACTION_TIME);
        }
    }

    /**
     * Parses an interaction note, trimming leading and trailing whitespaces.
     *
     * @throws ParseException if the interaction note is blank, too long, or contains a newline.
     */
    public static String parseInteractionNote(String interactionNote) throws ParseException {
        requireNonNull(interactionNote);
        String trimmedInteractionNote = interactionNote.trim();
        if (trimmedInteractionNote.isBlank()) {
            throw new ParseException(MESSAGE_INVALID_INTERACTION_NOTE);
        }
        if (trimmedInteractionNote.length() > MAX_INTERACTION_NOTE_LENGTH
                || trimmedInteractionNote.contains("\n")
                || trimmedInteractionNote.contains("\r")) {
            throw new ParseException(MESSAGE_INVALID_INTERACTION_NOTE_LENGTH);
        }
        return trimmedInteractionNote;
    }
}
