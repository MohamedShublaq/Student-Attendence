package com.example.final_project;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class CourseOpreationConrtroller {

    @FXML
    private void switchToAdmin() throws IOException  {
        App.setRoot("adminopreatins");
    }
    @FXML
    private void AddcourseAction () throws IOException{
        App.setRoot("addcourseopreation");
    }
    @FXML 
    private void editcourseAction () throws IOException{
        App.setRoot("editcourseopreation");
    }
    @FXML
    private void AddsectionAction () throws IOException{
        App.setRoot("addsectionopreation");
    }
    @FXML
    private void EditsectionAction () throws IOException{
        App.setRoot("editsectionopreation");
    }
    @FXML
    private void AddslectureAction () throws IOException{
        App.setRoot("addlectureopreation");
    }
    @FXML
    private void EditslectureAction () throws IOException{
        App.setRoot("editlectureopreation");
    }
}
