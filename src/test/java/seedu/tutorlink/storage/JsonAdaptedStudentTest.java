package seedu.tutorlink.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.storage.JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.followup.FollowUp;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

public class JsonAdaptedStudentTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_SUBJECT = "Math/Physics";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final List<String> VALID_SUBJECTS = BENSON.getSubjects().stream()
            .map(subject -> subject.subjectName)
            .toList();

    @Test
    public void toModelType_followUps_preservesInsertionOrderAndOtherRecords() throws Exception {
        JsonAdaptedInteraction interaction = new JsonAdaptedInteraction("2026-10-01", null, "Revision lesson");
        Student student = BENSON.withInteraction(interaction.toModelType())
                .withFollowUp(new FollowUp("Later", LocalDate.of(2026, 10, 15)))
                .withFollowUp(new FollowUp("Earlier", LocalDate.of(2026, 10, 14)));
        assertEquals(student, new JsonAdaptedStudent(student).toModelType());
    }

    @Test
    public void toModelType_missingOrEmptyFollowUps_returnsEmptyList() throws Exception {
        assertEquals(List.of(), new JsonAdaptedStudent(VALID_NAME, VALID_SUBJECTS).toModelType().getFollowUps());
        assertEquals(List.of(), new JsonAdaptedStudent(VALID_NAME, VALID_SUBJECTS, null, List.of())
                .toModelType().getFollowUps());
    }

    @Test
    public void toModelType_invalidFollowUp_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_SUBJECTS, null,
                List.of(new JsonAdaptedFollowUp("", "2026-10-15")));
        assertThrows(IllegalValueException.class, student::toModelType);
        List<JsonAdaptedFollowUp> followUps = new ArrayList<>();
        followUps.add(null);
        JsonAdaptedStudent nullEntryStudent = new JsonAdaptedStudent(VALID_NAME, VALID_SUBJECTS, null, followUps);
        assertThrows(IllegalValueException.class, nullEntryStudent::toModelType);
    }

    @Test
    public void toModelType_nullInteraction_throwsIllegalValueException() {
        List<JsonAdaptedInteraction> interactions = new ArrayList<>();
        interactions.add(null);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_SUBJECTS, interactions);

        assertThrows(IllegalValueException.class, JsonAdaptedStudent.NULL_INTERACTION_MESSAGE,
                student::toModelType);
    }

    @Test
    public void toModelType_validStudentDetails_returnsStudent() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(BENSON);
        assertEquals(BENSON, student.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(INVALID_NAME, VALID_SUBJECTS);
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, VALID_SUBJECTS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_missingSubjects_returnsStudentWithoutSubjects() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, null);
        assertEquals(List.of(), student.toModelType().getSubjects());
    }

    @Test
    public void toModelType_missingInteractions_returnsStudentWithoutInteractions() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_SUBJECTS);
        assertEquals(List.of(), student.toModelType().getInteractions());
    }

    @Test
    public void toModelType_validInteractions_returnsStudentWithInteractions() throws Exception {
        JsonAdaptedInteraction interaction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", "Discussed revision plan");
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_SUBJECTS, List.of(interaction));

        assertEquals(List.of(interaction.toModelType()), student.toModelType().getInteractions());
    }

    @Test
    public void toModelType_invalidSubject_throwsIllegalValueException() {
        List<String> invalidSubjects = new ArrayList<>(VALID_SUBJECTS);
        invalidSubjects.add(INVALID_SUBJECT);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, invalidSubjects);
        assertThrows(IllegalValueException.class, Subject.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_nullSubject_throwsIllegalValueException() {
        List<String> invalidSubjects = new ArrayList<>(VALID_SUBJECTS);
        invalidSubjects.add(null);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, invalidSubjects);

        assertThrows(IllegalValueException.class, Subject.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_duplicateSubjects_throwsIllegalValueException() {
        for (List<String> duplicates : List.of(List.of("Math", "Math"), List.of("Math", "math"),
                List.of("Math", "math "), List.of(" Math", "MATH"))) {
            JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, duplicates);

            assertThrows(IllegalValueException.class, student::toModelType);
        }
    }

    @Test
    public void toModelType_distinctSubjectsWithSurroundingSpaces_trimsAndKeepsOrder() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, List.of(" Physics ", "Math", "Chemistry "));

        assertEquals(List.of("Physics", "Math", "Chemistry"), student.toModelType().getSubjects().stream()
                .map(subject -> subject.subjectName).toList());
    }

}
