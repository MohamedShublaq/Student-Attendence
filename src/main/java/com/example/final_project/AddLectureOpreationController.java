package com.example.final_project;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class AddLectureOpreationController implements Initializable {

    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> course;
    @FXML
    ComboBox<String> section;
    @FXML
    ComboBox<String> tutor;
    @FXML
    TextField room_number;
    @FXML
    TextField building;
    @FXML
    TextField title;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
    }

    @FXML
    private void switchToOpening() throws IOException {
            App.setRoot("lectureopreation");
    }

    @FXML
    private void setCombInitials() {
        course.setValue("Select course");
        section.setValue("Select section");
        tutor.setValue("Select tutor");
    }

    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> courses = getcourses();
        ArrayList<String> semesters = getSemesters();
        ArrayList<String> tutors = getTutors();
        if (courses != null && semesters != null && tutors != null) {
            ObservableList<String> observableCourses = FXCollections.observableList(courses);
            course.setItems(observableCourses);
            ObservableList<String> observableTutors = FXCollections.observableList(tutors);
            tutor.setItems(observableTutors);
        } else {
            addLabel.setText("Failed to retrieve Courses/Semesters/Tutors from the database.");
        }
    }

    @FXML
    private void updateSections(ActionEvent event) {
        String selectedCourse = course.getValue();
        if (selectedCourse != null && !selectedCourse.isEmpty()) {
            ArrayList<String> sections = getSections(selectedCourse);
            if (sections != null) {
                ObservableList<String> observableSections = FXCollections.observableList(sections);
                section.setItems(observableSections);
            } else {
                addLabel.setText("Failed to retrieve sections for the selected course.");
            }
        } else {
            // Clear sections if no course is selected
            section.setItems(null);
        }
    }

    public ArrayList<String> getcourses() {
        String sql = "SELECT subject FROM course WHERE course_id IN (SELECT DISTINCT course_id FROM section)";
        ArrayList<String> courses = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                courses.add(rs.getString(1));
            }
            return courses;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getTutors() {
        String sql = "SELECT name FROM teacherassistant;";
        ArrayList<String> tutors = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                tutors.add(rs.getString(1));
            }
            return tutors;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getSections(String courseName) {
        String sql = "SELECT sec_id FROM section WHERE course_id = (SELECT course_id FROM course WHERE subject = ?);";
        ArrayList<String> sections = new ArrayList<>();
        try (PreparedStatement pstmt = App.con.prepareStatement(sql)) {
            pstmt.setString(1, courseName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sections.add(rs.getString(1));
            }
            return sections;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getSemesters() {
        ArrayList<String> semesters = new ArrayList<>();
        semesters.add("Fall");
        semesters.add("Spring");
        semesters.add("Summer");
        semesters.add("Winter");
        return semesters;
    }

    @FXML
    private void insertLecture() {
        String courseValue = course.getValue();

        // Validate input fields
        if (courseValue == null || building == null || room_number == null) {
            addLabel.setText("Please fill in all fields.");
        } else {
            String SQL = "INSERT INTO lecture(course_id, sec_id, semester, year, building, room_number, title,lec_id) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?,?)";
            String addID = String.valueOf(Integer.parseInt(maxLectureID()) + 1);
            String course_id = getCourseID();
            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {

                pstmt.setString(1, course_id);
                pstmt.setString(2,section.getValue().toString() );
                pstmt.setString(3, getSemester());
                pstmt.setInt(4, getYear());
                pstmt.setString(5, building.getText());
                pstmt.setString(6, room_number.getText());
                pstmt.setString(7, title.getText());
                pstmt.setString(8, addID);

                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {
                    addLabel.setText("Lecture is added successfully :)\n ID is " + addID);

                    insertRecord();
                    building.setText("");
                    room_number.setText("");
                    setCombInitials();
                } else {
                    addLabel.setText("Invalid Input");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private String getCourseID() {
        String SQL = "SELECT course_id FROM course WHERE subject = ?";
        String id = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, course.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString(1);
                // Do something with the retrieved string ID
            }
            return id;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String maxLectureID() {
        String sql = "SELECT MAX(lec_id) FROM lecture;";
        String max = "0";
        try (Statement stmt = App.con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            max = rs.getString(1);
            if (max == null) {
                max = "0";
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return max;
    }

    private String getTutorsID() {
        String SQL = "SELECT id FROM teacherassistant WHERE name = ?";
        String name = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, tutor.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString(1);
            }
            return name;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getSemester() {
        String SQL = "SELECT semester FROM section WHERE sec_id = ?";
        String name = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, section.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString(1);
            }
            return name;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private int getYear() {
        String SQL = "SELECT year FROM section WHERE sec_id = ?";
        int year =0;
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, section.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                year = resultSet.getInt(1);
            }
            return year;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void insertRecord() {
        String courseValue = course.getValue();
        String semesterValue = getSemester();
        if (semesterValue.length() > 6) {
            semesterValue = semesterValue.substring(0, 6);
        }
        String yearValue = getYear() + "";
        // Validate input fields
        if (semesterValue.isEmpty() || yearValue.isEmpty() || courseValue == null || building == null || room_number == null) {
            addLabel.setText("Please fill in all fields.");
        } else {
            String SQL = "INSERT INTO records(course_id, sec_id, semester, year,id,lec_id) "
                    + "VALUES(?, ?, ?, ?, ?,?)";
            String addID = String.valueOf(Integer.parseInt(maxLectureID()));
            String course_id = getCourseID();

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, course_id);
                pstmt.setString(2, section.getValue());
                pstmt.setString(3, getSemester());
                pstmt.setInt(4, getYear());
                pstmt.setString(5, getTutorsID());
                pstmt.setString(6, String.valueOf(addID));

                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {
                } else {
                    addLabel.setText("Invalid Input");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
