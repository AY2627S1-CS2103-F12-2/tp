package seedu.tutorlink.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.exceptions.IllegalValueException;
import seedu.tutorlink.model.interaction.Interaction;

public class JsonAdaptedInteractionTest {

    @Test
    public void toModelType_validInteractionDetails_returnsInteraction() throws Exception {
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", "Discussed revision plan");

        assertEquals(new Interaction(LocalDate.of(2026, 10, 1),
                Optional.of(LocalTime.of(14, 30)), "Discussed revision plan"),
                adaptedInteraction.toModelType());
    }

    @Test
    public void toModelType_missingTime_returnsInteractionWithoutTime() throws Exception {
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", null, "Discussed revision plan");

        assertEquals(Optional.empty(), adaptedInteraction.toModelType().getTime());
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-02-30", "14:30", "Discussed revision plan");

        assertThrows(IllegalValueException.class, JsonAdaptedInteraction.INVALID_DATE_MESSAGE,
                adaptedInteraction::toModelType);
    }

    @Test
    public void toModelType_invalidTime_throwsIllegalValueException() {
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "25:00", "Discussed revision plan");

        assertThrows(IllegalValueException.class, JsonAdaptedInteraction.INVALID_TIME_MESSAGE,
                adaptedInteraction::toModelType);
    }

    @Test
    public void toModelType_missingNote_throwsIllegalValueException() {
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", null);

        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedInteraction.MISSING_FIELD_MESSAGE_FORMAT, "note"),
                adaptedInteraction::toModelType);
    }

    @Test
    public void toModelType_multilineNote_throwsIllegalValueException() {
        for (String multilineNote : List.of("First line\nSecond line", "First line\rSecond line")) {
            JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                    "2026-10-01", "14:30", multilineNote);

            assertThrows(IllegalValueException.class, JsonAdaptedInteraction.INVALID_NOTE_MULTILINE_MESSAGE,
                    adaptedInteraction::toModelType);
        }
    }

    @Test
    public void toModelType_maxLengthNote_returnsInteraction() throws Exception {
        String maximumLengthNote = "A".repeat(JsonAdaptedInteraction.MAX_NOTE_LENGTH);
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", maximumLengthNote);

        assertEquals(maximumLengthNote, adaptedInteraction.toModelType().getNote());
    }

    @Test
    public void toModelType_unicodeBoundaryWhitespace_returnsStrippedNote() throws Exception {
        String maximumLengthNote = "A".repeat(JsonAdaptedInteraction.MAX_NOTE_LENGTH);
        String noteWithUnicodeBoundaryWhitespace = "\u2003" + maximumLengthNote + "\u2003";
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", noteWithUnicodeBoundaryWhitespace);

        assertEquals(maximumLengthNote, adaptedInteraction.toModelType().getNote());
    }

    @Test
    public void toModelType_overlongNote_throwsIllegalValueException() {
        String overlongNote = "A".repeat(JsonAdaptedInteraction.MAX_NOTE_LENGTH + 1);
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", overlongNote);

        assertThrows(IllegalValueException.class, JsonAdaptedInteraction.INVALID_NOTE_LENGTH_MESSAGE,
                adaptedInteraction::toModelType);
    }

    @Test
    public void toModelType_maximumEmojiNote_returnsInteraction() throws Exception {
        String emoji = String.valueOf(Character.toChars(0x1F600));
        String maximumEmojiNote = emoji.repeat(JsonAdaptedInteraction.MAX_NOTE_LENGTH);
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", maximumEmojiNote);

        assertEquals(maximumEmojiNote, adaptedInteraction.toModelType().getNote());
    }

    @Test
    public void toModelType_overlongEmojiNote_throwsIllegalValueException() {
        String emoji = String.valueOf(Character.toChars(0x1F600));
        String overlongEmojiNote = emoji.repeat(JsonAdaptedInteraction.MAX_NOTE_LENGTH + 1);
        JsonAdaptedInteraction adaptedInteraction = new JsonAdaptedInteraction(
                "2026-10-01", "14:30", overlongEmojiNote);

        assertThrows(IllegalValueException.class, JsonAdaptedInteraction.INVALID_NOTE_LENGTH_MESSAGE,
                adaptedInteraction::toModelType);
    }
}
