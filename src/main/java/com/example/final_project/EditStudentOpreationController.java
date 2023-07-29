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

public class EditStudentOpreationController implements Initializable {

        @FXML
        TextField street;
        @FXML
        TextField city;
        @FXML
        TextField phone;
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
        private void switchToOpening() throws IOException {
            App.setRoot("studentopreations");
        }

        private void onIDSelection() {
            String selectedID = getID();
            if (selectedID != null) {
                ArrayList<String> departments = getDepartments(selectedID);
                if (departments != null) {
                    ObservableList<String> observableDepartments = FXCollections.observableList(departments);
                    department.setItems(observableDepartments);
                    street.setText(String.valueOf(getStreet(selectedID)));
                    city.setText(String.valueOf(getCity(selectedID)));
                    phone.setText(String.valueOf(getPhone(selectedID)));
                } else {
                    addLabel.setText("Failed to retrieve information for the selected student.");
                }
            } else {
                department.getItems().clear();
            }
        }

        @FXML
        private ArrayList<String> getDepartments(String id) {
            String SQL = "SELECT dept_name FROM student WHERE id=?";
            ArrayList<String> departments = new ArrayList<>();
            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, id);
                ResultSet resultSet = pstmt.executeQuery();
                while (resultSet.next()) {
                    departments.add(resultSet.getString(1));
                }
                return departments;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @FXML
        private int getStreet(String id) {
            String SQL = "SELECT street FROM student WHERE id=?";
            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, id);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @FXML
        private int getCity(String id) {
            String SQL = "SELECT city FROM student WHERE id=?";
            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, id);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @FXML
        private int getPhone(String id) {
            String SQL = "SELECT phone_number FROM student WHERE id=?";
            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, id);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return 0;
                }
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
            ArrayList<String> ids = getIDs();
            if (departments != null && ids != null) {
                ObservableList<String> observableDepartments = FXCollections.observableList(departments);
                department.setItems(observableDepartments);
                ObservableList<String> observableIDS = FXCollections.observableList(ids);
                id.setItems(observableIDS);
            } else {
                addLabel.setText("Failed to retrieve departments/IDs from the database.");
            }
        }

        private ArrayList<String> getDepartments() {
            String sql = "SELECT dept_name FROM department;";
            ArrayList<String> departments = new ArrayList<>();
            try (Statement st = App.con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    departments.add(rs.getString(1));
                }
                return departments;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        private ArrayList<String> getIDs() {
            String sql = "SELECT name FROM student;";
            ArrayList<String> ids = new ArrayList<>();
            try (Statement st = App.con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ids.add(rs.getString(1));
                }
                return ids;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @FXML
        private void updateStudent() {
            String streetValue = street.getText();
            String cityValue = city.getText();
            String phoneValue = phone.getText();

            String departmentValue = department.getValue();
            String idValue = getID();

            // Validate input fields
            if (streetValue.isEmpty() || cityValue.isEmpty() || phoneValue.isEmpty() || departmentValue == null
                    || departmentValue.equals("Select department") || idValue == null || idValue.equals("Select id")) {
                addLabel.setText("Please fill in all fields.");
            } else {
                String SQL = "UPDATE student SET street = ?, dept_name = ?, city = ? WHERE id = ?";
                try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                    pstmt.setString(1, streetValue);
                    pstmt.setString(2, departmentValue);
                    pstmt.setString(3, cityValue);
                    pstmt.setString(4, idValue);

                    int affectedRows = pstmt.executeUpdate();
                    // check the affected rows
                    if (affectedRows > 0) {
                        addLabel.setText("Student is updated successfully :)");
                        if (!phoneValue.isEmpty()) {
                            updatePhone(phoneValue, idValue);
                        }
                        street.setText("");
                        city.setText("");
                        phone.setText("");

                        setCombInitials();
                    } else {
                        addLabel.setText("Invalid Input: Update operation failed.");
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                    addLabel.setText("Invalid Input: Database error occurred.");
                }
            }
        }

    private void updatePhone(String phone, String id) {
        String SQL = "UPDATE phone_number SET phone_number = ? WHERE id = ? AND phone_number NOT IN (SELECT phone_number FROM phone_number WHERE id = ? LIMIT 1)";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, id);
            pstmt.setString(3, id);
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                addLabel.setText("Student is updated successfully :)");
                setCombInitials();
            } else {
                addLabel.setText("Invalid Input: Update operation failed.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            addLabel.setText("Invalid Input: Database error occurred.");
        }
    }

        private String getID() {
            String SQL = "SELECT id FROM student WHERE name = ?";
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
        private void deleteStudent() {
            String idValue = getID();
            if (idValue == null || idValue.equals("Select id")) {
                addLabel.setText("Please select a student to delete.");
                return;
            }

            String SQL = "DELETE FROM student WHERE id = ?";
            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, idValue);
                int affectedRows = pstmt.executeUpdate();
                // check the affected rows
                if (affectedRows > 0) {
                    addLabel.setText("Student is deleted successfully :)");
                    setComboBoxes();
                    city.setText("");
                    street.setText("");
                    phone.setText("");
                    setCombInitials();
                } else {
                    addLabel.setText("Invalid Input: No student found with the provided ID.");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                addLabel.setText("Invalid Input: Database error occurred.");
            }
        }
    }
