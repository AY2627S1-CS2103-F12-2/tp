package seedu.tutorlink.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.interaction.Interaction;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.Subject;

/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";

    private final String name;
    private final List<String> subjects = new ArrayList<>();
    private final List<JsonAdaptedInteraction> interactions = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name,
            @JsonProperty("subjects") List<String> subjects,
            @JsonProperty("interactions") List<JsonAdaptedInteraction> interactions) {
        this.name = name;
        if (subjects != null) {
            this.subjects.addAll(subjects);
        }
        if (interactions != null) {
            this.interactions.addAll(interactions);
        }
    }

    /**
     * Constructs a student adapter without interactions for backward compatibility with existing callers.
     */
    public JsonAdaptedStudent(String name, List<String> subjects) {
        this(name, subjects, null);
    }

    /**
     * Converts a given {@code Student} into this class for Jackson use.
     */
    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        source.getSubjects().forEach(subject -> subjects.add(subject.subjectName));
        source.getInteractions().forEach(interaction -> interactions.add(new JsonAdaptedInteraction(interaction)));
    }

    /**
     * Converts this Jackson-friendly adapted student object into the model's {@code Student} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted student.
     */
    public Student toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        final List<Subject> modelSubjects = new ArrayList<>();
        for (String subject : subjects) {
            if (subject == null || !Subject.isValidSubject(subject)) {
                throw new IllegalValueException(Subject.MESSAGE_CONSTRAINTS);
            }
            modelSubjects.add(new Subject(subject));
        }

        final List<Interaction> modelInteractions = new ArrayList<>();
        for (JsonAdaptedInteraction interaction : interactions) {
            modelInteractions.add(interaction.toModelType());
        }

        return new Student(modelName, modelSubjects, modelInteractions);
    }

}
