package com.example.final_project;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReportStudentOpreationsConrtroller implements Initializable {
    @FXML
    private TableView<String[]> studentLectureTableView;

    @FXML
    private TableColumn<String[], String> lectureIdColumn;

    @FXML
    private TableColumn<String[], String> lectureNameColumn;

    @FXML
    private TableColumn<String[], String> attendanceColumn;

    @FXML
    Label addLabel;

    @FXML
    ComboBox<String> course;

    @FXML
    ComboBox<String> student;

    private ObservableList<String[]> attendanceData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
    }

    @FXML
    private void switchToOpening() throws IOException {
        App.setRoot("tutoropreation");
    }

    @FXML
    private void setCombInitials() {
        course.setValue("Select Course");
        student.setValue("Select Student");
    }

    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> students = getStudents();

        student.setOnAction(event -> {
            if (!student.getSelectionModel().isEmpty()) {
                ArrayList<String> courses = getCourses();
                if (courses != null) {
                    ObservableList<String> observableCourses = FXCollections.observableList(courses);
                    course.setItems(observableCourses);
                } else {
                    addLabel.setText("Failed to retrieve courses for the student.");
                }
            }
        });

        course.setOnAction(event -> {
            if (!course.getSelectionModel().isEmpty() && !student.getSelectionModel().isEmpty()) {
                String courseId = course.getValue();
                String studentId = student.getValue();
                attendanceData = getAttendanceData(courseId, studentId);
                populateTableView();
            }
        });

        if (students != null) {
            ObservableList<String> observableStudents = FXCollections.observableList(students);
            student.setItems(observableStudents);
        } else {
            addLabel.setText("Failed to retrieve students from the database.");
        }
    }

    public ArrayList<String> getStudents() {
        String sql = "SELECT id FROM takes;";
        ArrayList<String> students = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                students.add(rs.getString(1));
            }
            return students;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getCourses() {
        String SQL = "SELECT DISTINCT course_id FROM takes WHERE id = ?";
        ArrayList<String> courses = new ArrayList<>();
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, student.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                courses.add(resultSet.getString("course_id"));
            }
            return courses;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ObservableList<String[]> getAttendanceData(String courseId, String studentId) {
        ObservableList<String[]> attendanceData = FXCollections.observableArrayList();
        String sql = "SELECT l.lec_id, l.title, CASE WHEN a.lec_id IS NOT NULL THEN 'Attended' ELSE 'Not Attended' END AS attendance " +
                "FROM lecture l LEFT JOIN attend a ON a.lec_id = l.lec_id AND a.course_id = ? AND a.id = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            pstmt.setString(2, studentId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String lectureId = resultSet.getString("lec_id");
                String lectureName = resultSet.getString("title");
                String attendance = resultSet.getString("attendance");
                String[] attendanceRecord = { lectureId, lectureName, attendance };
                attendanceData.add(attendanceRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceData;
    }

    private void populateTableView() {
        lectureIdColumn.setCellValueFactory(param -> {
            String[] attendanceRecord = param.getValue();
            return new SimpleStringProperty(attendanceRecord[0]);
        });

        lectureNameColumn.setCellValueFactory(param -> {
            String[] attendanceRecord = param.getValue();
            return new SimpleStringProperty(attendanceRecord[1]);
        });

        attendanceColumn.setCellValueFactory(param -> {
            String[] attendanceRecord = param.getValue();
            return new SimpleStringProperty(attendanceRecord[2]);
        });

        studentLectureTableView.setItems(attendanceData);
    }
}
