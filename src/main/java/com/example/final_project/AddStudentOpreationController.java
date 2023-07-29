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

public class AddStudentOpreationController implements Initializable {
    @FXML
    TextField name;
    @FXML
    TextField city;
    @FXML
    TextField phone_number;
    @FXML
    TextField street;
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
        App.setRoot("studentopreations");
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
    private String maxStudentID(String genderValue) {
        String sql = "SELECT MAX(id) FROM student WHERE gender = ?";
        String max = "0";
        try (PreparedStatement pstmt = App.con.prepareStatement(sql)) {
            pstmt.setString(1, genderValue);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                max = rs.getString(1);
                if (max == null) {
                    max = "0"; // Set a default value when the result is null
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return max;
    }

    @FXML
    private void insertStudent() {
        String nameValue = name.getText();
        String passwordValue = password.getText();
        String cityValue = city.getText();
        String streetValue = street.getText();
        String departmentValue = department.getValue();
        String genderValue = gender.getValue();

        // Validate input fields
        if (cityValue.isEmpty() ||streetValue.isEmpty()|| passwordValue.isEmpty() || nameValue.isEmpty() ||phone_number.getText().isEmpty()||
                departmentValue == null || departmentValue.equals("Select department") ||
                genderValue == null || genderValue.equals("Select gender")) {
            addLabel.setText("Please fill in all fields.");
        } else {
                String SQL = "INSERT INTO student(id, name, city, gender, password, dept_name,street) " +
                        "VALUES (?, ?, ?, ?, ?, ?,?)";
                String addID = String.valueOf(Integer.parseInt(maxStudentID(genderValue)) + 1);
                passwordValue =new String(App.encryptPassword(passwordValue));
                try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                    if (genderValue.equals("Male")){
                        if(addID.equals( "1") ){
                            addID = "12020"+addID;
                            pstmt.setString(1,addID);
                        }else{
                            pstmt.setString(1,addID);
                        }

                    }else{
                        if(addID.equals( "1") ){
                            addID = "22020"+addID;
                            pstmt.setString(1,addID);
                        }else{
                            pstmt.setString(1,addID);
                        }
                    }
                    pstmt.setString(2, nameValue);
                    pstmt.setString(3, cityValue);
                    pstmt.setString(4, genderValue);
                    pstmt.setString(5, passwordValue );
                    pstmt.setString(6, departmentValue);
                    pstmt.setString(7, streetValue);

                    int affectedRows = pstmt.executeUpdate();
                    // check the affected rows
                    if (affectedRows > 0) {
                        addLabel.setText("Student is added successfully :)\n ID is " + addID);
                        insetrPhone(addID);
                        name.setText("");
                        password.setText("");
                        city.setText("");
                        street.setText("");
                        phone_number.setText("");
                        setCombInitials();
                    } else {
                        addLabel.setText("Invalid Input");
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    public void insetrPhone(String addID){
        String phoneNumberValue = phone_number.getText();

        String SQL = "INSERT INTO phone_number(id, phone_number) " +
                "VALUES (?, ?);";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1,addID);
            pstmt.setString(2, phoneNumberValue);
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                addLabel.setText("Student is added successfully :)\n ID is " +addID);

                setCombInitials();
            } else {
                addLabel.setText("Invalid Input");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
