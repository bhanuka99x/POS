module com.posapp {
    requires javafx.fxml;
    requires com.almasb.fxgl.scene;
    requires javafx.controls;
    requires java.sql;
    requires eu.hansolo.toolbox;

    opens com.posapp.controllers to javafx.fxml, javafx.base;
    exports com.posapp;
}