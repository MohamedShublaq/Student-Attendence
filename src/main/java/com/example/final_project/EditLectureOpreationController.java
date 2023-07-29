
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
import javafx.scene.control.*;

public class EditLectureOpreationController implements Initializable {
    private Lecture lectures;

    @FXML
    Label addLabel;
    @FXML
    ComboBox<String> tutor;
    @FXML
    ComboBox<String> lecture;
    @FXML
    TextField room_number;
    @FXML
    TextField building;
    @FXML
    TextField title;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setComboBoxes();
    }

    @FXML
    private void switchToOpening() throws IOException {
        App.setRoot("lectureopreation");
    }

    @FXML
    private void setCombInitials() {
        tutor.setValue("Select tutor");
        lecture.setValue("Select lecture");
    }

    @FXML
    private void setComboBoxes() {
        setCombInitials();
        ArrayList<String> lectures = getLectures();
        ArrayList<String> tutors = getTutors();
        if (tutors != null) {
            ObservableList<String> observablelectures = FXCollections.observableList(lectures);
            lecture.setItems(observablelectures);
            ObservableList<String> observabletutors = FXCollections.observableList(tutors);
            tutor.setItems(observabletutors);

        } else {
            addLabel.setText("Failed to retrieve Courses/Semesters/Instructors from the database.");
        }
    }

    public ArrayList<String> getLectures() {
        String sql = "select lec_id from lecture;";
        ArrayList<String> lectures = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                lectures.add(rs.getString(1));
            }
            return lectures;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getTutors() {
        String sql = "select name from teacherassistant;";
        ArrayList<String> tutors = new ArrayList<>();
        try (Statement st = App.con.createStatement();
             ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                tutors.add(rs.getString(1));
            }
            return tutors;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    private String getTutorID() {
        String SQL = "Select id from teacherassistant where name = ?";
        String name = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, tutor.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString(1);
            }
            return name;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSecId() {
        String SQL = "Select sec_id from lecture where lec_id = ?";
        String name = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, lecture.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString(1);
            }
            return name;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getCourse() {
        String SQL = "Select course_id from lecture where lec_id = ?";
        String name = "";
        try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
            pstmt.setString(1, lecture.getValue());
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString(1);
            }
            return name;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void updateLecture() {
        String lectureValue = lecture.getValue();
        // Validate input fields
        if (lectureValue.isEmpty() || building.getText().isEmpty() || room_number.getText().isEmpty()) {
            addLabel.setText("Please fill in all fields.");
        } else {

            String SQL = "UPDATE lecture SET  title = ? , building = ?, room_number = ? "
                    + "WHERE course_id = ? AND sec_id = ? and lec_id = ?";

            String lec_id = lecture.getValue();

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, title.getText());
                pstmt.setString(2, building.getText());
                pstmt.setString(3, room_number.getText());
                pstmt.setString(4, getCourse());
                pstmt.setString(5, getSecId());
                pstmt.setString(6, lec_id);

                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {
                    addLabel.setText("Lecture is updated successfully :)");
                    updateRecord();
                    building.setText("");
                    room_number.setText("");
                    setCombInitials();
                } else {
                    addLabel.setText("Invalid Input");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void updateRecord() {
        // Validate input fields
        if (building == null || room_number == null) {
            addLabel.setText("Please fill in all fields.");
        } else {
            String SQL = "UPDATE records SET id = ? WHERE course_id = ? AND sec_id = ?";

            try (PreparedStatement pstmt = App.con.prepareStatement(SQL)) {
                pstmt.setString(1, getTutorID());
                pstmt.setString(2, getCourse());
                pstmt.setString(3, getSecId());

                int affectedRows = pstmt.executeUpdate();

                // check the affected rows
                if (affectedRows > 0) {
                    System.out.println("Done");
                } else {
                    addLabel.setText("Invalid Input");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    public void setLecture(Lecture lecture1) {
        this.lectures = lecture1;
        lecture.setValue(lecture1.getLectureId());
        title.setText(lecture1.getLectureTitle());
    }
}