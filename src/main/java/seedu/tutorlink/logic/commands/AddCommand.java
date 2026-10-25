package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.List;
import java.util.logging.Logger;

import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

/**
 * Adds a student to TutorLink.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "student add";

    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_SUBJECT + "SUBJECT]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Tan "
            + PREFIX_SUBJECT + "Math "
            + PREFIX_SUBJECT + "Physics";

    public static final String MESSAGE_SUCCESS = "\u2714 Student added: %1$s (%2$s)\nTotal students: %3$d";
    public static final String MESSAGE_SUBJECTS = "subjects: %1$s";
    public static final String MESSAGE_NO_SUBJECTS = "no subjects recorded";
    public static final String MESSAGE_DUPLICATE_STUDENT =
            "A student named '%1$s' already exists. Student names must be unique.";

    private static final Logger logger = LogsCenter.getLogger(AddCommand.class);

    private final Student toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Student}
     */
    public AddCommand(Student student) {
        requireNonNull(student);
        toAdd = student;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasStudent(toAdd)) {
            Name existingName = model.findStudentByName(toAdd.getName()).map(Student::getName).orElse(toAdd.getName());
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, existingName));
        }

        model.addStudent(toAdd);
        model.setSelectedStudent(toAdd);
        assert model.findStudentByName(toAdd.getName()).filter(toAdd::equals).isPresent()
                : "Added student should be stored";
        logger.info("Added student " + toAdd.getName());
        int totalStudents = model.getTutorLink().getStudentList().size();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName(), describeSubjects(toAdd.getSubjects()),
                totalStudents));
    }

    /**
     * Returns the subjects as {@code subjects: Math, Physics}, or {@code no subjects recorded} if there are none.
     */
    static String describeSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) {
            return MESSAGE_NO_SUBJECTS;
        }
        return String.format(MESSAGE_SUBJECTS, Messages.formatSubjects(subjects));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
