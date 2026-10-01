package seedu.tutorlink.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StudentListPanelTest {

    @Test
    public void describeCount_allStudentsShown_showsTotal() {
        assertEquals("46 students", StudentListPanel.describeCount(46, 46));
        assertEquals("1 student", StudentListPanel.describeCount(1, 1));
        assertEquals("0 students", StudentListPanel.describeCount(0, 0));
    }

    @Test
    public void describeCount_filtered_explainsHowToShowAll() {
        assertEquals("Showing 2 of 46 · type list to show all", StudentListPanel.describeCount(2, 46));
        assertEquals("Showing 0 of 46 · type list to show all", StudentListPanel.describeCount(0, 46));
    }
}
