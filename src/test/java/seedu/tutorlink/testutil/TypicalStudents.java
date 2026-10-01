package seedu.tutorlink.testutil;

import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_SUBJECT_MATH;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_SUBJECT_PHYSICS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.student.Student;

/**
 * A utility class containing a list of {@code Student} objects to be used in tests.
 */
public class TypicalStudents {

    public static final Student ALICE = new StudentBuilder().withName("Alice Pauline")
            .withSubjects("Math").build();
    public static final Student BENSON = new StudentBuilder().withName("Benson Meier")
            .withSubjects("Physics", "Math").build();
    public static final Student CARL = new StudentBuilder().withName("Carl Kurz").build();
    public static final Student DANIEL = new StudentBuilder().withName("Daniel Meier").withSubjects("Math").build();
    public static final Student ELLE = new StudentBuilder().withName("Elle Meyer").withSubjects("English").build();
    public static final Student FIONA = new StudentBuilder().withName("Fiona Kunz").withSubjects("Chemistry").build();
    public static final Student GEORGE = new StudentBuilder().withName("George Best").build();

    // Manually added
    public static final Student HOON = new StudentBuilder().withName("Hoon Meier").withSubjects("Biology").build();
    public static final Student IDA = new StudentBuilder().withName("Ida Mueller").build();

    // Manually added - Student's details found in {@code CommandTestUtil}
    public static final Student AMY = new StudentBuilder().withName(VALID_NAME_AMY)
            .withSubjects(VALID_SUBJECT_MATH).build();
    public static final Student BOB = new StudentBuilder().withName(VALID_NAME_BOB)
            .withSubjects(VALID_SUBJECT_PHYSICS, VALID_SUBJECT_MATH).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalStudents() {} // prevents instantiation

    /**
     * Returns an {@code TutorLink} with all the typical students.
     */
    public static TutorLink getTypicalTutorLink() {
        TutorLink tutorLink = new TutorLink();
        for (Student student : getTypicalStudents()) {
            tutorLink.addStudent(student);
        }
        return tutorLink;
    }

    public static List<Student> getTypicalStudents() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
