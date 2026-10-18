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
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Deletes an interaction identified by its displayed chronological index.
 */
public class DeleteInteractionCommand extends Command {

    public static final String COMMAND_WORD = "interaction delete";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " n/NAME i/INDEX\n"
            + "Example: " + COMMAND_WORD + " n/John Tan i/1";
    public static final String MESSAGE_SUCCESS = "\u2714 Interaction deleted for %1$s: %2$s\n"
            + "Total interactions for %1$s: %3$d";

    private static final Logger logger = LogsCenter.getLogger(DeleteInteractionCommand.class);

    private final Name name;
    private final Index index;

    /**
     * Creates a command that deletes an interaction at the given displayed index.
     *
     * @param name The student's name.
     * @param index The index shown by {@code interaction list}.
     */
    public DeleteInteractionCommand(Name name, Index index) {
        this.name = requireNonNull(name);
        this.index = requireNonNull(index);
    }

    /**
     * Deletes the selected interaction and returns a confirmation.
     *
     * @throws CommandException if the student is unknown or the index exceeds the interaction list size.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, name)));
        List<Interaction> displayedInteractions = student.getInteractionsInChronologicalOrder();
        if (index.getZeroBased() >= displayedInteractions.size()) {
            throw new CommandException(Messages.MESSAGE_INDEX_OUT_OF_RANGE);
        }

        Interaction deletedInteraction = displayedInteractions.get(index.getZeroBased());
        Student updatedStudent = student.withoutInteractionAtChronologicalIndex(index.getZeroBased());
        assert updatedStudent.getInteractions().size() == student.getInteractions().size() - 1
                : "Exactly one interaction should be removed";
        assert updatedStudent.getSubjects().equals(student.getSubjects())
                : "Deleting an interaction should preserve subjects";
        assert updatedStudent.getFollowUps().equals(student.getFollowUps())
                : "Deleting an interaction should preserve follow-ups";

        model.setStudent(student, updatedStudent);
        assert model.findStudentByName(name).filter(updatedStudent::equals).isPresent()
                : "Updated student should be stored";
        logger.info("Deleted interaction " + index.getOneBased() + " for " + student.getName());

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(),
                Messages.formatInteraction(deletedInteraction), updatedStudent.getInteractions().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteInteractionCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name) && index.equals(otherCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("index", index)
                .toString();
    }
}
