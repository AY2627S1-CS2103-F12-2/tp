package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Shows the details of the student whose name matches the given name, ignoring case.
 */
public class ViewStudentCommand extends Command {

    public static final String COMMAND_WORD = "student view";

    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " "
            + PREFIX_NAME + "NAME\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "John Tan";

    public static final String MESSAGE_SUCCESS = "Student: %1$s\nSubjects: %2$s";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "No student named '%1$s' found.";

    private final Name name;

    /**
     * Creates a ViewStudentCommand to show the student with the given {@code name}.
     */
    public ViewStudentCommand(Name name) {
        requireNonNull(name);
        this.name = name;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, name)));
        model.setSelectedStudent(student);

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(),
                Messages.formatSubjects(student.getSubjects())));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewStudentCommand otherViewStudentCommand)) {
            return false;
        }

        return name.equals(otherViewStudentCommand.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .toString();
    }
}
