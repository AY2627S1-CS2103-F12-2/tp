package seedu.tutorlink.testutil;

import java.util.ArrayList;
import java.util.List;

import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;
import seedu.tutorlink.model.util.SampleDataUtil;

/**
 * A utility class to help with building Student objects.
 */
public class StudentBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";

    private Name name;
    private List<Subject> subjects;
    private List<Interaction> interactions;

    /**
     * Creates a {@code StudentBuilder} with the default details.
     */
    public StudentBuilder() {
        name = new Name(DEFAULT_NAME);
        subjects = new ArrayList<>();
        interactions = new ArrayList<>();
    }

    /**
     * Initializes the StudentBuilder with the data of {@code studentToCopy}.
     */
    public StudentBuilder(Student studentToCopy) {
        name = studentToCopy.getName();
        subjects = new ArrayList<>(studentToCopy.getSubjects());
        interactions = new ArrayList<>(studentToCopy.getInteractions());
    }

    /**
     * Sets the {@code Name} of the {@code Student} that we are building.
     */
    public StudentBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code subjects} into a {@code List<Subject>} and sets it to the {@code Student} that we are building.
     */
    public StudentBuilder withSubjects(String... subjects) {
        this.subjects = SampleDataUtil.getSubjectList(subjects);
        return this;
    }

    public Student build() {
        return new Student(name, subjects, interactions);
    }

}
