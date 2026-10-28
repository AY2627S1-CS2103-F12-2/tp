package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.index.Index;
import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.followup.FollowUpStatus;
import seedu.tutorlink.model.student.Student;

/**
 * Checks that shared status commands detect broken model contracts before reporting success.
 */
public class ChangeFollowUpStatusCommandTest {

    private static final Student STUDENT = BENSON.withFollowUp(
            new FollowUp("Review algebra", LocalDate.of(2026, 11, 1)));

    @Test
    public void execute_transitionDropsHistory_failsBeforeUpdatingModel() {
        Student withoutHistory = new Student(STUDENT.getName(), STUDENT.getSubjects(),
                STUDENT.getInteractions(), List.of());
        Student faultyStudent = new TransitionStubStudent(STUDENT, withoutHistory);
        Model model = new ModelManager();
        model.addStudent(faultyStudent);
        model.setSelectedStudent(faultyStudent);

        assertThrows(AssertionError.class, () ->
                new DoneFollowUpCommand(STUDENT.getName(), Index.fromOneBased(1)).execute(model));

        assertEquals(List.of(faultyStudent), model.getTutorLink().getStudentList());
        assertEquals(faultyStudent, model.getSelectedStudent().get());
    }

    @Test
    public void execute_transitionLeavesFollowUpOutstanding_failsBeforeUpdatingModel() {
        Student faultyStudent = new TransitionStubStudent(STUDENT, STUDENT);
        Model model = new ModelManager();
        model.addStudent(faultyStudent);
        model.setSelectedStudent(faultyStudent);

        assertThrows(AssertionError.class, () ->
                new CancelFollowUpCommand(STUDENT.getName(), Index.fromOneBased(1)).execute(model));

        assertEquals(List.of(faultyStudent), model.getTutorLink().getStudentList());
        assertEquals(faultyStudent, model.getSelectedStudent().get());
    }

    @Test
    public void execute_modelIgnoresUpdate_throwsAssertionError() {
        int[] updateAttempts = {0};
        Model model = new ModelManager() {
            @Override
            public void setStudent(Student target, Student editedStudent) {
                updateAttempts[0]++;
            }
        };
        model.addStudent(STUDENT);
        model.setSelectedStudent(STUDENT);

        assertThrows(AssertionError.class, () ->
                new DoneFollowUpCommand(STUDENT.getName(), Index.fromOneBased(1)).execute(model));

        assertEquals(1, updateAttempts[0]);
        assertEquals(List.of(STUDENT), model.getTutorLink().getStudentList());
        assertEquals(STUDENT, model.getSelectedStudent().get());
    }

    /**
     * Returns a deliberately invalid transition result to exercise the command's consistency checks.
     */
    private static class TransitionStubStudent extends Student {
        private final Student transitionResult;

        TransitionStubStudent(Student original, Student transitionResult) {
            super(original.getName(), original.getSubjects(), original.getInteractions(), original.getFollowUps());
            this.transitionResult = transitionResult;
        }

        @Override
        public Student withOutstandingFollowUpStatus(int index, FollowUpStatus status) {
            return transitionResult;
        }
    }
}
