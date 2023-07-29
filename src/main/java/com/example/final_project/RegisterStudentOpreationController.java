package com.example.final_project;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class RegisterStudentOpreationController implements Initializable {

    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> course;
    @FXML
    ComboBox<String> section;
    @FXML
    ComboBox<String> student;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
        course.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onCourseSelection();
        });
    }

    @FXML
    private void switchToOpening() throws IOException {
        if (App.checkUser().equals("tutor")){
            App.setRoot("tutoropreation");

        }else{
            App.setRoot("adminopreatins");
        }
    }

    @FXML
    private void setCombInitials() {
        course.setValue("Select course");
        section.setValue("Select section");
        student.setValue("Select student");
    }

    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> courses = getcourses();
        ArrayList<String> students = getStudents();

        if (courses != null && students != null) {
            ObservableList<String> observablecourses = FXCollections.observableList(courses);
            course.setItems(observablecourses);

            ObservableList<String> observablestudents = FXCollections.observableList(students);
            student.setItems(observablestudents);
        } else {
            addLabel.setText("Failed to retrieve Courses/Semesters/Instructors from the database.");
        }
    }

    public ArrayList<String> getcourses() {
        String sql = "SELECT subject FROM course;";
        ArrayList<String> courses = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                courses.add(rs.getString("subject"));
            }
            return courses;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getSections(String courseName) {
        String SQL = "SELECT sec_id FROM section WHERE course_id = (SELECT course_id FROM course WHERE subject = ?)";
        ArrayList<String> sections = new ArrayList<>();
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, courseName);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                sections.add(resultSet.getString("sec_id"));
            }
            return sections;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getYear(String sectionValue) {
        String SQL = "SELECT year FROM section WHERE sec_id = ? and course_id = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, sectionValue);
            pstmt.setString(2, getCourseID());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("year");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSemester(String sectionValue) {
        String SQL = "SELECT semester FROM section WHERE sec_id = ? and course_id = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, sectionValue);
            pstmt.setString(2, getCourseID());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("semester");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getStudents() {
        String sql = "SELECT id FROM student";
        ArrayList<String> students = new ArrayList<>();
        try (Statement stmt = App.con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(rs.getString("id"));
            }
            return students;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private String getCourseID() {
        String SQL = "SELECT course_id FROM course WHERE subject = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, course.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("course_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void registerStudent() {
        String studentValue = student.getValue();
        String courseValue = course.getValue();
        String sectionValue = section.getValue();
        int year = Integer.parseInt(getYear(sectionValue));

        if (studentValue == null || courseValue == null || sectionValue == null) {
            addLabel.setText("Please fill in all fields.");
        } else {
            String SQL = "INSERT INTO takes (id, course_id, sec_id, semester, year) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, studentValue);
                pstmt.setString(2, getCourseID());
                pstmt.setString(3, sectionValue);
                pstmt.setString(4, getSemester(sectionValue));
                pstmt.setInt(5, year);

                int affectedRows = pstmt.executeUpdate();

                // Check the affected rows
                if (affectedRows > 0) {
                    // Registration successful
                    addLabel.setText("Student is registered successfully :)");
                    setCombInitials();
                    showSuccessAlert("Registration Successful", "Student registered successfully.");
                } else {
                    // Registration failed
                    addLabel.setText("Invalid Input");
                    showErrorAlert("Registration Failed", "Invalid input. Please check your registration details.");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                showErrorAlert("Registration Failed", "An error occurred during the registration process.");
            }
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void onCourseSelection() {
        String selectedCourse = course.getValue();
        if (selectedCourse != null) {
            ArrayList<String> sections = getSections(selectedCourse);
            if (sections != null) {
                ObservableList<String> observableSections = FXCollections.observableList(sections);
                section.setItems(observableSections);
            } else {
                addLabel.setText("Failed to retrieve sections for the selected course.");
            }
        } else {
            section.getItems().clear();
        }
    }
}