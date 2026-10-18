package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
 * Edits selected fields of an interaction identified by its chronological index.
 */
public class EditInteractionCommand extends Command {

    public static final String COMMAND_WORD = "interaction edit";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD
            + " n/NAME i/INDEX [d/DATE] [t/TIME] [note/TEXT]\n"
            + "At least one of d/, t/ or note/ must be given, and note/ must be last.\n"
            + "Example: " + COMMAND_WORD + " n/John Tan i/1 note/Reviewed fractions";
    public static final String MESSAGE_SUCCESS = "\u2714 Interaction updated for %1$s: %2$s\n"
            + "Updated to: %3$s";

    private static final Logger logger = LogsCenter.getLogger(EditInteractionCommand.class);

    private final Name name;
    private final Index index;
    private final Optional<LocalDate> date;
    private final Optional<LocalTime> time;
    private final Optional<String> note;

    /**
     * Creates a command that edits the supplied fields of an interaction.
     */
    public EditInteractionCommand(Name name, Index index, Optional<LocalDate> date,
            Optional<LocalTime> time, Optional<String> note) {
        this.name = requireNonNull(name);
        this.index = requireNonNull(index);
        this.date = requireNonNull(date);
        this.time = requireNonNull(time);
        this.note = requireNonNull(note);
        if (date.isEmpty() && time.isEmpty() && note.isEmpty()) {
            throw new IllegalArgumentException("At least one interaction field must be edited");
        }
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, name)));
        List<Interaction> chronologicalInteractions = student.getInteractionsInChronologicalOrder();
        if (index.getZeroBased() >= chronologicalInteractions.size()) {
            throw new CommandException(Messages.MESSAGE_INDEX_OUT_OF_RANGE);
        }

        Interaction original = chronologicalInteractions.get(index.getZeroBased());
        Interaction edited = new Interaction(date.orElse(original.getDate()),
                time.map(Optional::of).orElse(original.getTime()), note.orElse(original.getNote()));
        Student updatedStudent = student.withInteractionAtChronologicalIndex(index.getZeroBased(), edited);
        assert updatedStudent.getInteractions().size() == student.getInteractions().size()
                : "Editing should preserve the interaction count";
        assert updatedStudent.getSubjects().equals(student.getSubjects())
                : "Editing an interaction should preserve subjects";
        assert updatedStudent.getFollowUps().equals(student.getFollowUps())
                : "Editing an interaction should preserve follow-ups";

        model.setStudent(student, updatedStudent);
        assert model.findStudentByName(name).filter(updatedStudent::equals).isPresent()
                : "Updated student should be stored";
        logger.info("Edited interaction " + index.getOneBased() + " for " + student.getName());

        LocalDate today = LocalDate.now();
        String warning = edited.getDate().isAfter(today)
                ? String.format(Messages.MESSAGE_FUTURE_INTERACTION_DATE_WARNING, edited.getDate(), today)
                : "";
        return new CommandResult(warning + String.format(MESSAGE_SUCCESS, student.getName(),
                Messages.formatInteraction(original), Messages.formatInteraction(edited)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditInteractionCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name) && index.equals(otherCommand.index)
                && date.equals(otherCommand.date) && time.equals(otherCommand.time) && note.equals(otherCommand.note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("index", index)
                .add("date", date).add("time", time).add("note", note).toString();
    }
}
