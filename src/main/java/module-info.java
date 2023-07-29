module com.example.final_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.naming;
    requires de.mkammerer.argon2.nolibs;
    requires org.controlsfx.controls;
    requires org.apache.commons.collections4;
    requires poi;
    requires poi.ooxml;

    opens com.example.final_project to javafx.fxml;
    exports com.example.final_project;
}