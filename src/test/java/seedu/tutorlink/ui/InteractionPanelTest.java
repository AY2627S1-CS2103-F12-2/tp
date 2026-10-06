package seedu.tutorlink.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.interaction.Interaction;

public class InteractionPanelTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 14);

    @Test
    public void describeWhen_withTime_showsDateAndTime() {
        Interaction interaction = new Interaction(DATE, Optional.of(LocalTime.of(15, 0)), "Factoring");
        assertEquals("2026-09-14 15:00", InteractionPanel.describeWhen(interaction));
    }

    @Test
    public void describeWhen_withoutTime_showsDateOnly() {
        Interaction interaction = new Interaction(DATE, Optional.empty(), "Factoring");
        assertEquals("2026-09-14", InteractionPanel.describeWhen(interaction));
    }
}
