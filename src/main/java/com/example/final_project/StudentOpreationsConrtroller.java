package com.example.final_project;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class StudentOpreationsConrtroller {

    @FXML
    private void switchToAdmin() throws IOException  {
        if (App.checkUser().equals("tutor")){
            App.setRoot("tutoropreation");

        }else{
        App.setRoot("adminopreatins");}
    }

    @FXML
    private void AddstudentAction () throws IOException{
        App.setRoot("addstudentopreation");
    }
    @FXML
    private void AddphoneAction () throws IOException{
        App.setRoot("addphoneopreation");
    }
    @FXML 
    private void editsudentAction () throws IOException{
        App.setRoot("editstudentopreation");
    }
    @FXML
    private void registersudentAction () throws IOException{
        App.setRoot("registerstudentopreation");
    }
}
