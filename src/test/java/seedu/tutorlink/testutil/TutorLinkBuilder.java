package seedu.tutorlink.testutil;

import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.student.Student;

/**
 * A utility class to help with building TutorLink objects.
 * Example usage: <br>
 *     {@code TutorLink ab = new TutorLinkBuilder().withStudent("John", "Doe").build();}
 */
public class TutorLinkBuilder {

    private TutorLink tutorLink;

    public TutorLinkBuilder() {
        tutorLink = new TutorLink();
    }

    public TutorLinkBuilder(TutorLink tutorLink) {
        this.tutorLink = tutorLink;
    }

    /**
     * Adds a new {@code Student} to the {@code TutorLink} that we are building.
     */
    public TutorLinkBuilder withStudent(Student student) {
        tutorLink.addStudent(student);
        return this;
    }

    public TutorLink build() {
        return tutorLink;
    }
}
