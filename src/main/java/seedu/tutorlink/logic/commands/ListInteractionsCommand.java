package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Shows a student's interaction history in chronological order.
 */
public class ListInteractionsCommand extends Command {

    public static final String COMMAND_WORD = "interaction list";
    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " n/NAME\n"
            + "Example: " + COMMAND_WORD + " n/Alex Tan";
    public static final String MESSAGE_SUCCESS = "Interactions for %1$s (%2$d):\n%3$s";
    public static final String MESSAGE_NONE_RECORDED = "\u2139 No interactions recorded for %1$s yet.";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "No student named '%1$s' found.";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final Name name;

    /**
     * Creates a command that lists interactions for the student with {@code name}.
     */
    public ListInteractionsCommand(Name name) {
        this.name = requireNonNull(name);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, name)));
        if (student.getInteractions().isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NONE_RECORDED, student.getName()));
        }

        List<Interaction> sortedInteractions = student.getInteractions().stream()
                .sorted(Interaction.CHRONOLOGICAL_ORDER)
                .toList();
        String formattedInteractions = IntStream.range(0, sortedInteractions.size())
                .mapToObj(index -> (index + 1) + ". " + formatInteraction(sortedInteractions.get(index)))
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), sortedInteractions.size(),
                formattedInteractions));
    }

    private static String formatInteraction(Interaction interaction) {
        String formattedTime = interaction.getTime()
                .map(time -> " " + time.format(TIME_FORMATTER))
                .orElse("");
        return interaction.getDate() + formattedTime + " \u2014 " + interaction.getNote();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListInteractionsCommand otherListInteractionsCommand)) {
            return false;
        }
        return name.equals(otherListInteractionsCommand.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .toString();
    }
}
