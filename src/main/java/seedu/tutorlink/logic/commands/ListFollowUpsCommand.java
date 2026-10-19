package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Shows a student's outstanding follow-ups in review-date order.
 */
public class ListFollowUpsCommand extends Command {

    public static final String COMMAND_WORD = "followup list";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " n/NAME\n"
            + "Example: " + COMMAND_WORD + " n/John Tan";
    public static final String MESSAGE_SUCCESS = "Outstanding follow-ups for %1$s (%2$d):\n%3$s";
    public static final String MESSAGE_NONE_RECORDED = "\u2139 No outstanding follow-ups for %1$s.";

    private final Name name;

    /**
     * Creates a command that lists outstanding follow-ups for the student with {@code name}.
     */
    public ListFollowUpsCommand(Name name) {
        this.name = requireNonNull(name);
    }

    /**
     * Returns the student's follow-ups sorted by review date, preserving insertion order for equal dates.
     *
     * @throws CommandException if no student matches the given name.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, name)));
        List<FollowUp> sortedFollowUps = student.getOutstandingFollowUps();
        if (sortedFollowUps.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NONE_RECORDED, student.getName()));
        }

        String formattedFollowUps = IntStream.range(0, sortedFollowUps.size())
                .mapToObj(index -> (index + 1) + ". " + sortedFollowUps.get(index).getReviewDate()
                        + " \u2014 " + sortedFollowUps.get(index).getDescription())
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), sortedFollowUps.size(),
                formattedFollowUps));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListFollowUpsCommand otherCommand)) {
            return false;
        }

        return name.equals(otherCommand.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).toString();
    }
}
