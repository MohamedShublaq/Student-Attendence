package com.example.final_project;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ReportLectureOpreationsConrtroller implements Initializable {
    private Lecture lectures;
    @FXML
    private Label title;
    @FXML
    private TableView<Lecture> tableView;
    @FXML
    private TableColumn<Lecture, String> courseIdColumn;
    @FXML
    private TableColumn<Lecture, String> sectionIdColumn;
    @FXML
    private TableColumn<Lecture, String> studentIdColumn;
    @FXML
    private TableColumn<Lecture, Void> operationsColumn;
    @FXML
    private TextField searchField; // Add a TextField for search input

    private ObservableList<String> courses; // Declare the ObservableList
    private ObservableList<Lecture> data;

    private void loadDataFromDatabase(String courseId, String searchKeyword) {
        // Create a connection to the database
        String query = "SELECT takes.sec_id, takes.lec_id, takes.title, student.name " +
                "FROM takes " +
                "JOIN student ON takes.id = student.id " +
                "WHERE takes.course_id = ? AND takes.lec_id = ? AND takes.sec_id = ? " +
                "AND student.name LIKE ?"; // Add search condition

        data = FXCollections.observableArrayList();

        try (PreparedStatement statement = App.con.prepareStatement(query)) {
            statement.setString(1, courseId);
            statement.setString(2, lectures.getLectureId());
            statement.setString(3, lectures.getSectionId());
            statement.setString(4, "%" + searchKeyword + "%"); // Use search keyword in LIKE condition

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String sectionId = resultSet.getString("sec_id");
                String studentId = resultSet.getString("lec_id");
                String lectureTitle = resultSet.getString("title");
                String studentName = resultSet.getString("name");

                Lecture lecture = new Lecture(courseId, sectionId, studentId, lectureTitle);
                data.add(lecture);
            }

            // Set the data to the TableView
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private String getStudentId(String studentName) {
        String query = "SELECT id FROM student WHERE name = ?";
        String studentId = null;

        try (PreparedStatement statement = App.con.prepareStatement(query)) {
            statement.setString(1, studentName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                studentId = resultSet.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentId;
    }

    private void addStudentToAttendTable(Lecture lecture) {
        String insertQuery = "INSERT INTO attend (course_id, sec_id, lec_id, title, id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement insertStatement = App.con.prepareStatement(insertQuery)) {
            insertStatement.setString(1, lecture.getCourseId());
            insertStatement.setString(2, lecture.getSectionId());
            insertStatement.setString(3, lecture.getLectureId());
            insertStatement.setString(4, lecture.getLectureTitle());
            insertStatement.setString(5, getStudentId(searchField.getText()));

            int affectedRows = insertStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Student added to attend table: " + getStudentId(searchField.getText()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchButtonClicked() {
        String searchKeyword = searchField.getText(); // Get the search keyword
        String courseId = ""; // Get the selected course ID from ComboBox if needed
        loadDataFromDatabase(courseId, searchKeyword);
    }

    private void handleViewButton(Lecture lecture) {
        System.out.println("View lecture: " + lecture.getLectureId());
    }

    public void setLecture(Lecture lecture1) {
        this.lectures = lecture1;
        title.setText(lecture1.getLectureTitle());
    }

    private void setComboBoxes() {
        courses = FXCollections.observableArrayList(); // Initialize the ObservableList
        List<String> coursesList = getCourses();

        if (coursesList != null) {
            courses.addAll(coursesList);
        } else {
            title.setText("Failed to retrieve courses from the database.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();

        // Map the columns to the Lecture properties
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        sectionIdColumn.setCellValueFactory(new PropertyValueFactory<>("sectionId"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        operationsColumn.setCellFactory(param -> {
            TableCell<Lecture, Void> cell = new TableCell<>() {
                private final Button viewButton = new Button("View");
                private final Button deleteButton = new Button("Delete");
                private final Button editButton = new Button("Edit");
                private final Button addButton = new Button("Add"); // Add a new button

                {
                    viewButton.setOnAction(event -> {
                        Lecture lecture = getTableView().getItems().get(getIndex());
                        handleViewButton(lecture);
                    });

                    deleteButton.setOnAction(event -> {
                        Lecture lecture = getTableView().getItems().get(getIndex());
                        handleDeleteButton(lecture);
                    });

                    editButton.setOnAction(event -> {
                        Lecture lecture = getTableView().getItems().get(getIndex());
                        handleEditButton(lecture);
                    });

                    addButton.setOnAction(event -> {
                        Lecture lecture = getTableView().getItems().get(getIndex());
                        addStudentToAttendTable(lecture); // Call the method to add student to attend table
                        tableView.refresh(); // Refresh the table view after adding the student
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttonsBox = new HBox(viewButton, deleteButton, editButton, addButton); // Add the new button to the layout
                        setGraphic(buttonsBox);
                    }
                }
            };
            return cell;
        });
    }

    public ArrayList<String> getCourses() {
        String sql = "SELECT DISTINCT course_id FROM lecture";
        ArrayList<String> coursesList = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                coursesList.add(rs.getString(1));
            }
            return coursesList;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void handleDeleteButton(Lecture lecture) {
        String query = "DELETE FROM lecture WHERE lec_id = ? and course_id = ? and sec_id = ? and title = ?;";
        deleteRecords(lecture);

        try (PreparedStatement statement = App.con.prepareStatement(query)) {
            statement.setString(1, lecture.getLectureId());
            statement.setString(2, lecture.getCourseId());
            statement.setString(3, lecture.getSectionId());
            statement.setString(4, lecture.getLectureTitle());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                data.remove(lecture);
                tableView.refresh();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecords(Lecture lecture) {
        String SQL = "DELETE FROM records "
                + "WHERE  lec_id = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, lecture.getLectureId());
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void handleEditButton(Lecture lecture) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/editlectureopreation.fxml.fxml"));
            Parent root = loader.load();

            // Get the controller for the new FXML
            EditLectureOpreationController controller = loader.getController();

            // Pass the lecture object to the controller
            controller.setLecture(lecture);

            // Create a new stage for the new FXML
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Lecture");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
