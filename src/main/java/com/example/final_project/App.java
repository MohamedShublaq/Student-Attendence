package com.example.final_project;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class App extends Application implements Initializable {
    private static Scene scene;
    static Connection con;

    public void start(Stage stage) throws IOException {
        stage.setMinHeight(480);
        stage.setMinWidth(680);

        con = connect("databasegroup35", "databasegroup35", "postgres", "public");

        boolean databaseExists = checkIfDatabaseExists(con, "Student Attendance");
        if (!databaseExists) {
            createDatabase("Student Attendance");
            createSchema("project");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select DDL File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
            File ddlFile = fileChooser.showOpenDialog(stage);

            if (ddlFile != null) {
                String ddlFilePath = ddlFile.getAbsolutePath();
                executeDDLStatements(ddlFilePath);
            }

            // Execute insert statements from a file
            FileChooser fileChooser2 = new FileChooser();
            fileChooser2.setTitle("Select Query File");
            fileChooser2.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
            File qFile = fileChooser2.showOpenDialog(stage);

            if (qFile != null) {
                String qFilePath = qFile.getAbsolutePath();
                executeInsertStatements(qFilePath);
            }
            con = connect("auth", "12345", "Student Attendance", "project");
        } else {
            con = connect("auth", "12345", "Student Attendance", "project");
        }

        scene = new Scene(loadFXML("opening"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Student Attendance");
        stage.setResizable(false);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public void initialize(URL url, ResourceBundle rb) {
    }

    public static String checkUser() {
        String query = "SELECT current_user";
        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                String currentUser = resultSet.getString(1);
                return currentUser;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean validateAdmin(String name, String password) {
        try {
            String query = "SELECT password FROM adminstrator WHERE id = ?";
            try (PreparedStatement statement = App.con.prepareStatement(query)) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedPassword = resultSet.getString("password");
                        String encryptedPassword = encryptPassword(password);
                        String comparisonResult = crypt(password, storedPassword);

                        return comparisonResult.equals(storedPassword);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean validateTutor(String name, String password) {
        try {
            String query = "SELECT password FROM teacherassistant WHERE id = ?";
            try (PreparedStatement statement = App.con.prepareStatement(query)) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedPassword = resultSet.getString("password");
                        String encryptedPassword = encryptPassword(password);
                        String comparisonResult = crypt(password, storedPassword);

                        return comparisonResult.equals(storedPassword);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    static Connection connect(String role, String password, String databaseName, String schemaName) {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("localhost");
        source.setUser(role);
        source.setPassword(password);
        String connectionString = "jdbc:postgresql://localhost/postgres";
        connectionString = String.format("jdbc:postgresql://localhost/%s?currentSchema=%s", databaseName, schemaName);
        source.setUrl(connectionString);
        try {
            con = source.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return con;
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private static String crypt(String password, String storedPassword) {
        String query = "SELECT crypt(?, ?)";
        try (PreparedStatement statement = App.con.prepareStatement(query)) {
            statement.setString(1, password);
            statement.setString(2, storedPassword);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptPassword(String password) {
        String encryptedPassword = null;
        String query = "SELECT crypt(?, gen_salt('bf'))";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    encryptedPassword = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return encryptedPassword;
    }

    private static void createDatabase(String database) {
        String query = "CREATE DATABASE " + database;
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createSchema(String schema) {
        String query = "CREATE SCHEMA " + schema;
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeDDLStatements(String ddlFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ddlFile))) {
            StringBuilder queryBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                queryBuilder.append(line);
                if (line.endsWith(";")) {
                    String query = queryBuilder.toString();
                    try (PreparedStatement statement = con.prepareStatement(query)) {
                        statement.executeUpdate();
                    }
                    queryBuilder.setLength(0);
                } else {
                    queryBuilder.append("\n");
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeInsertStatements(String insertFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(insertFile))) {
            StringBuilder queryBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                queryBuilder.append(line);
                if (line.endsWith(";")) {
                    String query = queryBuilder.toString();
                    try (PreparedStatement statement = con.prepareStatement(query)) {
                        statement.executeUpdate();
                    }
                    queryBuilder.setLength(0);
                } else {
                    queryBuilder.append("\n");
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean checkIfDatabaseExists(Connection con, String database) {
        String query = "SELECT 1 FROM pg_database WHERE datname = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, database);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
