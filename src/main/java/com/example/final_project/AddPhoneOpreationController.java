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

public class AddPhoneOpreationController implements Initializable {
    @FXML
    TextField phone;
    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> id;

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
        id.setValue("Select id");
    }
    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> ids = getName();
        if ( ids!=null) {

            ObservableList<String> observableIDS = FXCollections.observableList(ids);
            id.setItems(observableIDS);
        } else {

            addLabel.setText("Failed to retrieve ids from the database.");
        }
    }

    public ArrayList<String> getName() {
        String sql = "select name from student;";
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
    public void insetrPhone(){
        String phoneNumberValue = phone.getText();

        String SQL = "INSERT INTO phone_number(id, phone_number) " +
                "VALUES (?, ?);";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1,getID());
            pstmt.setString(2, phoneNumberValue);
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                addLabel.setText("Phone is added successfully ");
                phone.setText("");
                setCombInitials();
            } else {
                addLabel.setText("Invalid Input");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String getID() {
        String SQL = "Select id from student where name= ?";
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
}
