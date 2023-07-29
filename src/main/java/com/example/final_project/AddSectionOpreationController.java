package com.example.final_project;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class AddSectionOpreationController implements Initializable {

    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> course;
    @FXML
    ComboBox<String> instructor;
    @FXML
    ComboBox<String> semester;
    @FXML
    TextField room_number;
    @FXML
    TextField building;
    @FXML
    DatePicker year;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            setComboBoxes();
    }
    @FXML
    private void switchToOpening() throws IOException  {
        App.setRoot("courseopreation");
    }
    @FXML
    private void setCombInitials() {
        course.setValue("Select course");
        instructor.setValue("Select instructor");
        semester.setValue("Select semester");
    }
    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> courses = getcourses();
        ArrayList<String> semesters = getSemesters();
        ArrayList<String> instructors = getInstructors();

        if (courses != null &&semesters != null && instructors!=null ) {
            ObservableList<String> observablecourses = FXCollections.observableList(courses);
            course.setItems(observablecourses);
            ObservableList<String> observableinstructors = FXCollections.observableList(instructors);
            instructor.setItems(observableinstructors);
            ObservableList<String> observableSemesters = FXCollections.observableList(semesters);
            semester.setItems(observableSemesters);
        } else {
            addLabel.setText("Failed to retrieve Courses/Semesters/Instructors from the database.");
        }
    }
    public ArrayList<String> getcourses() {
        String sql = "select subject from course;";
        ArrayList<String> courses = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                courses.add(rs.getString(1));
            }
            return courses;
        }catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public ArrayList<String> getInstructors() {
        String sql = "select name from instructor;";
        ArrayList<String> instructors = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                instructors.add(rs.getString(1));
            }
            return instructors;
        }catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public ArrayList<String> getSemesters() {
        ArrayList<String> semsters = new ArrayList<>();
        semsters.add("Fall");
        semsters.add("Spring");
        semsters.add("Summer");
        semsters.add("Winter");
        return  semsters;
    }

    private String maxSectionID() {
        String sql = "SELECT MAX(sec_id) FROM section ;";
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
    private String getCourseID() {
        String SQL = "Select course_id from course where subject = ?";
        String id = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1,  course.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString(1);
                // Do something with the retrieved string ID
            }
            return id;

        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getInstructorID() {
        String SQL = "SELECT id FROM instructor WHERE name = ?";
        String id = null;
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, instructor.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @FXML
    private void insertSection() {
        String courseValue = course.getValue();
        String semesterValue = semester.getValue();
        String id = getInstructorID();
        if (semesterValue.length() > 6) {
            semesterValue = semesterValue.substring(0, 6);
        }
        LocalDate selectedDate = year.getValue();
        String yearValue = (selectedDate != null) ? String.valueOf(selectedDate.getYear()) : "";

        // Validate input fields
        if (semesterValue == null || yearValue.isEmpty() || courseValue == null||id==null
                || building.getText().isEmpty() || room_number.getText().isEmpty()) {
            addLabel.setText("Please fill in all fields.");
        } else {

            String SQL = "INSERT INTO section(course_id, sec_id, semester, year, building, room_number) "
                    + "VALUES(?, ?, ?, ?, ?, ?)";
            String addID = String.valueOf(Integer.parseInt(maxSectionID())+1);
            String course_id = getCourseID();

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, course_id);
                pstmt.setString(2, String.valueOf(addID));
                pstmt.setString(3, semester.getValue().toString());
                pstmt.setInt(4,year.getValue().getYear());
                pstmt.setString(5, building.getText());
                pstmt.setString(6, room_number.getText());
                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {
                    if (insertTeaches()){
                    addLabel.setText("Section is added successfully :)\n ID is " + addID);
                    building.setText("");
                    room_number.setText("");
                    setCombInitials();}
                } else {
                    addLabel.setText("Invalid Input");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private boolean insertTeaches() {
        boolean x =false;
        String courseValue = course.getValue()+"";
        String semesterValue = semester.getValue()+"";
        String id = getInstructorID()+"";
        if (semesterValue.length() > 6) {
            semesterValue = semesterValue.substring(0, 6);
        }
        LocalDate selectedDate = year.getValue();
        String yearValue = (selectedDate != null) ? String.valueOf(selectedDate.getYear()) : "";
        // Validate input fields
        if (semesterValue==null ||id==null || yearValue==null || courseValue==null|| building.getText().isEmpty()|| room_number.getText().isEmpty()) {
            addLabel.setText("Please fill in all fields.");
        } else {

            String SQL = "INSERT INTO teaches(course_id, sec_id, semester, year,id) "
                    + "VALUES(?, ?, ?, ?, ?)";
            String addID = String.valueOf(Integer.parseInt(maxSectionID()));
            String course_id = getCourseID();

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, course_id);
                pstmt.setString(2, String.valueOf(addID));
                pstmt.setString(3, semester.getValue().toString());
                pstmt.setInt(4,year.getValue().getYear());
                pstmt.setString(5, id);
                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {;
                    x= true;
                } else {
                    addLabel.setText("Invalid Input");
                }

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

        }
        return x;
    }

}
