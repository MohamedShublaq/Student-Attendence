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

public class EditInstructorOpreationController implements Initializable {
    @FXML
    TextField salary;
    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> department;
    @FXML
    ComboBox<String> id;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
    }
    @FXML
    private void switchToOpening() throws IOException  {
        App.setRoot("instructoropreations");
        id.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onIDSelection();});
    }
    private void onIDSelection() {
        String selectedCourse = getID();
        if (selectedCourse != null) {
            ArrayList<String> departments = getDepartments(selectedCourse);
            if (departments != null) {
                ObservableList<String> observableSections = FXCollections.observableList(departments);
                department.setItems(observableSections);
                salary.setText(getSalary(selectedCourse)+"");
            } else {
                addLabel.setText("Failed to retrieve sections for the selected course.");
            }
        } else {
            department.getItems().clear();
        }
    }
    @FXML
    private ArrayList<String> getDepartments(String name){
        String SQL = "SELECT dept_name FROM instructor WHERE id =?"
                ;
        ArrayList<String> ids = new ArrayList<>();
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, name);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                ids.add(resultSet.getString(1));
            }
            return ids;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
    @FXML
    private int getSalary(String name){
        String SQL = "SELECT salary FROM instructor WHERE id =?"
                ;
        int ids = 0;
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, name);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                ids=(resultSet.getInt(1));
            }
            return ids;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }
    @FXML
    private void setCombInitials() {
        department.setValue("Select department");
        id.setValue("Select id");
    }
    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> departments = getDepartments();
        ArrayList<String> ids = getName();
        if (departments != null && ids!=null) {
            ObservableList<String> observableDepartments = FXCollections.observableList(departments);
            department.setItems(observableDepartments);
            ObservableList<String> observableIDS = FXCollections.observableList(ids);
            id.setItems(observableIDS);
        } else {

            addLabel.setText("Failed to retrieve departments/ids from the database.");
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
    public ArrayList<String> getName() {
        String sql = "select name from instructor;";
        ArrayList<String> ids = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
            return ids;
        }catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @FXML
    private void updateTutor() {
        String salaryValue = salary.getText();
        String departmentValue = department.getValue();
        String idValue = getID();

        // Validate input fields
        if (salaryValue.isEmpty() || departmentValue == null || departmentValue.equals("Select department") ||
                idValue == null || idValue.equals("Select id")) {
            addLabel.setText("Please fill in all fields.");
        } else {
            try {
                int salaryInt = Integer.parseInt(salaryValue);
                if (salaryInt <= 5000) {
                    addLabel.setText("Salary should be above 5000.");
                } else {
                    String SQL = "UPDATE instructor "
                            + "SET salary = ?, dept_name = ? "
                            + "WHERE id = ?";
                    try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                        pstmt.setInt(1, salaryInt);
                        pstmt.setString(2, departmentValue);
                        pstmt.setString(3, idValue);

                        int affectedRows = pstmt.executeUpdate();
                        // check the affected rows
                        if (affectedRows > 0) {
                            addLabel.setText("Instructor is updated successfully :)");
                            salary.setText("");
                            setCombInitials();
                        } else {
                            addLabel.setText("Invalid Input: Update operation failed.");
                        }
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                        addLabel.setText("Invalid Input: Database error occurred.");
                    }
                }
            } catch (NumberFormatException e) {
                addLabel.setText("Invalid salary value. Please enter a number.");
            }
        }
    }


    private String getID() {
        String SQL = "Select id from instructor where name= ?";
        String id1 = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, id.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                id1 = resultSet.getString(1);
            }
            return id1;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    @FXML
    private void deleteTutor() {
        String idValue = getID();
        if (idValue == null || idValue.equals("Select id")) {
            addLabel.setText("Please select a instructor to delete.");
            return;
        }

        String SQL = "DELETE FROM instructor WHERE id = ?";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, getID());

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                addLabel.setText("Tutor is deleted successfully :)");
                setComboBoxes();
                salary.setText("");
                setCombInitials();
            } else {
                addLabel.setText("Invalid Input: No Instructor found with the provided ID.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            addLabel.setText("Invalid Input: Database error occurred.");
        }
    }

}
