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

public class AssignTutorOpreationController implements Initializable {

    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> course;
    @FXML
    ComboBox<String> section;
    @FXML
    ComboBox<String> tutor;
    @FXML
    ComboBox<String> lecture;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
        course.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onCourseSelection();
        });
        section.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onSectionSelection();
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

        tutor.setValue("Select tutor");

        lecture.setValue("Select lecture");
    }


    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> courses = getcourses();
        ArrayList<String> tutors = getStudents();

        if (courses != null && tutors != null) {
            ObservableList<String> observablecourses = FXCollections.observableList(courses);
            course.setItems(observablecourses);

            ObservableList<String> observabletutors = FXCollections.observableList(tutors);
            tutor.setItems(observabletutors);

        } else {
            addLabel.setText("Failed to retrieve Courses/Semesters/Tutors from the database.");
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
    public ArrayList<String> getLectures(String courseName,String sectionId) {
        String SQL = "SELECT lec_id FROM lecture WHERE course_id = (SELECT course_id FROM course WHERE subject = ?) and sec_id = ?";
        ArrayList<String> lectures = new ArrayList<>();
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, courseName);
            pstmt.setString(2, sectionId);

            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                lectures.add(resultSet.getString("lec_id"));
            }
            return lectures;
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
        String sql = "SELECT id FROM teacherassistant";
        ArrayList<String> tutors = new ArrayList<>();
        try (Statement stmt = App.con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tutors.add(rs.getString("id"));
            }
            return tutors;
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
    private void assignTutor() {
        String tutorValue = tutor.getValue();
        String courseValue = course.getValue();
        String sectionValue = section.getValue();
        String lectureValue = lecture.getValue();
        int year = Integer.parseInt(getYear(sectionValue));

        if (tutorValue == null || courseValue == null || sectionValue == null) {
            addLabel.setText("Please fill in all fields.");
        } else {
            String SQL = "INSERT INTO records (id, course_id, sec_id, semester, year,lec_id) VALUES (?, ?, ?, ?, ?,?)";

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, tutorValue);
                pstmt.setString(2, getCourseID());
                pstmt.setString(3, sectionValue);
                pstmt.setString(4, getSemester(sectionValue));
                pstmt.setInt(5, year);
                pstmt.setString(6,lectureValue );


                int affectedRows = pstmt.executeUpdate();

                // Check the affected rows
                if (affectedRows > 0) {
                    // Registration successful
                    addLabel.setText("Tutor is assigned successfully :)");
                    setCombInitials();
                    showSuccessAlert("Assingment Successful", "Course Assigned successfully.");
                } else {
                    // Registration failed
                    addLabel.setText("Invalid Input");
                    showErrorAlert("Assingment Failed", "Invalid input. Please check your Assignment details.");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                showErrorAlert("Assingment Failed", "An error occurred during the Assingment process.");
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
    private void onSectionSelection() {
        String selectedCourseName = course.getValue();
        String selectedCourse = section.getValue();
        if (selectedCourse != null) {
            ArrayList<String> lectures = getLectures(selectedCourseName,selectedCourse);
            if (lectures != null) {
                ObservableList<String> observableLectures = FXCollections.observableList(lectures);
                lecture.setItems(observableLectures);
            } else {
                addLabel.setText("Failed to retrieve Lectures for the selected course.");
            }
        } else {
            section.getItems().clear();
        }
    }
}