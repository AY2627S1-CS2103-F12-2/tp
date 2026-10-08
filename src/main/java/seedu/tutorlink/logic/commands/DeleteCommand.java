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
 * Deletes the student whose name matches the given name, ignoring case, together with their records.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "student delete";

    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " "
            + PREFIX_NAME + "NAME\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "John Tan";

    public static final String MESSAGE_SUCCESS = "✔ Student deleted: %1$s\nTotal students: %2$d";

    private final Name name;

    /**
     * Creates a DeleteCommand to delete the student with the given {@code name}.
     */
    public DeleteCommand(Name name) {
        requireNonNull(name);
        this.name = name;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, name)));
        model.deleteStudent(student);

        int totalStudents = model.getTutorLink().getStudentList().size();
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), totalStudents));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return name.equals(otherDeleteCommand.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .toString();
    }
}
