package com.example.final_project;

import java.io.IOException;
import javafx.fxml.FXML;

public class LectureOpreationConrtroller {

    @FXML
    private void switchToAdmin() throws IOException  {
        App.setRoot("tutoropreation");
    }

    @FXML
    private void AddslectureAction () throws IOException{
        App.setRoot("addlectureopreation");
    }
    @FXML
    private void EditslectureAction () throws IOException{
        App.setRoot("editlectureopreation");
    }
    @FXML
    private void ViewlectureAction () throws IOException{
        App.setRoot("viewlectureopreation");
    }
}
