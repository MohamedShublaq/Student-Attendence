package com.example.final_project;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class TutorOpreationController {

    @FXML
    private void switchToOpening() throws IOException  {
        App.setRoot("opening");
        App.con = null ;
    }
    @FXML
    private void courseAction () throws IOException{
        App.setRoot("lectureopreation");
    }
    @FXML 
    private void studentAction () throws IOException{
        App.setRoot("studentopreations");
    }
    @FXML
    private void reportAction() throws IOException{
        App.setRoot("reportstudentopreation");
    }
    @FXML
    private void attendenceAction() throws IOException{
        App.setRoot("viewlectureopreation");
    }

}
