package seedu.tutorlink.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandBoxTest {

    @Test
    public void replaceLineBreaksWithSpaces_eachLineBreakStyle_becomesOneSpace() {
        assertEquals("line one line two", CommandBox.replaceLineBreaksWithSpaces("line one\nline two"));
        assertEquals("line one line two", CommandBox.replaceLineBreaksWithSpaces("line one\r\nline two"));
        assertEquals("line one line two", CommandBox.replaceLineBreaksWithSpaces("line one\rline two"));
        assertEquals("a  b", CommandBox.replaceLineBreaksWithSpaces("a\n\nb"));
    }

    @Test
    public void replaceLineBreaksWithSpaces_noLineBreak_unchanged() {
        assertEquals("followup list n/John Tan", CommandBox.replaceLineBreaksWithSpaces("followup list n/John Tan"));
        assertEquals("", CommandBox.replaceLineBreaksWithSpaces(""));
    }
}
