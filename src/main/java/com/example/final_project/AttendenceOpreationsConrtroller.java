package com.example.final_project;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.util.Callback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendenceOpreationsConrtroller {
    @FXML
    private Button excelButton;
    @FXML
    private Label coursesLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private ListView<String>searchResultsListView;
    @FXML
    private Label sectionsLabel;
    @FXML
    private Label lecturesLabel;
    @FXML
    private Label numberLabel;
    @FXML
    private TextField name;
    @FXML
    private Button viewButton;
    @FXML
    private TableView<Student> student_table;
    @FXML
    private TableColumn<Student, String> student_id;
    @FXML
    private TableColumn<Student, String> course_id;
    @FXML
    private TableColumn<Student, String> section_id;
    @FXML
    private TableColumn<Student, String> lecture_id;
    @FXML
    private TableColumn<Student, Void> operations;
    private Connection connection = App.con;

    public void setCourseId(String courseId) {
        coursesLabel.setText(("Course ID: ")+courseId);
    }

    public void setSectionId(String sectionId) {
        sectionsLabel.setText(("Section ID: ")+sectionId);
    }

    public void setLectureId(String lectureId) {
        lecturesLabel.setText(("Lecture ID: ")+lectureId);
    }


    @FXML
    public void initialize() {
        resultLabel = new Label(); // Initialize the resultLabel object
        initializeTable();
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                searchStudents(newValue);
            }
        });
        excelButton.setOnAction(this::handleExcelButtonAction);
        viewButton.setOnAction(this::handleViewButtonAction);
        // Assuming you have a Button named "viewButton" next to the delete button

    }

    @FXML
    private void handleExcelButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("Lecture Attendance"+lecturesLabel.getText().replace("Lecture ID: ", "")+".xlsx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Show save file dialog
        File file = fileChooser.showSaveDialog(excelButton.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();

            // Export table to Excel using the specified file path
            exportTableToExcel(filePath);
        }
    }

    private void exportTableToExcel(String filePath) {
        // Get the current data from the table
        ObservableList<Student> students = student_table.getItems();

        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student ID");
        headerRow.createCell(1).setCellValue("Course ID");
        headerRow.createCell(2).setCellValue("Section ID");
        headerRow.createCell(3).setCellValue("Lecture ID");

        // Add data rows to the sheet
        int rowNum = 1;
        for (Student student : students) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(student.studentIdProperty().get());
            dataRow.createCell(1).setCellValue(student.courseIdProperty().get());
            dataRow.createCell(2).setCellValue(student.sectionIdProperty().get());
            dataRow.createCell(3).setCellValue(student.lectureIdProperty().get());
        }

        // Auto-size the columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save the workbook to the specified file path
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }

        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
    }

    @FXML
    private void handleViewButtonAction(ActionEvent event) {
        String courseId = coursesLabel.getText().replace("Course ID: ", "");
        String sectionId = sectionsLabel.getText().replace("Section ID: ", "");
        String lectureId = lecturesLabel.getText().replace("Lecture ID: ", "");
        loadStudents(courseId, sectionId, lectureId);
        loadnumber(courseId, sectionId, lectureId);

    }

    private void loadStudents(String courseId, String sectionId, String lectureId) {
        if (student_table == null) {
            System.out.println("TableView is null");
            return;
        }
        student_table.getItems().clear();

        try {
            String sql = "SELECT * FROM attend WHERE course_id = ? AND sec_id = ? AND lec_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, courseId);
            statement.setString(2, sectionId);
            statement.setString(3, lectureId);
            ResultSet resultSet = statement.executeQuery();

            ObservableList<Student> students = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String studentId = resultSet.getString("id");
                String courseID = resultSet.getString("course_id");
                String sectionID = resultSet.getString("sec_id");
                String lectureID = resultSet.getString("lec_id");

                Student student = new Student(studentId, courseID, sectionID, lectureID);
                students.add(student);
            }

            student_table.setItems(students);

            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
    }
    private void loadnumber(String courseId, String sectionId, String lectureId) {

        int studentnum = 0;
        try {
            String sql = "SELECT count(id) FROM attend WHERE course_id = ? AND sec_id = ? AND lec_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, courseId);
            statement.setString(2, sectionId);
            statement.setString(3, lectureId);
            ResultSet resultSet = statement.executeQuery();

            ObservableList<Student> students = FXCollections.observableArrayList();

            while (resultSet.next()) {
                studentnum = resultSet.getInt(1);
            }
            numberLabel.setText("The number of attendees: "+studentnum);
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTable() {
        student_id.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        course_id.setCellValueFactory(cellData -> cellData.getValue().courseIdProperty());
        section_id.setCellValueFactory(cellData -> cellData.getValue().sectionIdProperty());
        lecture_id.setCellValueFactory(cellData -> cellData.getValue().lectureIdProperty());
        operations.setCellFactory(createButtonCellFactory());
    }

    private Callback<TableColumn<Student, Void>, TableCell<Student, Void>> createButtonCellFactory() {
        return new Callback<TableColumn<Student, Void>, TableCell<Student, Void>>() {
            @Override
            public TableCell<Student, Void> call(final TableColumn<Student, Void> param) {
                final TableCell<Student, Void> cell = new TableCell<Student, Void>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Student student = getTableView().getItems().get(getIndex());
                            deleteStudentFromDatabase(student);
                            getTableView().getItems().remove(student);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttonsContainer = new HBox(deleteButton);
                            buttonsContainer.setSpacing(5);
                            setGraphic(buttonsContainer);
                        }
                    }
                };
                return cell;
            }
        };
    }


    private void deleteStudentFromDatabase(Student student) {
        try {
            String sql = "DELETE FROM attend WHERE id = ? AND lec_id = ? AND course_id = ? AND sec_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, student.studentIdProperty().get());
            statement.setString(2, student.lectureIdProperty().get());
            statement.setString(3, student.courseIdProperty().get());
            statement.setString(4, student.sectionIdProperty().get());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
    }

    private ResultSet activeResultSet; // Declare a variable to store the active ResultSet

    @FXML
    private void searchStudents(String searchQuery) {
        searchResultsListView.getItems().clear();

        try {
            String sql = "SELECT s.id, s.name, p.phone_number " +
                    "FROM student s " +
                    "JOIN takes t ON s.id = t.id " +
                    "JOIN phone_number p ON s.id = p.id " +
                    "WHERE t.course_id = ? AND (s.name LIKE ? OR p.phone_number LIKE ? OR s.id LIKE ?)";
            PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1, coursesLabel.getText().replace("Course ID: ", ""));
            statement.setString(2, "%" + searchQuery + "%");
            statement.setString(3, "%" + searchQuery + "%");
            statement.setString(4, "%" + searchQuery + "%");

            // Close the active ResultSet if it exists
            if (activeResultSet != null && !activeResultSet.isClosed()) {
                activeResultSet.close();
            }

            ResultSet resultSet = statement.executeQuery();
            activeResultSet = resultSet; // Store the new active ResultSet

            // Check if there are any search results
            if (resultSet.next()) {
                resultSet.beforeFirst(); // Move the cursor back to the beginning
                ObservableList<String> searchResults = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    String studentId = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String phoneNumber = resultSet.getString("phone_number");

                    String resultText = "Student ID: " + studentId + "\nName: " + name + "\nPhone Number: " + phoneNumber;
                    searchResults.add(resultText);
                }

                searchResultsListView.setItems(searchResults);
            } else {
                resultLabel.setText("No results found.");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addSelectedStudent() {
        String selectedResult = searchResultsListView.getSelectionModel().getSelectedItem();
        if (selectedResult != null) {
            String studentId = extractStudentId(selectedResult);
            addToAttendTable(studentId);
        } else {
            // No result is selected, display an error message or handle it as needed
        }
    }

    private String extractStudentId(String resultText) {
        // Extract the student ID from the result text
        // Assuming the student ID is in the format "Student ID: {studentId}"
        int startIndex = resultText.indexOf(":") + 1;
        int endIndex = resultText.indexOf("\n");
        return resultText.substring(startIndex, endIndex).trim();
    }

    private void addToTableView(String studentId) {
        // Retrieve the student details from the database based on the ID
        try {
            String course_id =  coursesLabel.getText().replace("Course ID: ","");
            String sec_id =  sectionsLabel.getText().replace("Section ID: ","");
            String lec_id =  lecturesLabel.getText().replace("Lecture ID: ","");
            Student student = new Student(studentId, course_id, sec_id , lec_id);
            student_table.getItems().add(student);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
    }

    private void addToAttendTable(String studentId) {
        try {
            String sql = "SELECT * FROM attend " +
                    "WHERE id = ? AND course_id = ? AND sec_id = ? AND lec_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(sql);
            selectStatement.setString(1, studentId);
            selectStatement.setString(2, coursesLabel.getText().replace("Course ID: ", ""));
            selectStatement.setString(3, sectionsLabel.getText().replace("Section ID: ", ""));
            selectStatement.setString(4, lecturesLabel.getText().replace("Lecture ID: ", ""));
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Student is already in the lecture, display an error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Enrollment Error");
                alert.setContentText("Student is already enrolled in the lecture.");
                alert.showAndWait();
            } else {
                // Student is not in the lecture, proceed with adding to the attend table
                String sql2 = "INSERT INTO attend (id, course_id, sec_id, lec_id, semester, year) " +
                        "SELECT ?, ?, ?, ?, l.semester, l.year " +
                        "FROM lecture l " +
                        "WHERE l.lec_id = ? AND l.course_id = ? AND l.sec_id = ?";
                PreparedStatement insertStatement = connection.prepareStatement(sql2);
                insertStatement.setString(1, studentId);
                insertStatement.setString(2, coursesLabel.getText().replace("Course ID: ", ""));
                insertStatement.setString(3, sectionsLabel.getText().replace("Section ID: ", ""));
                insertStatement.setString(4, lecturesLabel.getText().replace("Lecture ID: ", ""));
                insertStatement.setString(5, lecturesLabel.getText().replace("Lecture ID: ", ""));
                insertStatement.setString(6, coursesLabel.getText().replace("Course ID: ", ""));
                insertStatement.setString(7, sectionsLabel.getText().replace("Section ID: ", ""));
                insertStatement.executeUpdate();
                insertStatement.close();
                addToTableView(studentId);
                // Provide feedback to the user
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Enrollment Successful");
                alert.setContentText("Student added to the attend table.");
                alert.showAndWait();
            }

            selectStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
    }

    @FXML
    private void readAttendanceDataFromExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Attendance Data Excel File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                Workbook workbook = WorkbookFactory.create(selectedFile);
                Sheet sheet = workbook.getSheetAt(0);

                // Flag to skip the first row
                boolean isFirstRow = true;

                for (Row row : sheet) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue; // Skip the first row
                    }

                    // Assuming the data is in the format: Student ID | Course ID | Section ID | Lecture ID
                    String studentId = getStringCellValue(row.getCell(0));
                    String courseId = getStringCellValue(row.getCell(1));
                    String secId = getStringCellValue(row.getCell(2));
                    String lecId = getStringCellValue(row.getCell(3));

                    if (!checkCourseEnrollment(studentId, courseId)) {
                        // Student does not take the course, display an error message
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Enrollment Error");
                        alert.setContentText("Student " + studentId + " does not take the course " + courseId);
                        alert.showAndWait();
                    } else if (isStudentAlreadyAttended(studentId, lecId)) {
                        // Student already marked as attended for the lecture, display an error message
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Attendance Error");
                        alert.setContentText("Student " + studentId + " has already attended the lecture " + lecId);
                        alert.showAndWait();
                    } else {
                        addToTableView(studentId, courseId, secId, lecId);
                        addToAttendTable(studentId, courseId, secId, lecId);
                    }
                }

                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle any potential exceptions here
            }
        }
    }

    private boolean isStudentAlreadyAttended(String studentId, String lectureId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Initialize the connection object
            Connection connection = App.con;

            String sql = "SELECT * FROM attend WHERE id = ? AND lec_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, studentId);
            statement.setString(2, lectureId);

            resultSet = statement.executeQuery();

            boolean isAttended = resultSet.next();

            return isAttended;
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        } finally {
            // Close the resources in the reverse order of their creation
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return "";
        }
    }
    private boolean checkCourseEnrollment(String studentId, String courseId) {
        try {
            String sql = "SELECT * FROM takes " +
                    "WHERE id = ? AND course_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, studentId);
            statement.setString(2, courseId);
            ResultSet resultSet = statement.executeQuery();

            boolean enrolled = resultSet.next();

            statement.close();
            resultSet.close();

            return enrolled;
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }

        return false;
    }

    private void addToTableView(String studentId, String courseId, String secId, String lecId) {
        // Retrieve the student and course details from the database based on the IDs
        try {
            // Perform necessary database queries to retrieve student and course details
            // Assuming you have appropriate database tables and methods to retrieve the data
            Student student = new Student(studentId, courseId, secId, lecId);
            student_table.getItems().add(student);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
    }

    private void addToAttendTable(String studentId, String courseId, String secId, String lecId) {
        try {
            String sql = "INSERT INTO attend (id, course_id, sec_id, lec_id, semester, year) " +
                    "SELECT ?, ?, ?, ?, l.semester, l.year " +
                    "FROM lecture l " +
                    "WHERE l.lec_id = ? AND l.course_id = ? AND l.sec_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, studentId);
            statement.setString(2, courseId);
            statement.setString(3, secId);
            statement.setString(4, lecId);
            statement.setString(5, lecId);
            statement.setString(6, courseId);
            statement.setString(7, secId);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
    }


}

class Student {
    private StringProperty studentId;
    private StringProperty courseId;
    private StringProperty sectionId;
    private StringProperty lectureId;

    public Student(String studentId, String courseId, String sectionId, String lectureId) {
        this.studentId = new SimpleStringProperty(studentId);
        this.courseId = new SimpleStringProperty(courseId);
        this.sectionId = new SimpleStringProperty(sectionId);
        this.lectureId = new SimpleStringProperty(lectureId);
    }

    public StringProperty studentIdProperty() {
        return studentId;
    }

    public StringProperty courseIdProperty() {
        return courseId;
    }

    public StringProperty sectionIdProperty() {
        return sectionId;
    }

    public StringProperty lectureIdProperty() {
        return lectureId;
    }

}
