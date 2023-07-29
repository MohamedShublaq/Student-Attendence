package com.example.final_project;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class AdminOpreationsController {

    @FXML
    private void switchToOpening() throws IOException  {
        App.setRoot("opening");
        App.con = null ;
    }
    @FXML
    private void courseAction () throws IOException{
        App.setRoot("courseopreation");
    }
    @FXML 
    private void studentAction () throws IOException{
        App.setRoot("studentopreations");
    }
    @FXML
    private void tutorAction() throws IOException{
        App.setRoot("tutoropreations");
    }
    @FXML
    private void instructorAction() throws IOException{
        App.setRoot("instructoropreations");
    }

}
