package com.example.final_project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
public class AdminLogController implements Initializable {
    @FXML
    TextField id;
    @FXML
    PasswordField pass;
    @FXML
    Label label;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.connect("auth","12345","Student Attendance","project");
    }
    @FXML
    private void switchToOpening() throws IOException  {
        App.setRoot("opening");
        App.con=null;
    }
    @FXML
    private void login() throws IOException{
        String id1 = id.getText();
        String pass1 = pass.getText();
        if (App.validateAdmin(id1,pass1)){
            App.con=null;
            App.connect("admin","1234","Student Attendance","project");
            App.setRoot("adminopreatins");
        } else if (App.validateTutor(id1,pass1)){
            App.con=null;
            App.connect("tutor","1234","Student Attendance","project");
            App.setRoot("tutoropreation");

        }else{
            label.setText("login Failed");
        }
    }
}
