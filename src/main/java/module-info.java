module com.example.edp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires org.apache.commons.io;
    requires org.json;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires java.annotation;
    requires jersey.client;
    requires java.sql;

    opens com.example.edp to javafx.fxml;
    exports com.example.edp;
    exports com.example.edp.Maps;
    opens com.example.edp.Maps to javafx.fxml;
    exports com.example.edp.Weather;
    opens com.example.edp.Weather to javafx.fxml;
    exports com.example.edp.TimeZones;
    opens com.example.edp.TimeZones to javafx.fxml;
    exports com.example.edp.Database;
    opens com.example.edp.Database to javafx.fxml;
}