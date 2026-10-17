package seedu.tutorlink.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_NEW_NAME;
import static seedu.tutorlink.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.logic.Messages;
import seedu.tutorlink.logic.commands.exceptions.CommandException;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

/**
 * Renames a student, replaces their subjects, or both, keeping their interactions and follow-ups.
 */
public class EditStudentCommand extends Command {

    public static final String COMMAND_WORD = "student edit";

    public static final String MESSAGE_USAGE = "Command format: " + COMMAND_WORD + " "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_NEW_NAME + "NEW_NAME] "
            + "[" + PREFIX_SUBJECT + "SUBJECT]...\n"
            + "Give at least one of " + PREFIX_NEW_NAME + " or " + PREFIX_SUBJECT
            + ". Any " + PREFIX_SUBJECT + " replaces all existing subjects.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Tan "
            + PREFIX_NEW_NAME + "Jonathan Tan "
            + PREFIX_SUBJECT + "Math";

    public static final String MESSAGE_SUCCESS = "✔ Student updated: %1$s (subjects: %2$s)";
    public static final String MESSAGE_NOTHING_TO_EDIT = "Nothing to change. Give " + PREFIX_NEW_NAME
            + "NEW_NAME, " + PREFIX_SUBJECT + "SUBJECT, or both.\n" + MESSAGE_USAGE;

    private static final Logger logger = LogsCenter.getLogger(EditStudentCommand.class);

    private final Name name;
    private final Optional<Name> newName;
    private final Optional<List<Subject>> newSubjects;

    /**
     * Creates an EditStudentCommand that edits the student with the given {@code name}.
     *
     * @param name The current name of the student to edit.
     * @param newName The new name, or empty to keep the current name.
     * @param newSubjects The subjects that replace the current ones, or empty to keep them.
     */
    public EditStudentCommand(Name name, Optional<Name> newName, Optional<List<Subject>> newSubjects) {
        requireNonNull(name);
        requireNonNull(newName);
        requireNonNull(newSubjects);
        assert newName.isPresent() || newSubjects.isPresent() : "Parser must reject an edit that changes nothing";
        this.name = name;
        this.newName = newName;
        this.newSubjects = newSubjects.map(List::copyOf);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student student = model.findStudentByName(name)
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, name)));
        Student editedStudent = createEditedStudent(student);

        if (!student.isSameStudent(editedStudent) && model.hasStudent(editedStudent)) {
            Name existingName = model.findStudentByName(editedStudent.getName())
                    .map(Student::getName)
                    .orElse(editedStudent.getName());
            throw new CommandException(String.format(AddCommand.MESSAGE_DUPLICATE_STUDENT, existingName));
        }

        model.setStudent(student, editedStudent);
        assert model.hasStudent(editedStudent) : "Edited student should be stored";
        logger.info("Edited student " + student.getName() + " -> " + editedStudent.getName());

        return new CommandResult(String.format(MESSAGE_SUCCESS, editedStudent.getName(),
                Messages.formatSubjects(editedStudent.getSubjects())));
    }

    /**
     * Returns a copy of {@code student} with the requested changes, keeping its interactions and follow-ups.
     */
    private Student createEditedStudent(Student student) {
        Name updatedName = newName.orElse(student.getName());
        List<Subject> updatedSubjects = newSubjects.orElse(student.getSubjects());
        return new Student(updatedName, updatedSubjects, student.getInteractions(), student.getFollowUps());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditStudentCommand otherCommand)) {
            return false;
        }

        return name.equals(otherCommand.name)
                && newName.equals(otherCommand.newName)
                && newSubjects.equals(otherCommand.newSubjects);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("newName", newName)
                .add("newSubjects", newSubjects)
                .toString();
    }
}
