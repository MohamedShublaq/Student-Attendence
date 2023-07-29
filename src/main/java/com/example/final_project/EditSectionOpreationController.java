
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class EditSectionOpreationController implements Initializable {

    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> course;
    @FXML
    ComboBox<String> instructor;

    @FXML
    ComboBox<String> section;
    @FXML
    TextField room_number;
    @FXML
    TextField building;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
        course.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onCourseSelection();
        });
    }

    @FXML
    private void switchToOpening() throws IOException {
        App.setRoot("courseopreation");
    }

    @FXML
    private void setCombInitials() {
        course.setValue("Select course");
        instructor.setValue("Select instructor");
        section.setValue("Select section");

    }

    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> courses = getcourses();
        ArrayList<String> instructors = getInstructors();

        if (courses != null && instructors != null) {
            ObservableList<String> observablecourses = FXCollections.observableList(courses);
            course.setItems(observablecourses);
            ObservableList<String> observableinstructors = FXCollections.observableList(instructors);
            instructor.setItems(observableinstructors);
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
                sections.add(resultSet.getString(1));
            }
            return sections;
        } catch (SQLException e) {
            e.printStackTrace();
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
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getCourseID() {
        String SQL = "Select course_id from course where subject = ?";
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

    private String getInstructorID() {
        String SQL = "Select id from instructor where name = ?";
        String name = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, instructor.getValue());
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

    @FXML
    private void updateSection() {
        String courseValue = course.getValue();
        // Validate input fields
        if ( courseValue == null || building == null || room_number == null) {
            addLabel.setText("Please fill in all fields.");
        } else {

            String SQL = "UPDATE section SET  building = ?, room_number = ? "
                    + "WHERE course_id = ? AND sec_id = ?";

            String course_id = getCourseID();

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(3, course_id);
                pstmt.setString(4, section.getValue());
                pstmt.setString(1, building.getText());
                pstmt.setString(2, room_number.getText());
                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {
                    addLabel.setText("Section is Updated successfully :)");
                    updateTeaches();
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
    private void updateTeaches() {
        String courseValue = course.getValue();
        // Validate input fields
        if (courseValue == null || building == null || room_number == null) {
            addLabel.setText("Please fill in all fields.");
        } else {

            String SQL = "UPDATE teaches SET id = ? WHERE course_id = ? AND sec_id = ?";
            String course_id = getCourseID();

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(2, course_id);
                pstmt.setString(3, section.getValue());
                pstmt.setString(1, getInstructorID());
                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {
                    ;
                    System.out.println("Done");
                } else {
                    addLabel.setText("Invalid Input");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void DeleteSection() {
        String SQL = "DELETE FROM section "
                + "WHERE course_id = ? and sec_id = ? ";

        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, getCourseID());
            pstmt.setString(2, section.getValue());
            deleteTeaches();
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                addLabel.setText("Section is Deleted successfully :)");
                deleteTeaches();
                setComboBoxes();
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

    private void deleteTeaches() {
        String SQL = "DELETE FROM teaches "
                + "WHERE course_id = ? and sec_id = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, getCourseID());
            pstmt.setString(2, section.getValue());
            int affectedRows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}