package seedu.tutorlink.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.commons.util.StringUtil;
import seedu.tutorlink.logic.parser.exceptions.ParseException;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Subject;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_SUBJECT = "Subject '%1$s' is not valid. %2$s";
    public static final String MESSAGE_DUPLICATE_SUBJECT = "Subject '%1$s' is specified twice for the same student.";
    public static final String MESSAGE_INVALID_REVIEW_DATE = "Review date must be a valid date in yyyy-MM-dd format.";

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
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
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
        String trimmedReviewDate = reviewDate.trim();
        if (!trimmedReviewDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ParseException(MESSAGE_INVALID_REVIEW_DATE);
        }

        try {
            return LocalDate.parse(trimmedReviewDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_REVIEW_DATE);
        }
    }
}
