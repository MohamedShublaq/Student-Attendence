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

public class AddCourseOpreationController implements Initializable {
    @FXML
    TextField subject;
    @FXML
    TextField book;
    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> department ;
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
    }
    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> departments = getDepartments();
        if (departments != null) {
            ObservableList<String> observableDepartments = FXCollections.observableList(departments);
            department.setItems(observableDepartments);
        } else {
            addLabel.setText("Failed to retrieve departments from the database.");
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
    private String maxCourseID() {
        String sql = "SELECT MAX(course_id) FROM course ;";
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

    @FXML
    private void insertCourse() {
        String subjectValue = subject.getText();
        String bookValue = book.getText();
        String departmentValue = department.getValue();

        // Validate input fields
        if (subjectValue.isEmpty() || bookValue.isEmpty()) {
            addLabel.setText("Please fill in all fields.");
        } else if (departmentValue == null || departmentValue.equals("Select department")) {
            addLabel.setText("Please select a department.");
        } else {
            String SQL = "INSERT INTO course(course_id, subject, book, dept_name) " +
                    "VALUES (?, ?, ?, ?)";
            String addID = String.valueOf(Integer.parseInt(maxCourseID()) + 1);

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, String.valueOf(addID));
                pstmt.setString(2, subject.getText());
                pstmt.setString(3, book.getText());
                pstmt.setString(4, departmentValue);

                int affectedRows = pstmt.executeUpdate();
                // check the affected rows
                if (affectedRows > 0) {
                    addLabel.setText("Course is added successfully :)\n ID is " + addID);
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


}
