package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Adds a follow-up for an existing student.
 */
public class AddFollowUpCommand extends Command {

    public static final String COMMAND_WORD = "followup add";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD
            + " n/NAME desc/TEXT review/DATE\n"
            + "Example: " + COMMAND_WORD + " n/John Tan desc/Review algebra review/2026-10-15";
    public static final String MESSAGE_SUCCESS = "\u2714 Follow-up added for %1$s — review on %2$s: \"%3$s\"\n"
            + "Total outstanding follow-ups for %1$s: %4$d";
    public static final String MESSAGE_DUPLICATE_FOLLOW_UP =
            "A follow-up with the same description and review date already exists for this student.";

    private final Name name;
    private final FollowUp followUp;

    /**
     * Creates a command that adds {@code followUp} for the student with {@code name}.
     */
    public AddFollowUpCommand(Name name, FollowUp followUp) {
        this.name = requireNonNull(name);
        this.followUp = requireNonNull(followUp);
    }

    /**
     * Adds the follow-up to the matching student and returns a confirmation.
     *
     * @throws CommandException if the student is not found or already has the same follow-up.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, name)));
        if (student.getFollowUps().contains(followUp)) {
            throw new CommandException(MESSAGE_DUPLICATE_FOLLOW_UP);
        }

        Student updatedStudent = student.withFollowUp(followUp);
        model.setStudent(student, updatedStudent);

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), followUp.getReviewDate(),
                followUp.getDescription(), updatedStudent.getFollowUps().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddFollowUpCommand otherCommand)) {
            return false;
        }

        return name.equals(otherCommand.name) && followUp.equals(otherCommand.followUp);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("followUp", followUp).toString();
    }
}
