package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Records an interaction for an existing student.
 */
public class AddInteractionCommand extends Command {

    public static final String COMMAND_WORD = "interaction add";

    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " "
            + "n/NAME d/DATE [t/TIME] note/TEXT\n"
            + "Example: " + COMMAND_WORD + " "
            + "n/Alex Tan d/2026-10-01 t/14:30 note/Practised algebraic fractions";
    public static final String MESSAGE_SUCCESS = "\u2714 Interaction recorded for %1$s.";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "No student named '%1$s' found.";

    private final Name name;
    private final Interaction interaction;

    /**
     * Creates a command that records the given {@code interaction} for the student with {@code name}.
     */
    public AddInteractionCommand(Name name, Interaction interaction) {
        this.name = requireNonNull(name);
        this.interaction = requireNonNull(interaction);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, name)));
        Student updatedStudent = student.withInteraction(interaction);
        model.setStudent(student, updatedStudent);

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddInteractionCommand otherAddInteractionCommand)) {
            return false;
        }
        return name.equals(otherAddInteractionCommand.name)
                && interaction.equals(otherAddInteractionCommand.interaction);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("interaction", interaction)
                .toString();
    }
}
