package seedu.tutorlink.model.interaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class InteractionTest {

    @Test
    public void constructor_validDetails_storesDetails() {
        Interaction interaction = new Interaction(LocalDate.of(2026, 10, 1),
                Optional.of(LocalTime.of(14, 30)), "  Discussed revision plan  ");

        assertEquals(LocalDate.of(2026, 10, 1), interaction.getDate());
        assertEquals(Optional.of(LocalTime.of(14, 30)), interaction.getTime());
        assertEquals("Discussed revision plan", interaction.getNote());
    }

    @Test
    public void constructor_blankNote_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Interaction(
                LocalDate.of(2026, 10, 1), Optional.empty(), "   "));
    }

    @Test
    public void equals_sameDetails_returnsTrue() {
        Interaction first = new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Discussed revision plan");
        Interaction second = new Interaction(LocalDate.of(2026, 10, 1), Optional.empty(), "Discussed revision plan");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
