package com.example.final_project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ViewLectureOpreationsConrtroller implements Initializable {
    @FXML
    private ListView<String> autocompleteListView;
    private ObservableList<String> autocompleteOptions;
    @FXML
    private TableView<Lecture> tableView;
    @FXML
    private TableColumn<Lecture, String> courseIdColumn;
    @FXML
    private TableColumn<Lecture, String> sectionIdColumn;
    @FXML
    private TableColumn<Lecture, String> lectureIdColumn;
    @FXML
    private TableColumn<Lecture, String> lectureTitleColumn;
    @FXML
    private TableColumn<Lecture, Void> operationsColumn;
    @FXML
    private TextField searchBar;
    @FXML
    private Label addLabel;
    @FXML
    private Button searchButton;

    @FXML
    private ComboBox<String> courses;

    private ObservableList<Lecture> data;


    @FXML
    private void switchToAdmin() throws IOException {
        App.setRoot("tutoropreation");
    }
    private void loadDataFromDatabase(String courseId) {
        // Create a connection to the database
        String query = "SELECT * FROM lecture WHERE course_id = ?";
        data = FXCollections.observableArrayList();

        try (PreparedStatement statement = App.con.prepareStatement(query)) {
            statement.setString(1, courseId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String sectionId = resultSet.getString("sec_id");
                String lectureId = resultSet.getString("lec_id");
                String lectureTitle = resultSet.getString("title");

                data.add(new Lecture(courseId, sectionId, lectureId, lectureTitle));
            }

            // Set the data to the TableView
            tableView.setItems(data);
        } catch (SQLException e) {
            showError("Error loading data from the database: " + e.getMessage());
        }
    }

    private void handleViewButton(Lecture lecture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/attendence.fxml"));
            Parent root = loader.load();

            AttendenceOpreationsConrtroller attendenceController = loader.getController();
            attendenceController.setCourseId(lecture.getCourseId());
            attendenceController.setSectionId(lecture.getSectionId());
            attendenceController.setLectureId(lecture.getLectureId());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Lecture Report"+lecture.getLectureId());
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private void setComboBoxes() {
        courses.setValue("Select Course");
        ArrayList<String> coursesList = getCourses();
        if (coursesList != null) {
            ObservableList<String> observableCourses = FXCollections.observableList(coursesList);
            courses.setItems(observableCourses);
        } else {
            addLabel.setText("Failed to retrieve courses from the database.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the autocompleteListView
        autocompleteListView = new ListView<>();

        autocompleteListView.setOnMouseClicked(event -> {
            // Handle the click event on the autocomplete list view
            String selectedTitle = autocompleteListView.getSelectionModel().getSelectedItem();
            if (selectedTitle != null) {
                // Perform the desired action when a title is selected from the autocomplete list view
                // For example, update a text field with the selected title
                searchBar.setText(selectedTitle);
                // ...
            }
            // Hide the autocomplete list view after the selection
            autocompleteListView.setVisible(false);
        });

        autocompleteOptions = FXCollections.observableArrayList();
        autocompleteListView.setItems(autocompleteOptions);
        setComboBoxes();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            searchLectures();
        });
        searchBar.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                autocompleteListView.setVisible(false);
            }
        });

        // Map the columns to the Lecture properties
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        sectionIdColumn.setCellValueFactory(new PropertyValueFactory<>("sectionId"));
        lectureIdColumn.setCellValueFactory(new PropertyValueFactory<>("lectureId"));
        lectureTitleColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTitle"));

        operationsColumn.setCellFactory(param -> {
            TableCell<Lecture, Void> cell = new TableCell<>() {
                private final Button viewButton = new Button("View");
                private final Button deleteButton = new Button("Delete");
                private final Button editButton = new Button("Edit");

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
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttonsBox = new HBox(viewButton, deleteButton, editButton);
                        setGraphic(buttonsBox);
                    }
                }
            };
            return cell;
        });
    }


    public void ViewLecture() {
        String selectedCourse = courses.getValue();
        if (selectedCourse != null) {
            loadDataFromDatabase(selectedCourse);
        } else {
            addLabel.setText("Please select a course.");
        }
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
            showError("Error retrieving courses from the database: " + ex.getMessage());
            return null;
        }
    }

    @FXML
    private void ViewLectureAction() throws IOException {
        App.setRoot("addstudentoperation");
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
            showError("Error deleting lecture from the database: " + e.getMessage());
        }
    }

    private void deleteRecords(Lecture lecture) {
        String SQL = "DELETE FROM records " + "WHERE lec_id = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, lecture.getLectureId());
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
        } catch (SQLException ex) {
            showError("Error deleting records from the database: " + ex.getMessage());
        }
    }

    private void handleEditButton(Lecture lecture) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/editlectureopreation.fxml"));
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
            showError("Error loading edit lecture view: " + e.getMessage());
        }
    }

    @FXML
    private void searchLectures() {
        String keyword = searchBar.getText().trim();
        if (!keyword.isEmpty()) {
            // Perform the search query
            String query = "SELECT * FROM lecture WHERE title LIKE ?";
            data = FXCollections.observableArrayList();
            try (PreparedStatement statement = App.con.prepareStatement(query)) {
                statement.setString(1, "%" + keyword + "%");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String courseId = resultSet.getString("course_id");
                    String sectionId = resultSet.getString("sec_id");
                    String lectureId = resultSet.getString("lec_id");
                    String lectureTitle = resultSet.getString("title");
                    data.add(new Lecture(courseId, sectionId, lectureId, lectureTitle));
                }
            } catch (SQLException e) {
                showError("Error searching for lectures: " + e.getMessage());
            }

            // Set the data to the TableView
            tableView.setItems(data);
        } else {
            // If the search keyword is empty, load all lectures
            String selectedCourse = courses.getValue();
            if (selectedCourse != null) {
                loadDataFromDatabase(selectedCourse);
            } else {
                addLabel.setText("Please select a course.");
            }
        }
    }
    private ArrayList<String> getLectureTitles() {
        String keyword = searchBar.getText().trim();
        if (!keyword.isEmpty()) {
            ArrayList<String> lectureTitles = new ArrayList<>();

            // Perform the search query to retrieve all lecture titles
            String query = "SELECT DISTINCT title FROM lecture";
            try (PreparedStatement statement = App.con.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    lectureTitles.add(title);
                }
            } catch (SQLException e) {
                showError("Error retrieving lecture titles from the database: " + e.getMessage());
                return null;
            }

            // Filter lecture titles based on the keyword and populate autocomplete options
            ArrayList<String> autocompleteOptions = new ArrayList<>();
            for (String title : lectureTitles) {
                if (title.toLowerCase().contains(keyword.toLowerCase())) {
                    autocompleteOptions.add(title);
                }
            }

            // Show the autocomplete options in the ListView
            autocompleteListView.setItems(FXCollections.observableArrayList(autocompleteOptions));
            autocompleteListView.setVisible(true);
            return autocompleteOptions;
        } else {
            // Empty keyword, hide the autocomplete options
            autocompleteListView.setVisible(false);
            return null;
        }
    }
    private void showError(String errorMessage) {
        // Display error message to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
    @FXML
    private void exportExcelButtonClicked() {
        String selectedCourseId = courses.getValue(); // Get the selected course ID from ComboBox
        String filePath = "attendance_report.xlsx"; // File path to save the Excel file

        // Get the attendance data for the selected course and student
        ObservableList<String[]> attendanceData = getAttendanceData(selectedCourseId);
        attendanceData.addAll(checkAttendanceData(selectedCourseId));
        // Calculate the total number of lectures and the threshold for 25% attendance
        int totalLectures = attendanceData.size();
        int threshold = (int) (totalLectures * 0.25);

        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance Report");

        // Create headers for the columns
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student ID");
        headerRow.createCell(1).setCellValue("Student Name");

        // Iterate over the attendance data and add the student IDs and names to the sheet
        int rowIndex = 1;
        for (String[] attendanceRecord : attendanceData) {
            String studentId = attendanceRecord[0];
            String studentName = attendanceRecord[1];

            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(studentId);
            dataRow.createCell(1).setCellValue(studentName);
        }

        // Autosize the columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        try {
            // Write the workbook to the file
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String[]> getAttendanceData(String courseId) {
        ObservableList<String[]> attendanceData = FXCollections.observableArrayList();

        // Get the total number of lectures in the course
        int totalLectures = getTotalLectures(courseId);

        // Get the number of lectures attended by each student
        String sql = "SELECT s.id, s.name, COUNT(a.lec_id) AS attended_lectures " +
                "FROM student s " +
                "LEFT JOIN attend a ON s.id = a.id AND a.course_id = ? " +
                "JOIN lecture l ON a.lec_id = l.lec_id " +
                "WHERE (a.course_id IS NULL OR a.lec_id IS NULL) " +
                "GROUP BY s.id, s.name " +
                "UNION " +
                "SELECT s.id, s.name, COUNT(a.lec_id) AS attended_lectures " +
                "FROM student s " +
                "JOIN attend a ON s.id = a.id AND a.course_id = ? " +
                "JOIN lecture l ON a.lec_id = l.lec_id " +
                "WHERE a.course_id = ? " +
                "GROUP BY s.id, s.name " +
                "HAVING (COUNT(a.lec_id) / ?) < 0.75";

        try (PreparedStatement pstmt = App.con.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            pstmt.setString(2, courseId);
            pstmt.setString(3, courseId);
            pstmt.setInt(4, totalLectures);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String studentId = resultSet.getString("id");
                String studentName = resultSet.getString("name");
                int attendedLectures = resultSet.getInt("attended_lectures");

                // Calculate the percentage of attended lectures
                double attendancePercentage = (double) attendedLectures / totalLectures * 100;

                // Check if the attendance is below 75%
                if (attendancePercentage < 75) {
                    String[] studentData = {studentId, studentName};
                    attendanceData.add(studentData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceData;
    }
    private ObservableList<String[]> checkAttendanceData(String courseId) {
        ObservableList<String[]> attendanceData = FXCollections.observableArrayList();

        // Get the total number of lectures in the course
        int totalLectures = getTotalLectures(courseId);

        // Get the number of lectures attended by each student
        String sql = "SELECT s.id, s.name, COUNT(a.lec_id) AS attended_lectures " +
                "FROM student s " +
                "LEFT JOIN attend a ON s.id = a.id AND a.course_id = ? " +
                "JOIN lecture l ON a.lec_id = l.lec_id " +
                "WHERE (a.course_id IS NULL OR a.lec_id IS NULL) " +
                "GROUP BY s.id, s.name " +
                "UNION " +
                "SELECT s.id, s.name, 0 AS attended_lectures " +
                "FROM student s " +
                "WHERE s.id NOT IN (SELECT id FROM attend WHERE course_id = ?)";

        try (PreparedStatement pstmt = App.con.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            pstmt.setString(2, courseId);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String studentId = resultSet.getString("id");
                String studentName = resultSet.getString("name");
                int attendedLectures = resultSet.getInt("attended_lectures");

                // Calculate the percentage of attended lectures
                double attendancePercentage = (double) attendedLectures / totalLectures * 100;

                // Check if the attendance is below 75%
                if (attendancePercentage < 75) {
                    String[] studentData = {studentId, studentName};
                    attendanceData.add(studentData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceData;
    }



    private int getTotalLectures(String courseId) {
        int totalLectures = 0;

        String sql = "SELECT COUNT(*) AS total_lectures FROM lecture WHERE course_id = ?";

        try (PreparedStatement pstmt = App.con.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                totalLectures = resultSet.getInt("total_lectures");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalLectures;
    }

    private String getStudentName(String studentId) {
        String studentName = "";

        String sql = "SELECT name FROM student WHERE id = ?";

        try (PreparedStatement pstmt = App.con.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                studentName = resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentName;
    }

}
