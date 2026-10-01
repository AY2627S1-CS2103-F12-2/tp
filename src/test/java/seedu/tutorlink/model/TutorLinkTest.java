package seedu.tutorlink.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_SUBJECT_PHYSICS;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.ALICE;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.exceptions.DuplicateStudentException;
import seedu.tutorlink.testutil.StudentBuilder;

public class TutorLinkTest {

    private final TutorLink tutorLink = new TutorLink();

    @Test
    public void constructor() {
        assertEquals(List.of(), tutorLink.getStudentList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> tutorLink.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyTutorLink_replacesData() {
        TutorLink newData = getTypicalTutorLink();
        tutorLink.resetData(newData);
        assertEquals(newData, tutorLink);
    }

    @Test
    public void resetData_withDuplicateStudents_throwsDuplicateStudentException() {
        // Two students with the same identity fields
        Student editedAlice = new StudentBuilder(ALICE).withSubjects(VALID_SUBJECT_PHYSICS).build();
        List<Student> newStudents = List.of(ALICE, editedAlice);
        TutorLinkStub newData = new TutorLinkStub(newStudents);

        assertThrows(DuplicateStudentException.class, () -> tutorLink.resetData(newData));
    }

    @Test
    public void hasStudent_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> tutorLink.hasStudent(null));
    }

    @Test
    public void hasStudent_studentNotInTutorLink_returnsFalse() {
        assertFalse(tutorLink.hasStudent(ALICE));
    }

    @Test
    public void hasStudent_studentInTutorLink_returnsTrue() {
        tutorLink.addStudent(ALICE);
        assertTrue(tutorLink.hasStudent(ALICE));
    }

    @Test
    public void hasStudent_studentWithSameIdentityFieldsInTutorLink_returnsTrue() {
        tutorLink.addStudent(ALICE);
        Student editedAlice = new StudentBuilder(ALICE).withSubjects(VALID_SUBJECT_PHYSICS).build();
        assertTrue(tutorLink.hasStudent(editedAlice));
    }

    @Test
    public void getStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> tutorLink.getStudentList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = TutorLink.class.getCanonicalName() + "{students=" + tutorLink.getStudentList() + "}";
        assertEquals(expected, tutorLink.toString());
    }

    /**
     * A stub ReadOnlyTutorLink whose students list can violate interface constraints.
     */
    private static class TutorLinkStub implements ReadOnlyTutorLink {
        private final ObservableList<Student> students = FXCollections.observableArrayList();

        TutorLinkStub(Collection<Student> students) {
            this.students.setAll(students);
        }

        @Override
        public ObservableList<Student> getStudentList() {
            return students;
        }
    }

}
