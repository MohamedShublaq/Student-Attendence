package com.example.final_project;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class TutorOpreationsConrtroller {

    @FXML
    private void switchToAdmin() throws IOException  {
        App.setRoot("adminopreatins");
    }
    @FXML
    private void AddtutorAction () throws IOException{
        App.setRoot("addtutoropreation");
    }
    @FXML 
    private void edittutorAction () throws IOException{
        App.setRoot("edittutoropreation");
    }
    @FXML
    private void assigntutorAction () throws IOException{
        App.setRoot("assigntutoropreation");
    }
}
