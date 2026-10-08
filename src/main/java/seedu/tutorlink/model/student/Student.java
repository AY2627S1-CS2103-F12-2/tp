package seedu.tutorlink.model.student;

import static seedu.tutorlink.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.interaction.Interaction;

/**
 * Represents a Student in TutorLink.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Student {

    // Identity fields
    private final Name name;

    // Data fields
    private final List<Subject> subjects;
    private final List<Interaction> interactions;
    private final List<FollowUp> followUps;

    /**
     * Every field must be present and not null.
     * Subjects are kept in the order given.
     */
    public Student(Name name, List<Subject> subjects) {
        this(name, subjects, List.of());
    }

    /**
     * Creates a student with the given name, subjects, and interactions.
     *
     * @param name The student's name.
     * @param subjects The subjects taught to the student.
     * @param interactions The interactions recorded for the student.
     */
    public Student(Name name, List<Subject> subjects, List<Interaction> interactions) {
        this(name, subjects, interactions, List.of());
    }

    /**
     * Creates a student with the given name, subjects, interactions, and follow-ups.
     *
     * @param name The student's name.
     * @param subjects The subjects taught to the student.
     * @param interactions The interactions recorded for the student.
     * @param followUps The follow-ups recorded for the student, in insertion order.
     */
    public Student(Name name, List<Subject> subjects, List<Interaction> interactions, List<FollowUp> followUps) {
        requireAllNonNull(name, subjects, interactions, followUps);
        this.name = name;
        this.subjects = List.copyOf(subjects);
        this.interactions = List.copyOf(interactions);
        this.followUps = List.copyOf(followUps);
    }

    public Name getName() {
        return name;
    }

    /**
     * Returns an immutable list of subjects, in the order they were added, which throws
     * {@code UnsupportedOperationException} if modification is attempted.
     */
    public List<Subject> getSubjects() {
        return subjects;
    }

    /**
     * Returns an immutable list of interactions, in the order they were added.
     */
    public List<Interaction> getInteractions() {
        return interactions;
    }

    /**
     * Returns an immutable list of follow-ups, in the order they were added.
     */
    public List<FollowUp> getFollowUps() {
        return followUps;
    }

    /**
     * Returns a copy of this student with the given follow-up appended.
     *
     * @param followUp The follow-up to append.
     * @return A student containing the appended follow-up.
     */
    public Student withFollowUp(FollowUp followUp) {
        List<FollowUp> updatedFollowUps = new ArrayList<>(followUps);
        updatedFollowUps.add(Objects.requireNonNull(followUp));
        return new Student(name, subjects, interactions, updatedFollowUps);
    }

    /**
     * Returns a copy of this student with the given interaction appended.
     *
     * @param interaction The interaction to append.
     * @return A student containing the appended interaction.
     */
    public Student withInteraction(Interaction interaction) {
        List<Interaction> updatedInteractions = new ArrayList<>(interactions);
        updatedInteractions.add(Objects.requireNonNull(interaction));
        return new Student(name, subjects, updatedInteractions, followUps);
    }

    /**
     * Returns true if both students have the same name.
     * This defines a weaker notion of equality between two students.
     */
    public boolean isSameStudent(Student otherStudent) {
        if (otherStudent == this) {
            return true;
        }

        return otherStudent != null
                && otherStudent.getName().isSameName(getName());
    }

    /**
     * Returns true if both students have the same identity and data fields.
     * This defines a stronger notion of equality between two students.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Student otherStudent)) {
            return false;
        }

        return name.equals(otherStudent.name)
                && subjects.equals(otherStudent.subjects)
                && interactions.equals(otherStudent.interactions)
                && followUps.equals(otherStudent.followUps);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, subjects, interactions, followUps);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("subjects", subjects)
                .add("interactions", interactions)
                .add("followUps", followUps)
                .toString();
    }

}
