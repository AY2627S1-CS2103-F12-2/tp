package seedu.tutorlink.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.logic.parser.Prefix;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_ERROR_PREFIX = "\u274C ";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_UNKNOWN_SUBCOMMAND = "Unknown command. Did you mean one of these? %1$s";
    public static final String MESSAGE_NO_SUBJECTS = "(none recorded)";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "No student named '%1$s' found.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Index %1$d is out of range: %2$s's %3$s are numbered"
            + " 1 to %4$d.";
    public static final String MESSAGE_NO_ITEMS_TO_SELECT = "%1$s has no %2$s.";
    public static final String ITEMS_INTERACTIONS = "interactions";
    public static final String ITEMS_OUTSTANDING_FOLLOW_UPS = "outstanding follow-ups";
    public static final String MESSAGE_FUTURE_INTERACTION_DATE_WARNING =
            "\u26A0 Interaction date %1$s is in the future (today is %2$s); recording it anyway.\n";
    public static final String MESSAGE_STUDENTS_LISTED_OVERVIEW =
            "\u2714 %1$d student(s) listed. Type list to show all students again.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns an error message for an index that is beyond the end of a student's list of items.
     *
     * @param index The index the user gave.
     * @param studentName The name of the student whose list was used.
     * @param listSize The number of items in the list.
     * @param itemsDescription The kind of items in the list, such as {@link #ITEMS_INTERACTIONS}.
     */
    public static String getIndexOutOfRangeMessage(Index index, Name studentName, int listSize,
            String itemsDescription) {
        assert index.getZeroBased() >= listSize : "Index should be out of range";
        if (listSize == 0) {
            return String.format(MESSAGE_NO_ITEMS_TO_SELECT, studentName, itemsDescription);
        }
        return String.format(MESSAGE_INDEX_OUT_OF_RANGE, index.getOneBased(), studentName, itemsDescription,
                listSize);
    }

    /**
     * Formats the {@code student} for display to the user.
     */
    public static String format(Student student) {
        final StringBuilder builder = new StringBuilder();
        builder.append(student.getName())
                .append("; Subjects: ")
                .append(formatSubjects(student.getSubjects()));
        return builder.toString();
    }

    /**
     * Returns the subjects as a comma-separated list, or {@code (none recorded)} if there are none.
     */
    public static String formatSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) {
            return MESSAGE_NO_SUBJECTS;
        }
        return subjects.stream().map(Subject::toString).collect(Collectors.joining(", "));
    }

    /**
     * Returns the interaction's date, optional time, and note for display.
     */
    public static String formatInteraction(Interaction interaction) {
        String formattedTime = interaction.getTime()
                .map(time -> " " + time.format(Interaction.TIME_FORMATTER))
                .orElse("");
        return interaction.getDate() + formattedTime + " \u2014 " + interaction.getNote();
    }

}
