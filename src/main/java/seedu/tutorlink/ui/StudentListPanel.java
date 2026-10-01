package seedu.tutorlink.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.model.student.Student;

/**
 * Panel containing the list of students, which highlights the student currently shown in detail.
 */
public class StudentListPanel extends UiPart<Region> {
    private static final String FXML = "StudentListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(StudentListPanel.class);

    @FXML
    private ListView<Student> studentListView;
    @FXML
    private Label studentCount;

    /**
     * Creates a {@code StudentListPanel} with the given {@code ObservableList},
     * highlighting the student held by {@code selectedStudent}.
     */
    public StudentListPanel(ObservableList<Student> studentList, ObservableValue<Student> selectedStudent) {
        super(FXML);
        studentListView.setItems(studentList);
        studentListView.setCellFactory(listView -> new StudentListViewCell());
        studentCount.textProperty().bind(Bindings.createStringBinding(() ->
                studentList.size() + (studentList.size() == 1 ? " student shown" : " students shown"), studentList));
        selectedStudent.addListener((observable, oldStudent, newStudent) -> highlight(newStudent));
    }

    private void highlight(Student student) {
        if (student == null) {
            studentListView.getSelectionModel().clearSelection();
            return;
        }
        studentListView.getSelectionModel().select(student);
        studentListView.scrollTo(student);
        // Adjust again after the list has laid out any newly added row, so the selected row is shown in full
        Platform.runLater(() -> scrollIntoView(studentListView.getItems().indexOf(student)));
    }

    /**
     * Scrolls the list by the smallest amount that shows the whole row at {@code index}.
     */
    private void scrollIntoView(int index) {
        if (index < 0) {
            return;
        }
        if (studentListView.lookup(".virtual-flow") instanceof VirtualFlow<?> flow) {
            flow.scrollTo(index);
        } else {
            studentListView.scrollTo(index);
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Student} using a {@code StudentCard}.
     */
    class StudentListViewCell extends ListCell<Student> {
        @Override
        protected void updateItem(Student student, boolean empty) {
            super.updateItem(student, empty);

            if (empty || student == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new StudentCard(student, getIndex() + 1).getRoot());
            }
            // Keep each row within the list's width, so long names wrap instead of adding a horizontal scroll bar
            setPrefWidth(0);
        }
    }

}
