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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditCourseOpreationController implements Initializable {
    @FXML
    TextField subject;
    @FXML
    TextField book;
    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> department;

    @FXML
    ComboBox<String> course;

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
        department.setValue("Select department");
        course.setValue("Select course");
    }
    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> departments = getDepartments();
        ArrayList<String> courses = getCourses();
        if (departments != null && courses!=null) {
            ObservableList<String> observableDepartments = FXCollections.observableList(departments);
            department.setItems(observableDepartments);
            ObservableList<String> observableCourses = FXCollections.observableList(courses);
            course.setItems(observableCourses);
        } else {

            addLabel.setText("Failed to retrieve departments/courses from the database.");
        }
    }
    public ArrayList<String> getDepartments() {
        String sql = "select dept_name from department;";
        ArrayList<String> departments = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                departments.add(rs.getString(1));
            }
            return departments;
        }catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public ArrayList<String> getCourses() {
        String sql = "select course_id from course;";
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

    @FXML
    private void updateCourse() {
        String subjectValue = subject.getText();
        String bookValue = book.getText();
        String departmentValue = department.getValue();
        String courseValue = course.getValue();

        // Validate input fields
        if (subjectValue.isEmpty() || bookValue.isEmpty() || departmentValue == null || courseValue == null) {
            addLabel.setText("Please fill in all fields.");
        }else {
            String SQL = "UPDATE course "
                    + "SET subject = ?, book = ?, dept_name = ? "
                    + "WHERE course_id = ?";
            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, subjectValue);
                pstmt.setString(2, bookValue);
                pstmt.setString(3, departmentValue);
                pstmt.setString(4, courseValue);

                int affectedRows = pstmt.executeUpdate();
                // check the affected rows
                if (affectedRows > 0) {
                    addLabel.setText("Course is updated successfully :)\n");
                    subject.setText("");
                    book.setText("");
                    setCombInitials();
                } else {
                    addLabel.setText("Invalid Input");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void DeleteCourse(){
        String SQL = "DELETE FROM course "
                + "WHERE course_id = ?";

        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, course.getValue().toString());

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                addLabel.setText("Course is Deleted successfully :)");
                setComboBoxes();
                subject.setText("");
                book.setText("");
                setCombInitials();
            }else{
                addLabel.setText("Invalid Input");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
