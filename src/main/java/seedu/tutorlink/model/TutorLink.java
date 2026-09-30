package seedu.tutorlink.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.tutorlink.commons.util.ToStringBuilder;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.model.student.UniqueStudentList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameStudent comparison).
 */
public class TutorLink implements ReadOnlyTutorLink {

    private final UniqueStudentList students = new UniqueStudentList();

    public TutorLink() {}

    /**
     * Creates a TutorLink using the Students in the {@code toBeCopied}
     */
    public TutorLink(ReadOnlyTutorLink toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the student list with {@code students}.
     * {@code students} must not contain duplicate students.
     */
    public void setStudents(List<Student> students) {
        this.students.setStudents(students);
    }

    /**
     * Resets the existing data of this {@code TutorLink} with {@code newData}.
     */
    public void resetData(ReadOnlyTutorLink newData) {
        requireNonNull(newData);

        setStudents(newData.getStudentList());
    }

    //// student-level operations

    /**
     * Returns true if a student with the same identity as {@code student} exists in TutorLink.
     */
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return students.contains(student);
    }

    /**
     * Adds a student to TutorLink.
     * The student must not already exist in TutorLink.
     */
    public void addStudent(Student p) {
        students.add(p);
    }

    /**
     * Replaces the given student {@code target} in the list with {@code editedStudent}.
     * {@code target} must exist in TutorLink.
     * The student identity of {@code editedStudent} must not be the same as another existing student in TutorLink.
     */
    public void setStudent(Student target, Student editedStudent) {
        requireNonNull(editedStudent);

        students.setStudent(target, editedStudent);
    }

    /**
     * Removes {@code key} from this {@code TutorLink}.
     * {@code key} must exist in TutorLink.
     */
    public void removeStudent(Student key) {
        students.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("students", students)
                .toString();
    }

    @Override
    public ObservableList<Student> getStudentList() {
        return students.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TutorLink otherTutorLink)) {
            return false;
        }

        return students.equals(otherTutorLink.students);
    }

    @Override
    public int hashCode() {
        return students.hashCode();
    }
}
