package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;

/**
 * Finds and lists all students in TutorLink whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " KEYWORD [MORE_KEYWORDS]...\n"
            + "Shows students whose names contain any of the keywords as a whole word, ignoring case.\n"
            + "Example: " + COMMAND_WORD + " tan lim";

    private final NameContainsKeywordsPredicate predicate;

    public FindCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStudentList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW, model.getFilteredStudentList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
