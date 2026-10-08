package seedu.tutorlink.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_SUBJECT_PHYSICS;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.ALICE;
import static seedu.tutorlink.testutil.TypicalStudents.BOB;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.testutil.StudentBuilder;

public class StudentTest {

    private static final FollowUp FIRST_FOLLOW_UP =
            new FollowUp("Review algebra", LocalDate.of(2026, 10, 8));
    private static final FollowUp SECOND_FOLLOW_UP =
            new FollowUp("Revisit fractions", LocalDate.of(2026, 10, 9));

    @Test
    public void constructor_withoutFollowUps_emptyList() {
        Student student = new StudentBuilder().build();
        assertEquals(List.of(), student.getFollowUps());
        Student studentWithInteractions = new Student(student.getName(), student.getSubjects(), List.of());
        assertEquals(List.of(), studentWithInteractions.getFollowUps());
        Student studentWithoutInteractions = new Student(student.getName(), student.getSubjects());
        assertEquals(List.of(), studentWithoutInteractions.getFollowUps());
    }

    @Test
    public void constructor_followUps_defensiveCopy() {
        Student original = new StudentBuilder().build();
        List<FollowUp> followUps = new ArrayList<>(List.of(FIRST_FOLLOW_UP));
        Student student = new Student(original.getName(), original.getSubjects(), List.of(), followUps);

        followUps.clear();

        assertEquals(List.of(FIRST_FOLLOW_UP), student.getFollowUps());
        assertThrows(UnsupportedOperationException.class, () -> student.getFollowUps().add(SECOND_FOLLOW_UP));
    }

    @Test
    public void constructor_nullFollowUps_throwsNullPointerException() {
        Student student = new StudentBuilder().build();
        assertThrows(NullPointerException.class, () ->
                new Student(student.getName(), student.getSubjects(), List.of(), null));
        List<FollowUp> followUps = new ArrayList<>();
        followUps.add(null);
        assertThrows(NullPointerException.class, () ->
                new Student(student.getName(), student.getSubjects(), List.of(), followUps));
    }

    @Test
    public void withFollowUp_appendsWithoutMutatingOriginalStudent() {
        Interaction interaction = new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Revision plan");
        Student student = new StudentBuilder().build().withInteraction(interaction).withFollowUp(FIRST_FOLLOW_UP);

        Student updatedStudent = student.withFollowUp(SECOND_FOLLOW_UP);

        assertEquals(List.of(FIRST_FOLLOW_UP), student.getFollowUps());
        assertEquals(List.of(FIRST_FOLLOW_UP, SECOND_FOLLOW_UP), updatedStudent.getFollowUps());
        assertEquals(student.getName(), updatedStudent.getName());
        assertEquals(student.getSubjects(), updatedStudent.getSubjects());
        assertEquals(student.getInteractions(), updatedStudent.getInteractions());
        assertThrows(NullPointerException.class, () -> student.withFollowUp(null));
    }

    @Test
    public void withInteraction_preservesFollowUps() {
        Student student = new StudentBuilder().build().withFollowUp(FIRST_FOLLOW_UP);
        Interaction interaction = new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Revision plan");

        assertEquals(student.getFollowUps(), student.withInteraction(interaction).getFollowUps());
    }

    @Test
    public void equalsAndHashCode_includeFollowUps() {
        Student student = new StudentBuilder().build().withFollowUp(FIRST_FOLLOW_UP);
        Student copy = new StudentBuilder(student).build();

        assertEquals(student, copy);
        assertEquals(student.hashCode(), copy.hashCode());
        assertFalse(student.equals(new StudentBuilder().build()));
        assertFalse(student.equals(new StudentBuilder().build().withFollowUp(SECOND_FOLLOW_UP)));
        assertFalse(student.equals(student.withFollowUp(SECOND_FOLLOW_UP)));
        assertTrue(student.isSameStudent(student.withFollowUp(SECOND_FOLLOW_UP)));
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Student student = new StudentBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> student.getSubjects().remove(0));
    }

    @Test
    public void interactions_modifyList_throwsUnsupportedOperationException() {
        Interaction interaction = new Interaction(LocalDate.of(2026, 10, 1),
                Optional.of(LocalTime.of(14, 30)), "Discussed revision plan");
        Student student = new StudentBuilder().build().withInteraction(interaction);

        assertThrows(UnsupportedOperationException.class, () -> student.getInteractions().remove(0));
    }

    @Test
    public void withInteraction_appendsInteractionWithoutMutatingOriginalStudent() {
        Student student = new StudentBuilder().build();
        Interaction interaction = new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(),
                "Discussed revision plan");

        Student updatedStudent = student.withInteraction(interaction);

        assertEquals(List.of(), student.getInteractions());
        assertEquals(List.of(interaction), updatedStudent.getInteractions());
        assertEquals(student.getName(), updatedStudent.getName());
        assertEquals(student.getSubjects(), updatedStudent.getSubjects());
    }

    @Test
    public void isSameStudent() {
        // same object -> returns true
        assertTrue(ALICE.isSameStudent(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameStudent(null));

        // same name, all other attributes different -> returns true
        Student editedAlice = new StudentBuilder(ALICE).withSubjects(VALID_SUBJECT_PHYSICS).build();
        assertTrue(ALICE.isSameStudent(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new StudentBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameStudent(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Student editedBob = new StudentBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSameStudent(editedBob));

        // name has trailing spaces, all other attributes same -> returns true
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new StudentBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSameStudent(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Student aliceCopy = new StudentBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different student -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Student editedAlice = new StudentBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different subjects -> returns false
        editedAlice = new StudentBuilder(ALICE).withSubjects(VALID_SUBJECT_PHYSICS).build();
        assertFalse(ALICE.equals(editedAlice));

        // different interactions -> returns false
        editedAlice = new StudentBuilder(ALICE).build().withInteraction(
                new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Discussed revision plan"));
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Student.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", subjects=" + ALICE.getSubjects() + ", interactions=" + ALICE.getInteractions()
                + ", followUps=" + ALICE.getFollowUps() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
