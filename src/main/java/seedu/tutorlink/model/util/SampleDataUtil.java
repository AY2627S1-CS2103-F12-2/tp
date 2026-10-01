package seedu.tutorlink.model.util;

import java.util.Arrays;
import java.util.List;

import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

/**
 * Contains utility methods for populating {@code TutorLink} with sample data.
 */
public class SampleDataUtil {
    public static Student[] getSampleStudents() {
        return new Student[] {
            new Student(new Name("Alex Yeoh"), getSubjectList("Math", "Physics")),
            new Student(new Name("Bernice Yu"), getSubjectList("Chemistry")),
            new Student(new Name("Charlotte Oliveiro"), getSubjectList("English", "Literature")),
            new Student(new Name("David Li"), getSubjectList("H2 Math")),
            new Student(new Name("Irfan Ibrahim"), getSubjectList("Biology + Chemistry")),
            new Student(new Name("Roy Balakrishnan"), getSubjectList())
        };
    }

    public static ReadOnlyTutorLink getSampleTutorLink() {
        TutorLink sampleTutorLink = new TutorLink();
        for (Student sampleStudent : getSampleStudents()) {
            sampleTutorLink.addStudent(sampleStudent);
        }
        return sampleTutorLink;
    }

    /**
     * Returns a subject list containing the given subject names, in the same order.
     */
    public static List<Subject> getSubjectList(String... subjectNames) {
        return Arrays.stream(subjectNames)
                .map(Subject::new)
                .toList();
    }

}
