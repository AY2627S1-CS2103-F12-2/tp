package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.tutorlink.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.tutorlink.logic.commands.CommandTestUtil.VALID_SUBJECT_PHYSICS;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.tutorlink.testutil.EditStudentDescriptorBuilder;

public class EditStudentDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditStudentDescriptor descriptorWithSameValues = new EditStudentDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditStudentDescriptor editedAmy = new EditStudentDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different subjects -> returns false
        editedAmy = new EditStudentDescriptorBuilder(DESC_AMY).withSubjects(VALID_SUBJECT_PHYSICS).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptor();
        String expected = EditStudentDescriptor.class.getCanonicalName() + "{name="
                + editStudentDescriptor.getName().orElse(null) + ", subjects="
                + editStudentDescriptor.getSubjects().orElse(null) + "}";
        assertEquals(expected, editStudentDescriptor.toString());
    }
}
