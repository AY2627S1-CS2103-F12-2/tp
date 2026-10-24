package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Marks an outstanding follow-up as completed while retaining its history.
 */
public class DoneFollowUpCommand extends Command {

    public static final String COMMAND_WORD = "followup done";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " n/NAME i/INDEX\n"
            + "Example: " + COMMAND_WORD + " n/John Tan i/1";
    public static final String MESSAGE_SUCCESS = "\u2714 Follow-up completed for %1$s — review on %2$s: \"%3$s\"\n"
            + "Total outstanding follow-ups for %1$s: %4$d";

    private static final Logger logger = LogsCenter.getLogger(DoneFollowUpCommand.class);
    private final Name name;
    private final Index index;

    /**
     * Creates a command that completes the student's outstanding follow-up at the given displayed index.
     *
     * @param name The student's name.
     * @param index The index shown by {@code followup list}.
     */
    public DoneFollowUpCommand(Name name, Index index) {
        this.name = requireNonNull(name);
        this.index = requireNonNull(index);
    }

    /**
     * Completes the selected outstanding follow-up and returns a confirmation.
     *
     * @throws CommandException if the student is unknown or the index exceeds the outstanding list size.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, name)));
        List<FollowUp> outstanding = student.getOutstandingFollowUps();
        if (index.getZeroBased() >= outstanding.size()) {
            throw new CommandException(Messages.getIndexOutOfRangeMessage(index, student.getName(),
                    outstanding.size(), Messages.ITEMS_OUTSTANDING_FOLLOW_UPS));
        }

        FollowUp completed = outstanding.get(index.getZeroBased());
        Student updated = student.withOutstandingFollowUpStatus(index.getZeroBased(), FollowUpStatus.DONE);
        assert updated.getFollowUps().size() == student.getFollowUps().size();
        assert updated.getOutstandingFollowUps().size() == outstanding.size() - 1;
        model.setStudent(student, updated);
        assert model.findStudentByName(name).orElseThrow().equals(updated);
        logger.info("Completed a follow-up; outstanding count is " + updated.getOutstandingFollowUps().size());

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), completed.getReviewDate(),
                completed.getDescription(), updated.getOutstandingFollowUps().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DoneFollowUpCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name) && index.equals(otherCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("index", index).toString();
    }
}
