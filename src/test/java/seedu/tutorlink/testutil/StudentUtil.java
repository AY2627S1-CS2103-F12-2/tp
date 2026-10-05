package seedu.tutorlink.testutil;

import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_SUBJECT;

import seedu.tutorlink.logic.commands.AddCommand;
import seedu.tutorlink.model.student.Student;

/**
 * A utility class for Student.
 */
public class StudentUtil {

    /**
     * Returns an add command string for adding the {@code student}.
     */
    public static String getAddCommand(Student student) {
        return AddCommand.COMMAND_WORD + " " + getStudentDetails(student);
    }

    /**
     * Returns the part of command string for the given {@code student}'s details.
     */
    public static String getStudentDetails(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + student.getName().fullName + " ");
        student.getSubjects().forEach(s -> sb.append(PREFIX_SUBJECT + s.subjectName + " "));
        return sb.toString();
    }
}
