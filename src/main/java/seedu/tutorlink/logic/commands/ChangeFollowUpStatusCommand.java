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
 * Changes an outstanding follow-up to a completed or cancelled status while retaining its history.
 */
public abstract class ChangeFollowUpStatusCommand extends Command {

    private static final Logger logger = LogsCenter.getLogger(ChangeFollowUpStatusCommand.class);

    private final Name name;
    private final Index index;
    private final FollowUpStatus targetStatus;
    private final String successMessage;

    /**
     * Creates a command that changes the student's outstanding follow-up at the given displayed index.
     *
     * @param name The student's name.
     * @param index The index shown by {@code followup list}.
     * @param targetStatus The terminal status, {@code DONE} or {@code CANCELLED}.
     * @param successMessage The confirmation format for the student, date, description and outstanding count.
     */
    protected ChangeFollowUpStatusCommand(Name name, Index index, FollowUpStatus targetStatus, String successMessage) {
        this.name = requireNonNull(name);
        this.index = requireNonNull(index);
        this.targetStatus = requireNonNull(targetStatus);
        this.successMessage = requireNonNull(successMessage);
    }

    /**
     * Changes the selected outstanding follow-up's status and returns a confirmation.
     *
     * @param model The model containing the student's follow-ups. Cannot be null.
     * @return confirmation of the status change and remaining outstanding count.
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

        FollowUp followUp = outstanding.get(index.getZeroBased());
        Student updated = student.withOutstandingFollowUpStatus(index.getZeroBased(), targetStatus);
        assert updated.getFollowUps().size() == student.getFollowUps().size();
        assert updated.getOutstandingFollowUps().size() == outstanding.size() - 1;
        model.setStudent(student, updated);
        assert model.findStudentByName(name).orElseThrow().equals(updated);
        String action = targetStatus == FollowUpStatus.DONE ? "Completed" : "Cancelled";
        logger.info(action + " a follow-up; outstanding count is " + updated.getOutstandingFollowUps().size());

        return new CommandResult(String.format(successMessage, student.getName(), followUp.getReviewDate(),
                followUp.getDescription(), updated.getOutstandingFollowUps().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ChangeFollowUpStatusCommand otherCommand)) {
            return false;
        }
        return targetStatus == otherCommand.targetStatus
                && name.equals(otherCommand.name) && index.equals(otherCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("index", index).toString();
    }
}
