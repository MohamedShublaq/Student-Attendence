package com.example.final_project;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class InstructorOpreationsConrtroller {

    @FXML
    private void switchToAdmin() throws IOException  {
        App.setRoot("adminopreatins");
    }
    @FXML
    private void AddInstructorAction () throws IOException{
        App.setRoot("addinstructoropreation");
    }
    @FXML 
    private void editInstructorAction () throws IOException{
        App.setRoot("editinstructoropreation");
    }
}
