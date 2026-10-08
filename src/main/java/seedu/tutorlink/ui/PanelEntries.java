package seedu.tutorlink.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Builds and toggles the parts shared by the record panels, such as the interaction and follow-up lists.
 */
final class PanelEntries {

    private PanelEntries() {}

    /**
     * Returns an entry with a small {@code heading} line (e.g. a date) above the wrapped {@code text}.
     */
    static VBox createEntry(String heading, String text) {
        Label headingLabel = new Label(heading);
        headingLabel.getStyleClass().add("entry-when");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("entry-text");
        textLabel.setWrapText(true);

        VBox entry = new VBox(2, headingLabel, textLabel);
        entry.getStyleClass().add("entry");
        return entry;
    }

    /**
     * Shows or hides {@code region}, removing it from the layout while hidden.
     */
    static void setShown(Region region, boolean isShown) {
        region.setVisible(isShown);
        region.setManaged(isShown);
    }
}
