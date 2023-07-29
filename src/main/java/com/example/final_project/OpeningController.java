package com.example.final_project;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
public class OpeningController {

    @FXML
    Button AdminButton;

    @FXML
    ImageView comp ;
    @FXML
    private void switchToAdmin() throws IOException {
        App.setRoot("adminlog");
    }

}

