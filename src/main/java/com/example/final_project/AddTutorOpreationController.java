package com.example.final_project;

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

public class AddTutorOpreationController implements Initializable {
    @FXML
    TextField name;
    @FXML
    TextField salary;
    @FXML
    PasswordField password;
    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> department ;
    @FXML
    ComboBox<String> gender ;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
    }
    @FXML
    private void switchToOpening() throws IOException  {
        App.setRoot("tutoropreations");
    }
    @FXML
    private void setCombInitials() {
        department.setValue("Select department");
        gender.setValue("Select gender");
    }
    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> departments = getDepartments();
        ArrayList<String> genders = getGenders();

        if (departments != null&&genders!=null) {
            ObservableList<String> observableDepartments = FXCollections.observableList(departments);
            department.setItems(observableDepartments);
            ObservableList<String> observableGenders = FXCollections.observableList(genders);
            gender.setItems(observableGenders);
        } else {
            addLabel.setText("Failed to retrieve departments/genders from the database.");
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
    public ArrayList<String> getGenders() {

        ArrayList<String> genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");
            return genders;

    }
    private String maxTutorID() {
        String sql = "SELECT MAX(id) FROM teacherassistant ;";
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
    private void insertTutor() {
        String nameValue = name.getText();
        String passwordValue = password.getText();
        String salaryValue = salary.getText();
        String departmentValue = department.getValue();
        String genderValue = gender.getValue();

        // Validate input fields
        if (salaryValue.isEmpty() || passwordValue.isEmpty() || nameValue.isEmpty() ||
                departmentValue == null || departmentValue.equals("Select department") ||
                genderValue == null || genderValue.equals("Select gender")) {
            addLabel.setText("Please fill in all fields.");
        } else {
            try {
                int salaryInt = Integer.parseInt(salaryValue);
                if (salaryInt <= 5000) {
                    addLabel.setText("Salary should be above 5000.");
                } else {
                    String SQL = "INSERT INTO teacherassistant(id, name, salary, gender, password, dept_name) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    String addID = String.valueOf(Integer.parseInt(maxTutorID()) + 1);
                    passwordValue =new String(App.encryptPassword(passwordValue));
                    try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                        if(addID.equals("1")){
                            pstmt.setString(1, "100"+addID);

                        }else {
                            pstmt.setString(1, addID);
                        }
                        pstmt.setString(2, nameValue);
                        pstmt.setInt(3, salaryInt);
                        pstmt.setString(4, genderValue);
                        pstmt.setString(5, passwordValue );
                        pstmt.setString(6, departmentValue);

                        int affectedRows = pstmt.executeUpdate();
                        // check the affected rows
                        if (affectedRows > 0) {
                            addLabel.setText("Tutor is added successfully :)\n ID is " + addID);
                            name.setText("");
                            password.setText("");
                            salary.setText("");
                            setCombInitials();
                        } else {
                            addLabel.setText("Invalid Input");
                        }
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            } catch (NumberFormatException e) {
                addLabel.setText("Invalid salary value. Please enter a number.");
            }
        }
    }



}
