package seedu.tutorlink.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.storage.JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Subject;

public class JsonAdaptedStudentTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_SUBJECT = "Math/Physics";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final List<String> VALID_SUBJECTS = BENSON.getSubjects().stream()
            .map(subject -> subject.subjectName)
            .toList();

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
    public void toModelType_invalidSubject_throwsIllegalValueException() {
        List<String> invalidSubjects = new ArrayList<>(VALID_SUBJECTS);
        invalidSubjects.add(INVALID_SUBJECT);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, invalidSubjects);
        assertThrows(IllegalValueException.class, Subject.MESSAGE_CONSTRAINTS, student::toModelType);
    }

}
