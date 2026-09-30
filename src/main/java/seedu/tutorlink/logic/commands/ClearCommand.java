package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.TutorLink;

/**
 * Clears TutorLink.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "TutorLink has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setTutorLink(new TutorLink());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
