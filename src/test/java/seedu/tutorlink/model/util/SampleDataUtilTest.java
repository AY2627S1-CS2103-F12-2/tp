package seedu.tutorlink.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.student.Student;

public class SampleDataUtilTest {

    @Test
    public void getSampleTutorLink_containsAllSampleStudents() {
        assertEquals(SampleDataUtil.getSampleStudents().length,
                SampleDataUtil.getSampleTutorLink().getStudentList().size());
    }

    @Test
    public void getSampleStudents_someHaveInteractionsAndFollowUps() {
        List<Student> students = List.of(SampleDataUtil.getSampleStudents());
        assertTrue(students.stream().anyMatch(student -> !student.getInteractions().isEmpty()));
        assertTrue(students.stream().anyMatch(student -> !student.getFollowUps().isEmpty()));
    }
}
