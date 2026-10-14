package seedu.tutorlink.model.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

/**
 * Contains utility methods for populating {@code TutorLink} with sample data.
 */
public class SampleDataUtil {
    public static Student[] getSampleStudents() {
        return new Student[] {
            new Student(new Name("Alex Yeoh"), getSubjectList("Math", "Physics"),
                    List.of(interaction("2026-10-01", "14:30", "Practised algebraic fractions; weak at factorising"),
                            interaction("2026-10-08", "14:30", "Factorising improved; started kinematics graphs")),
                    List.of(new FollowUp("Check factorising homework", LocalDate.parse("2026-10-15")),
                            new FollowUp("Revisit velocity-time graphs", LocalDate.parse("2026-10-22")))),
            new Student(new Name("Bernice Yu"), getSubjectList("Chemistry"),
                    List.of(interaction("2026-10-06", null, "Mole concept quiz 6/10; unsure of limiting reagents")),
                    List.of(new FollowUp("Go through limiting reagent questions", LocalDate.parse("2026-10-13")))),
            new Student(new Name("Charlotte Oliveiro"), getSubjectList("English", "Literature"),
                    List.of(interaction("2026-10-09", "10:00", "Planned essay structure for unseen poetry")),
                    List.of()),
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

    /**
     * Returns an interaction on {@code date} with the optional {@code time} (null for none).
     */
    private static Interaction interaction(String date, String time, String note) {
        return new Interaction(LocalDate.parse(date), Optional.ofNullable(time).map(LocalTime::parse), note);
    }

}
