package seedu.tutorlink.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.ReadOnlyTutorLink;
import seedu.tutorlink.model.TutorLink;
import seedu.tutorlink.model.student.Student;

/**
 * An Immutable TutorLink that is serializable to JSON format.
 */
@JsonRootName(value = "tutorlink")
class JsonSerializableTutorLink {

    public static final String MESSAGE_DUPLICATE_STUDENT = "Students list contains duplicate student(s).";

    private final List<JsonAdaptedStudent> students = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableTutorLink} with the given students.
     */
    @JsonCreator
    public JsonSerializableTutorLink(@JsonProperty("students") List<JsonAdaptedStudent> students) {
        this.students.addAll(students);
    }

    /**
     * Converts a given {@code ReadOnlyTutorLink} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableTutorLink}.
     */
    public JsonSerializableTutorLink(ReadOnlyTutorLink source) {
        students.addAll(source.getStudentList().stream().map(JsonAdaptedStudent::new).collect(Collectors.toList()));
    }

    /**
     * Converts this TutorLink into the model's {@code TutorLink} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public TutorLink toModelType() throws IllegalValueException {
        TutorLink tutorLink = new TutorLink();
        for (JsonAdaptedStudent jsonAdaptedStudent : students) {
            Student student = jsonAdaptedStudent.toModelType();
            if (tutorLink.hasStudent(student)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_STUDENT);
            }
            tutorLink.addStudent(student);
        }
        return tutorLink;
    }

}
