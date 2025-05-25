module com.posapp {
    requires javafx.fxml;
    requires com.almasb.fxgl.scene;
    requires javafx.controls;
    requires java.sql;
    requires eu.hansolo.toolbox;
    requires java.desktop;
    requires itextpdf;
    requires org.apache.pdfbox;
    requires mysql.connector.java;

    opens com.posapp.controllers to javafx.fxml, javafx.base;
    exports com.posapp;
}