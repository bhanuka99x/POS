module com.posapp {
    requires javafx.fxml;
    requires com.almasb.fxgl.scene;
    requires javafx.controls;
    requires java.sql;
    requires eu.hansolo.toolbox;
    requires java.desktop;
    requires itextpdf;
    requires org.apache.pdfbox;

    opens com.posapp.controllers to javafx.fxml, javafx.base;
    exports com.posapp;
}